package local_search.candidate_type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import local_search.complement.StrategyType;
import local_search.complement.TabuSolutions;
import problem.definition.State;
import support.BiciamTestSupport;

@DisplayName("Selección de candidatos")
class CandidateTypesTest {

    private final State one = BiciamTestSupport.evaluated(1);
    private final State three = BiciamTestSupport.evaluated(3);
    private final State two = BiciamTestSupport.evaluated(2);

    @BeforeEach
    void setUp() {
        BiciamTestSupport.setUpOneMax(BiciamTestSupport.BITS);
    }

    @Test
    @DisplayName("GreaterCandidate devuelve el vecino de mayor evaluación")
    void greater() throws Exception {
        assertSame(three, new GreaterCandidate().stateSearch(List.of(one, three, two)));
    }

    @Test
    @DisplayName("GreaterCandidate con un único vecino lo devuelve")
    void greaterSingle() throws Exception {
        assertSame(one, new GreaterCandidate().stateSearch(List.of(one)));
    }

    @Test
    @DisplayName("GreaterCandidate elige al azar si ninguno mejora al primero")
    void greaterFallbackRandom() throws Exception {
        List<State> list = List.of(three, one, two);
        assertTrue(list.contains(new GreaterCandidate().stateSearch(list)));
    }

    @Test
    @DisplayName("SmallerCandidate devuelve el vecino de menor evaluación")
    void smaller() throws Exception {
        assertSame(one, new SmallerCandidate().stateSearch(List.of(three, one, two)));
        assertSame(two, new SmallerCandidate().stateSearch(List.of(two)));
    }

    @Test
    @DisplayName("SmallerCandidate nunca devuelve null si el primero ya es el menor")
    void smallerFirstIsBest() throws Exception {
        assertSame(one, new SmallerCandidate().stateSearch(List.of(one, three, two)));
    }

    @Test
    @DisplayName("RandomCandidate puede devolver cualquier vecino, incluido el último")
    void randomReachesAll() {
        List<State> list = List.of(one, two, three);
        Set<State> seen = new HashSet<>();
        RandomCandidate random = new RandomCandidate();
        for (int i = 0; i < 500; i++) {
            seen.add(random.stateSearch(list));
        }
        assertEquals(3, seen.size());
    }

    @Test
    @DisplayName("NotDominatedCandidate devuelve el único vecino")
    void notDominatedSingle() throws Exception {
        State s = BiciamTestSupport.uniform(BiciamTestSupport.BITS, 1);
        assertSame(s, new NotDominatedCandidate().stateSearch(List.of(s)));
    }

    @Test
    @DisplayName("NotDominatedCandidate avanza hacia el vecino que domina")
    void notDominatedAdvances() throws Exception {
        State worse = BiciamTestSupport.uniform(BiciamTestSupport.BITS, 0);
        State better = BiciamTestSupport.uniform(BiciamTestSupport.BITS, 1);
        assertSame(better, new NotDominatedCandidate().stateSearch(List.of(worse, better)));
    }

    @Test
    @DisplayName("CandidateValue crea el buscador del tipo pedido")
    void candidateValueFactory() throws Exception {
        CandidateValue value = new CandidateValue();
        assertTrue(value.newSearchCandidate(CandidateType.GreaterCandidate) instanceof GreaterCandidate);
        assertTrue(value.newSearchCandidate(CandidateType.SmallerCandidate) instanceof SmallerCandidate);
        assertTrue(value.newSearchCandidate(CandidateType.RandomCandidate) instanceof RandomCandidate);
        assertTrue(value.newSearchCandidate(CandidateType.NotDominatedCandidate) instanceof NotDominatedCandidate);
    }

    @Test
    @DisplayName("CandidateValue en modo NORMAL aplica el criterio sobre la vecindad")
    void candidateValueNormal() throws Exception {
        CandidateValue value = new CandidateValue(StrategyType.NORMAL, null, CandidateType.GreaterCandidate,
                new TabuSolutions(), new GreaterCandidate());
        State result = value.stateCandidate(one, CandidateType.GreaterCandidate, StrategyType.NORMAL, 3,
                List.of(one, three, two));
        assertSame(three, result);
        assertSame(StrategyType.NORMAL, value.getStrategyType());
        assertSame(CandidateType.GreaterCandidate, value.getTypeCandidate());
        assertNotNull(value.getTabusolution());
        TabuSolutions tabu = new TabuSolutions();
        value.setTabusolution(tabu);
        assertSame(tabu, value.getTabusolution());
    }

    @Test
    @DisplayName("CandidateValue en modo TABU descarta los vecinos tabú")
    void candidateValueTabu() throws Exception {
        State best = BiciamTestSupport.uniform(BiciamTestSupport.BITS, 1);
        State other = BiciamTestSupport.uniform(BiciamTestSupport.BITS, 0);
        TabuSolutions.listTabu.add(BiciamTestSupport.uniform(BiciamTestSupport.BITS, 1));
        State result = new CandidateValue().stateCandidate(other, CandidateType.GreaterCandidate,
                StrategyType.TABU, 2, new java.util.ArrayList<>(List.of(other, best)));
        assertSame(other, result);
    }

    @Test
    @DisplayName("CandidateValue regenera la vecindad si todos los vecinos son tabú")
    void candidateValueAllTabu() throws Exception {
        State only = BiciamTestSupport.uniform(BiciamTestSupport.BITS, 0);
        TabuSolutions.listTabu.add(BiciamTestSupport.uniform(BiciamTestSupport.BITS, 0));
        State result = new CandidateValue().stateCandidate(only, CandidateType.GreaterCandidate,
                StrategyType.TABU, 3, new java.util.ArrayList<>(List.of(only)));
        assertNotNull(result);
        assertEquals(1.0, only.Distance(result));
    }
}
