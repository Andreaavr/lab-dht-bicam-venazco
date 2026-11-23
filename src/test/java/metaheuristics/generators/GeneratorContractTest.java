package metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import factory_method.FactoryGenerator;
import local_search.candidate_type.CandidateType;
import metaheurictics.strategy.Strategy;
import problem.definition.State;
import support.BiciamTestSupport;

/**
 * Comprueba que cada generador cumple el contrato común de {@link Generator}.
 */
@DisplayName("Contrato de los generadores")
class GeneratorContractTest {

    /** Generadores que pueden crearse y usarse de forma aislada. */
    private static final List<GeneratorType> TYPES = List.of(
            GeneratorType.HillClimbing, GeneratorType.TabuSearch, GeneratorType.SimulatedAnnealing,
            GeneratorType.RandomSearch, GeneratorType.LimitThreshold, GeneratorType.HillClimbingRestart,
            GeneratorType.GeneticAlgorithm, GeneratorType.EvolutionStrategies,
            GeneratorType.DistributionEstimationAlgorithm, GeneratorType.ParticleSwarmOptimization,
            GeneratorType.MultiGenerator, GeneratorType.MultiobjectiveTabuSearch,
            GeneratorType.MultiobjectiveStochasticHillClimbing, GeneratorType.MultiCaseSimulatedAnnealing,
            GeneratorType.MultiobjectiveHillClimbingRestart, GeneratorType.MultiobjectiveHillClimbingDistance);

    private Strategy strategy;

    @BeforeEach
    void setUp() throws Exception {
        BiciamTestSupport.setUpOneMax(BiciamTestSupport.BITS);
        strategy = Strategy.getStrategy();
        strategy.initialize();
    }

    @AfterEach
    void tearDown() {
        BiciamTestSupport.resetStatics();
    }

    private Generator create(GeneratorType type) throws Exception {
        return new FactoryGenerator().createGenerator(type);
    }

    @Test
    @DisplayName("Cada generador informa de su tipo")
    void types() throws Exception {
        for (GeneratorType type : TYPES) {
            assertEquals(type, create(type).getType(), type.name());
        }
    }

    @Test
    @DisplayName("Los métodos de la API común no fallan y devuelven valores coherentes")
    void commonApi() throws Exception {
        State state = BiciamTestSupport.uniform(BiciamTestSupport.BITS, 1);
        for (GeneratorType type : TYPES) {
            Generator generator = create(type);
            generator.setWeight(30f);
            float weight = generator.getWeight();
            assertTrue(weight == 30f || weight == 0f || weight == 50f, type.name());
            generator.getTrace();
            generator.getListCountBetterGender();
            generator.getListCountGender();
            generator.getSonList();
            assertFalse(generator.awardUpdateREF(state) && type == GeneratorType.HillClimbing);
            generator.setInitialReference(state);
        }
    }

    @Test
    @DisplayName("Los generadores de un punto devuelven la referencia asignada")
    void singlePointReference() throws Exception {
        State state = BiciamTestSupport.uniform(BiciamTestSupport.BITS, 1);
        for (GeneratorType type : List.of(GeneratorType.HillClimbing, GeneratorType.TabuSearch,
                GeneratorType.SimulatedAnnealing, GeneratorType.RandomSearch, GeneratorType.LimitThreshold,
                GeneratorType.HillClimbingRestart, GeneratorType.MultiobjectiveTabuSearch,
                GeneratorType.MultiobjectiveStochasticHillClimbing, GeneratorType.MultiCaseSimulatedAnnealing,
                GeneratorType.MultiobjectiveHillClimbingRestart, GeneratorType.MultiobjectiveHillClimbingDistance)) {
            Generator generator = create(type);
            generator.setInitialReference(state);
            assertSame(state, generator.getReference(), type.name());
            assertTrue(generator.getReferenceList().stream().anyMatch(state::Comparator), type.name());
        }
    }

