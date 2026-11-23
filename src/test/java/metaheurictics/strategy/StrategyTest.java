package metaheurictics.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import local_search.complement.StopExecute;
import local_search.complement.UpdateParameter;
import metaheuristics.generators.EvolutionStrategies;
import metaheuristics.generators.GeneratorType;
import metaheuristics.generators.GeneticAlgorithm;
import problem.definition.Problem;
import problem.definition.Problem.ProblemType;
import problem.definition.State;
import support.BiciamTestSupport;

@DisplayName("Strategy")
class StrategyTest {

    private static final int ITERATIONS = 300;
    private static final int NEIGHBOURS = 5;

    private Strategy strategy;

    @BeforeEach
    void setUp() {
        BiciamTestSupport.setUpOneMax(BiciamTestSupport.BITS);
        strategy = Strategy.getStrategy();
    }

    @AfterEach
    void tearDown() {
        BiciamTestSupport.resetStatics();
    }

    private double best() {
        return strategy.getBestState().getEvaluation().get(0);
    }

    private void run(GeneratorType type) throws Exception {
        strategy.executeStrategy(ITERATIONS, ITERATIONS * 10, NEIGHBOURS, type);
        assertNotNull(strategy.getBestState());
        assertEquals(ITERATIONS, strategy.getCountCurrent());
        assertNotNull(strategy.listBest);
    }

    @Test
    @DisplayName("getStrategy() devuelve siempre la misma instancia hasta destroyExecute()")
    void singleton() {
        assertSame(strategy, Strategy.getStrategy());
        Strategy.destroyExecute();
        assertNotSame(strategy, Strategy.getStrategy());
    }

    @Test
    @DisplayName("Hill Climbing resuelve OneMax de 20 bits")
    void hillClimbing() throws Exception {
        run(GeneratorType.HillClimbing);
        assertEquals(BiciamTestSupport.BITS, best());
    }

    @Test
    @DisplayName("Hill Climbing también funciona en minimización")
    void hillClimbingMinimize() throws Exception {
        strategy.getProblem().setTypeProblem(ProblemType.Minimizar);
        run(GeneratorType.HillClimbing);
        assertEquals(0.0, best());
    }

    @Test
    @DisplayName("Hill Climbing con reinicio y umbral límite resuelven OneMax")
    void hillClimbingVariants() throws Exception {
        run(GeneratorType.HillClimbingRestart);
        assertEquals(BiciamTestSupport.BITS, best());
        BiciamTestSupport.setUpOneMax(BiciamTestSupport.BITS);
        strategy = Strategy.getStrategy();
        run(GeneratorType.LimitThreshold);
        assertTrue(best() >= BiciamTestSupport.BITS - 2);
    }

    @Test
    @DisplayName("Búsqueda tabú, recocido simulado y búsqueda aleatoria mejoran el estado inicial")
    void otherLocalSearches() throws Exception {
        for (GeneratorType type : List.of(GeneratorType.TabuSearch, GeneratorType.SimulatedAnnealing,
                GeneratorType.RandomSearch)) {
            BiciamTestSupport.setUpOneMax(BiciamTestSupport.BITS);
            strategy = Strategy.getStrategy();
            run(type);
            assertTrue(best() >= BiciamTestSupport.BITS / 2.0, type + " no mejoró: " + best());
        }
    }

    @Test
    @DisplayName("El algoritmo genético se activa tras la fase aleatoria (countRef)")
    void geneticAlgorithm() throws Exception {
        GeneticAlgorithm.countRef = 10;
        run(GeneratorType.RandomSearch);
        assertEquals(GeneratorType.GeneticAlgorithm, strategy.generator.getType());
        assertTrue(best() >= BiciamTestSupport.BITS / 2.0);
    }

    @Test
    @DisplayName("Las estrategias evolutivas se activan tras la fase aleatoria (countRef)")
    void evolutionStrategies() throws Exception {
        EvolutionStrategies.countRef = 10;
        run(GeneratorType.RandomSearch);
        assertEquals(GeneratorType.EvolutionStrategies, strategy.generator.getType());
    }

