package local_search.acceptation_type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import metaheurictics.strategy.Strategy;
import metaheuristics.generators.MultiCaseSimulatedAnnealing;
import metaheuristics.generators.RandomSearch;
import metaheuristics.generators.SimulatedAnnealing;
import problem.definition.Problem.ProblemType;
import problem.definition.State;
import support.BiciamTestSupport;

@DisplayName("Criterios de aceptación")
class AcceptanceCriteriaTest {

    private final State low = BiciamTestSupport.evaluated(1);
    private final State high = BiciamTestSupport.evaluated(5);

    @BeforeEach
    void setUp() {
        BiciamTestSupport.setUpOneMax(ProblemType.Maximizar);
        Strategy.getStrategy().generator = new RandomSearch();
    }

    private void minimize() {
        Strategy.getStrategy().getProblem().setTypeProblem(ProblemType.Minimizar);
    }

    @Test
    @DisplayName("AcceptAnyone acepta siempre")
    void acceptAnyone() {
        assertTrue(new AcceptAnyone().acceptCandidate(high, low));
    }

    @Test
    @DisplayName("AcceptBest acepta solo candidatos no peores (maximizar)")
    void acceptBestMaximize() throws Exception {
        AcceptBest accept = new AcceptBest();
        assertTrue(accept.acceptCandidate(low, high));
        assertTrue(accept.acceptCandidate(low, BiciamTestSupport.evaluated(1)));
        assertFalse(accept.acceptCandidate(high, low));
    }

    @Test
    @DisplayName("AcceptBest acepta solo candidatos no peores (minimizar)")
    void acceptBestMinimize() throws Exception {
        minimize();
        AcceptBest accept = new AcceptBest();
        assertTrue(accept.acceptCandidate(high, low));
        assertFalse(accept.acceptCandidate(low, high));
    }

    @Test
    @DisplayName("AcceptNotBad en ambos sentidos de optimización")
    void acceptNotBad() {
        AcceptNotBad accept = new AcceptNotBad();
        assertTrue(accept.acceptCandidate(low, high));
        assertFalse(accept.acceptCandidate(high, low));
        minimize();
        assertTrue(accept.acceptCandidate(high, low));
        assertFalse(accept.acceptCandidate(low, high));
    }

    @Test
    @DisplayName("AcceptNotBadT: siempre acepta mejoras y rechaza empeoramientos a temperatura ~0")
    void acceptNotBadTLowTemperature() throws Exception {
        SimulatedAnnealing.tinitial = 1e-9;
        AcceptNotBadT accept = new AcceptNotBadT();
        assertTrue(accept.acceptCandidate(low, high));
        assertFalse(accept.acceptCandidate(high, low));
        minimize();
        assertTrue(accept.acceptCandidate(high, low));
        assertFalse(accept.acceptCandidate(low, high));
    }

    @Test
    @DisplayName("AcceptNotBadT: a temperatura muy alta acepta casi cualquier candidato")
    void acceptNotBadTHighTemperature() throws Exception {
        SimulatedAnnealing.tinitial = 1e12;
        AcceptNotBadT accept = new AcceptNotBadT();
        assertTrue(accept.acceptCandidate(high, low));
        minimize();
        assertTrue(accept.acceptCandidate(low, high));
    }

    @Test
    @DisplayName("AcceptNotBadU acepta empeoramientos menores que el umbral")
    void acceptNotBadU() throws Exception {
        AcceptNotBadU accept = new AcceptNotBadU();
        Strategy.getStrategy().setThreshold(2.0);
        State slightlyWorse = BiciamTestSupport.evaluated(4);
        assertTrue(accept.acceptCandidate(high, slightlyWorse));
        assertFalse(accept.acceptCandidate(high, low));
        minimize();
        assertTrue(accept.acceptCandidate(BiciamTestSupport.evaluated(4), high));
        assertFalse(accept.acceptCandidate(low, high));
        assertEquals(2.0, Strategy.getStrategy().getThreshold());
    }