    @Test
    @DisplayName("Los generadores monoobjetivo mantienen la traza de pesos y los contadores")
    void monoObjectiveTrace() throws Exception {
        for (GeneratorType type : List.of(GeneratorType.HillClimbing, GeneratorType.TabuSearch,
                GeneratorType.SimulatedAnnealing, GeneratorType.RandomSearch, GeneratorType.LimitThreshold,
                GeneratorType.HillClimbingRestart, GeneratorType.GeneticAlgorithm,
                GeneratorType.EvolutionStrategies, GeneratorType.DistributionEstimationAlgorithm)) {
            Generator generator = create(type);
            assertNotNull(generator.getTrace(), type.name());
            assertEquals(10, generator.getListCountGender().length, type.name());
            assertEquals(10, generator.getListCountBetterGender().length, type.name());
        }
    }

    @Test
    @DisplayName("Los setters específicos de los generadores de búsqueda local")
    void specificSetters() {
        State state = BiciamTestSupport.uniform(BiciamTestSupport.BITS, 0);

        HillClimbing hc = new HillClimbing();
        hc.setStateRef(state);
        hc.setGeneratorType(GeneratorType.HillClimbing);
        hc.setTypeCandidate(CandidateType.SmallerCandidate);
        assertSame(state, hc.getReference());
        assertEquals(GeneratorType.HillClimbing, hc.getGeneratorType());

        HillClimbingRestart hcr = new HillClimbingRestart();
        hcr.setStateRef(state);
        hcr.setGeneratorType(GeneratorType.HillClimbingRestart);
        hcr.setTypeCandidate(CandidateType.GreaterCandidate);
        assertEquals(GeneratorType.HillClimbingRestart, hcr.getGeneratorType());

        SimulatedAnnealing sa = new SimulatedAnnealing();
        sa.setStateRef(state);
        sa.setTypeGenerator(GeneratorType.SimulatedAnnealing);
        assertEquals(GeneratorType.SimulatedAnnealing, sa.getTypeGenerator());

        TabuSearch ts = new TabuSearch();
        ts.setStateRef(state);
        ts.setTypeGenerator(GeneratorType.TabuSearch);
        assertEquals(GeneratorType.TabuSearch, ts.getTypeGenerator());

        RandomSearch rs = new RandomSearch();
        rs.setTypeGenerator(GeneratorType.RandomSearch);
        assertEquals(GeneratorType.RandomSearch, rs.getTypeGenerator());

        LimitThreshold lt = new LimitThreshold();
        lt.setTypeCandidate(CandidateType.GreaterCandidate);
        lt.setInitialReference(state);
        assertSame(state, lt.getReference());
    }

    @Test
    @DisplayName("El algoritmo genético expone su población y elige el mejor individuo")
    void geneticPopulation() throws Exception {
        GeneticAlgorithm ga = (GeneticAlgorithm) create(GeneratorType.GeneticAlgorithm);
        List<State> population = new ArrayList<>(List.of(BiciamTestSupport.bits(0, 0), BiciamTestSupport.bits(1, 1)));
        ga.setListState(population);
        assertSame(population, ga.getListState());
        assertEquals(2.0, ga.getReference().getEvaluation().get(0));
        assertEquals(2, ga.getReferenceList().size());
        ga.setGeneratorType(GeneratorType.GeneticAlgorithm);
        assertEquals(GeneratorType.GeneticAlgorithm, ga.getGeneratorType());
        ga.setStateRef(population.get(0));
    }

    @Test
    @DisplayName("LimitRoulette conserva sus límites y su generador")
    void limitRoulette() {
        LimitRoulette roulette = new LimitRoulette();
        Generator generator = new RandomSearch();
        roulette.setLimitLow(0.1f);
        roulette.setLimitHigh(0.6f);
        roulette.setGenerator(generator);
        assertEquals(0.1f, roulette.getLimitLow());
        assertEquals(0.6f, roulette.getLimitHigh());
        assertSame(generator, roulette.getGenerator());
    }

    @Test
    @DisplayName("DistanceCalculateAdd acumula las distancias entre soluciones")
    void distanceCalculateAdd() {
        MultiobjectiveHillClimbingDistance.distanceSolution = new ArrayList<>();
        List<State> solutions = new ArrayList<>();
        solutions.add(BiciamTestSupport.bits(0, 0));
        MultiobjectiveHillClimbingDistance.distanceSolution.add(0.0);
        solutions.add(BiciamTestSupport.bits(1, 1));
        List<Double> distances = MultiobjectiveHillClimbingDistance.DistanceCalculateAdd(solutions);
        assertEquals(List.of(2.0, 2.0), distances);
    }
}