    @Test
    @DisplayName("Algoritmos multiobjetivo con una sola función")
    void multiobjective() throws Exception {
        for (GeneratorType type : List.of(GeneratorType.MultiobjectiveStochasticHillClimbing,
                GeneratorType.MultiobjectiveHillClimbingRestart, GeneratorType.MultiobjectiveHillClimbingDistance,
                GeneratorType.MultiobjectiveTabuSearch, GeneratorType.MultiCaseSimulatedAnnealing)) {
            BiciamTestSupport.setUpOneMax(BiciamTestSupport.BITS);
            strategy = Strategy.getStrategy();
            run(type);
            assertFalse(strategy.listRefPoblacFinal.isEmpty(), type.name());
        }
    }

    @Test
    @DisplayName("Guarda estados, mejores estados, frente de Pareto y tiempo si se pide")
    void saveOptions() throws Exception {
        strategy.saveListStates = true;
        strategy.saveListBestStates = true;
        strategy.saveFreneParetoMonoObjetivo = true;
        strategy.calculateTime = true;
        run(GeneratorType.HillClimbing);
        assertEquals(ITERATIONS + 1, strategy.listStates.size());
        assertFalse(strategy.listRefPoblacFinal.isEmpty());
        assertNotNull(strategy.notDominated);
        assertTrue(Strategy.timeExecute >= 0);
    }

    @Test
    @DisplayName("Los cambios periódicos (problemas dinámicos) actualizan la referencia")
    void dynamicChanges() throws Exception {
        strategy.saveListStates = true;
        strategy.saveListBestStates = true;
        strategy.executeStrategy(200, 50, NEIGHBOURS, GeneratorType.HillClimbing);
        assertTrue(strategy.countChange > 50);
        assertTrue(strategy.listOfflineError[0] > 0);
    }

    @Test
    @DisplayName("Los cambios periódicos reevalúan la población del algoritmo genético")
    void dynamicChangesGenetic() throws Exception {
        GeneticAlgorithm.countRef = 10;
        strategy.executeStrategy(200, 50, NEIGHBOURS, GeneratorType.RandomSearch);
        assertEquals(GeneratorType.GeneticAlgorithm, strategy.generator.getType());
    }

    @Test
    @DisplayName("initialize() crea un generador por cada tipo y getListKey() los enumera")
    void initializeAndKeys() throws Exception {
        strategy.initialize();
        assertEquals(GeneratorType.values().length, strategy.mapGenerators.size());
        List<String> keys = strategy.getListKey();
        assertEquals(GeneratorType.values().length, keys.size());
        assertTrue(keys.contains("HillClimbing"));
        strategy.initializeGenerators();
        assertEquals(GeneratorType.values().length, strategy.mapGenerators.size());
    }

    @Test
    @DisplayName("update() cambia al generador poblacional al llegar a su countRef")
    void updateSwitchesGenerator() throws Exception {
        strategy.initialize();
        EvolutionStrategies.countRef = 3;
        strategy.update(2);
        assertEquals(GeneratorType.EvolutionStrategies, strategy.generator.getType());
    }

    @Test
    @DisplayName("Getters y setters de la estrategia")
    void gettersAndSetters() {
        Problem problem = strategy.getProblem();
        StopExecute stop = new StopExecute();
        UpdateParameter update = new UpdateParameter();
        State state = BiciamTestSupport.bits(1);

        strategy.setStopexecute(stop);
        strategy.setUpdateparameter(update);
        strategy.setBestState(state);
        strategy.setCountMax(7);
        strategy.setCountCurrent(3);
        strategy.setThreshold(1.5);

        assertSame(problem, strategy.getProblem());
        assertSame(stop, strategy.getStopexecute());
        assertSame(update, strategy.getUpdateparameter());
        assertSame(state, strategy.getBestState());
        assertEquals(7, strategy.getCountMax());
        assertEquals(3, strategy.getCountCurrent());
        assertEquals(1.5, strategy.getThreshold());
    }

    @Test
    @DisplayName("calculateOffLinePerformance divide la suma entre el periodo de cambio")
    void offlinePerformance() {
        strategy.countPeriodChange = 4;
        strategy.calculateOffLinePerformance(10f, 2);
        assertEquals(2.5f, strategy.listOfflineError[2]);
    }
}