    @Test
    @DisplayName("AcceptNotDominated acepta un candidato no dominado y actualiza el frente")
    void acceptNotDominated() {
        AcceptNotDominated accept = new AcceptNotDominated();
        State current = BiciamTestSupport.evaluated(1, 1);
        State candidate = BiciamTestSupport.evaluated(2, 2);
        assertTrue(accept.acceptCandidate(current, candidate));
        List<State> front = Strategy.getStrategy().listRefPoblacFinal;
        assertEquals(1, front.size());
        assertEquals(candidate.getEvaluation(), front.get(0).getEvaluation());
    }

    @Test
    @DisplayName("AcceptNotDominated rechaza un candidato dominado por el actual")
    void acceptNotDominatedRejects() {
        AcceptNotDominated accept = new AcceptNotDominated();
        assertFalse(accept.acceptCandidate(BiciamTestSupport.evaluated(3, 3), BiciamTestSupport.evaluated(1, 1)));
    }

    @Test
    @DisplayName("AcceptNotDominatedTabu acepta siempre y actualiza el frente")
    void acceptNotDominatedTabu() {
        AcceptNotDominatedTabu accept = new AcceptNotDominatedTabu();
        assertTrue(accept.acceptCandidate(BiciamTestSupport.evaluated(3, 3), BiciamTestSupport.evaluated(1, 1)));
        assertEquals(1, Strategy.getStrategy().listRefPoblacFinal.size());
    }

    @Test
    @DisplayName("AcceptMulticase acepta un candidato que domina al actual")
    void acceptMulticaseDominating() {
        MultiCaseSimulatedAnnealing.tinitial = 1.0;
        assertTrue(new AcceptMulticase().acceptCandidate(
                BiciamTestSupport.evaluated(1, 1), BiciamTestSupport.evaluated(2, 2)));
    }

    @Test
    @DisplayName("AcceptMulticase acepta un candidato no dominado por el frente")
    void acceptMulticaseTradeOff() {
        MultiCaseSimulatedAnnealing.tinitial = 1.0;
        State current = BiciamTestSupport.evaluated(5, 1);
        State candidate = BiciamTestSupport.evaluated(1, 5);
        candidate.getCode().add(1);
        assertTrue(new AcceptMulticase().acceptCandidate(current, candidate));
        assertEquals(2, Strategy.getStrategy().listRefPoblacFinal.size());
    }

    @Test
    @DisplayName("AcceptMulticase rechaza un candidato dominado a temperatura ~0")
    void acceptMulticaseDominated() {
        MultiCaseSimulatedAnnealing.tinitial = 1e-9;
        assertFalse(new AcceptMulticase().acceptCandidate(
                BiciamTestSupport.evaluated(5, 5), BiciamTestSupport.evaluated(1, 1)));
    }

    @Test
    @DisplayName("AcceptMulticase con candidato y actual del mismo rango")
    void acceptMulticaseSameRank() {
        MultiCaseSimulatedAnnealing.tinitial = 1e12;
        List<State> front = Strategy.getStrategy().listRefPoblacFinal;
        front.add(BiciamTestSupport.evaluated(10, 10));
        State current = BiciamTestSupport.evaluated(5, 1);
        State candidate = BiciamTestSupport.evaluated(1, 5);
        assertFalse(new AcceptMulticase().acceptCandidate(current, candidate));
        assertEquals(1, front.size());
    }

    @Test
    @DisplayName("AcceptMulticase con candidato de peor rango que el actual")
    void acceptMulticaseWorseRank() {
        MultiCaseSimulatedAnnealing.tinitial = 1e12;
        List<State> front = Strategy.getStrategy().listRefPoblacFinal;
        front.add(BiciamTestSupport.evaluated(10, 3));
        front.add(BiciamTestSupport.evaluated(3, 10));
        State current = BiciamTestSupport.evaluated(2, 9);
        State candidate = BiciamTestSupport.evaluated(1, 1);
        assertFalse(new AcceptMulticase().acceptCandidate(current, candidate));
    }
}
