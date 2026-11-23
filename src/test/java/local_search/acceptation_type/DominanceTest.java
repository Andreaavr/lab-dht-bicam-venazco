package local_search.acceptation_type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import metaheurictics.strategy.Strategy;
import metaheuristics.generators.RandomSearch;
import problem.definition.Problem.ProblemType;
import problem.definition.State;
import support.BiciamTestSupport;

@DisplayName("Dominance")
class DominanceTest {

    private final Dominance dominance = new Dominance();

    @BeforeEach
    void setUp() {
        BiciamTestSupport.setUpOneMax(ProblemType.Maximizar);
        Strategy.getStrategy().generator = new RandomSearch();
    }

    @Test
    @DisplayName("Maximizar: X domina a Y si no es peor en nada y mejor en algo")
    void dominanceMaximize() {
        State x = BiciamTestSupport.evaluated(2, 3);
        State y = BiciamTestSupport.evaluated(1, 3);
        assertTrue(dominance.dominance(x, y));
        assertFalse(dominance.dominance(y, x));
        assertFalse(dominance.dominance(x, BiciamTestSupport.evaluated(2, 3)));
        assertFalse(dominance.dominance(x, BiciamTestSupport.evaluated(3, 1)));
    }

    @Test
    @DisplayName("Minimizar: el criterio se invierte")
    void dominanceMinimize() {
        Strategy.getStrategy().getProblem().setTypeProblem(ProblemType.Minimizar);
        State x = BiciamTestSupport.evaluated(1, 3);
        State y = BiciamTestSupport.evaluated(2, 3);
        assertTrue(dominance.dominance(x, y));
        assertFalse(dominance.dominance(y, x));
    }

    @Test
    @DisplayName("ListDominance añade una solución a una lista vacía")
    void listDominanceEmpty() {
        List<State> list = new ArrayList<>();
        State x = BiciamTestSupport.bits(1, 0);
        assertTrue(dominance.ListDominance(x, list));
        assertEquals(1, list.size());
    }

    @Test
    @DisplayName("ListDominance rechaza una solución dominada")
    void listDominanceRejectsDominated() {
        List<State> list = new ArrayList<>(List.of(BiciamTestSupport.evaluated(5, 5)));
        assertFalse(dominance.ListDominance(BiciamTestSupport.evaluated(1, 1), list));
        assertEquals(1, list.size());
    }

    @Test
    @DisplayName("ListDominance sustituye las soluciones que domina")
    void listDominanceReplacesDominated() {
        State weak = BiciamTestSupport.evaluated(1, 1);
        List<State> list = new ArrayList<>(List.of(weak));
        State strong = BiciamTestSupport.evaluated(5, 5);
        assertTrue(dominance.ListDominance(strong, list));
        assertEquals(1, list.size());
        assertEquals(strong.getEvaluation(), list.get(0).getEvaluation());
    }

    @Test
    @DisplayName("ListDominance no duplica una solución ya presente")
    void listDominanceNoDuplicates() {
        State a = BiciamTestSupport.bits(1, 0);
        List<State> list = new ArrayList<>(List.of(a));
        assertFalse(dominance.ListDominance(BiciamTestSupport.bits(1, 0), list));
        assertEquals(1, list.size());
        assertSame(a, list.get(0));
    }

    @Test
    @DisplayName("ListDominance mantiene soluciones no comparables")
    void listDominanceKeepsTradeOffs() {
        List<State> list = new ArrayList<>(List.of(BiciamTestSupport.evaluated(1, 5)));
        State x = BiciamTestSupport.evaluated(5, 1);
        x.getCode().add(1);
        assertTrue(dominance.ListDominance(x, list));
        assertEquals(2, list.size());
    }
}
