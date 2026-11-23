package local_search.complement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import metaheurictics.strategy.Strategy;
import metaheuristics.generators.GeneratorType;
import metaheuristics.generators.DistributionEstimationAlgorithm;
import metaheuristics.generators.EvolutionStrategies;
import metaheuristics.generators.GeneticAlgorithm;
import metaheuristics.generators.ParticleSwarmOptimization;
import problem.definition.State;
import support.BiciamTestSupport;

@DisplayName("Complementos de búsqueda local")
class LocalSearchComplementTest {

    @BeforeEach
    void setUp() {
        BiciamTestSupport.setUpOneMax(BiciamTestSupport.BITS);
    }

    @Test
    @DisplayName("StopExecute se detiene al alcanzar el máximo de iteraciones")
    void stopExecute() {
        StopExecute stop = new StopExecute();
        assertFalse(stop.stopIterations(4, 5));
        assertTrue(stop.stopIterations(5, 5));
        assertTrue(stop.stopIterations(6, 5));
    }

    @Test
    @DisplayName("TabuSolutions no filtra nada si la lista tabú está vacía")
    void tabuEmpty() {
        List<State> neighbours = new ArrayList<>(List.of(BiciamTestSupport.bits(1, 0)));
        assertSame(neighbours, new TabuSolutions().filterNeighborhood(neighbours));
    }

    @Test
    @DisplayName("TabuSolutions elimina los vecinos con el mismo código que un estado tabú")
    void tabuFiltersByCode() {
        TabuSolutions.listTabu.add(BiciamTestSupport.bits(1, 1));
        List<State> neighbours = new ArrayList<>(List.of(BiciamTestSupport.bits(1, 1), BiciamTestSupport.bits(0, 1)));
        List<State> filtered = new TabuSolutions().filterNeighborhood(neighbours);
        assertEquals(1, filtered.size());
        assertEquals(List.of(0, 1), filtered.get(0).getCode());
    }

    @Test
    @DisplayName("TabuSolutions lanza IllegalStateException si todos los vecinos son tabú")
    void tabuAllFiltered() {
        TabuSolutions.listTabu.add(BiciamTestSupport.bits(1, 1));
        TabuSolutions.listTabu.add(BiciamTestSupport.bits(0, 0));
        List<State> neighbours = new ArrayList<>(List.of(BiciamTestSupport.bits(1, 1), BiciamTestSupport.bits(0, 0)));
        TabuSolutions tabu = new TabuSolutions();
        assertThrows(IllegalStateException.class, () -> tabu.filterNeighborhood(neighbours));
    }

    @Test
    @DisplayName("UpdateParameter incrementa el contador de iteraciones")
    void updateParameterIncrements() throws Exception {
        assertEquals(4, UpdateParameter.updateParameter(3));
    }

    @Test
    @DisplayName("UpdateParameter cambia al algoritmo genético al llegar a countRef - 1")
    void updateParameterSwitchesGenerator() throws Exception {
        Strategy.getStrategy().initialize();
        GeneticAlgorithm.countRef = 5;
        UpdateParameter.updateParameter(3);
        assertEquals(GeneratorType.GeneticAlgorithm, Strategy.getStrategy().generator.getType());
    }

    @Test
    @DisplayName("UpdateParameter cambia a los demás generadores poblacionales")
    void updateParameterSwitchesOtherGenerators() throws Exception {
        Strategy.getStrategy().initialize();
        EvolutionStrategies.countRef = 5;
        UpdateParameter.updateParameter(3);
        assertEquals(GeneratorType.EvolutionStrategies, Strategy.getStrategy().generator.getType());

        EvolutionStrategies.countRef = 0;
        DistributionEstimationAlgorithm.countRef = 5;
        UpdateParameter.updateParameter(3);
        assertEquals(GeneratorType.DistributionEstimationAlgorithm, Strategy.getStrategy().generator.getType());

        DistributionEstimationAlgorithm.countRef = 0;
        ParticleSwarmOptimization.countRef = 5;
        UpdateParameter.updateParameter(3);
        assertEquals(GeneratorType.ParticleSwarmOptimization, Strategy.getStrategy().generator.getType());
    }

    @Test
    @DisplayName("Los enumerados de estrategia contienen TABU y NORMAL")
    void strategyType() {
        assertEquals(StrategyType.TABU, StrategyType.valueOf("TABU"));
        assertEquals(2, StrategyType.values().length);
    }
}
