package problem.extension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import problem.definition.ObjetiveFunction;
import problem.definition.Problem;
import problem.definition.Problem.ProblemType;
import problem.definition.State;
import support.BiciamTestSupport;

@DisplayName("Métodos de solución multiobjetivo")
class SolutionMethodTest {

    private Problem problem;

    @BeforeEach
    void setUp() {
        problem = BiciamTestSupport.setUpOneMax(4);
        ArrayList<ObjetiveFunction> functions = new ArrayList<>();
        functions.add(BiciamTestSupport.constant(0.8, ProblemType.Maximizar, 0.25f));
        functions.add(BiciamTestSupport.constant(0.4, ProblemType.Minimizar, 0.75f));
        problem.setFunction(functions);
    }

    @Test
    @DisplayName("FactoresPonderados en un problema de maximización")
    void weightedFactorsMaximize() {
        State state = new State();
        new FactoresPonderados().evaluationState(state);
        // 0.8 * 0.25 + (1 - 0.4) * 0.75
        assertEquals(0.65, state.getEvaluation().get(0), 1e-6);
    }

    @Test
    @DisplayName("FactoresPonderados en un problema de minimización")
    void weightedFactorsMinimize() {
        problem.setTypeProblem(ProblemType.Minimizar);
        State state = new State();
        new FactoresPonderados().evaluationState(state);
        // (1 - 0.8) * 0.25 + 0.4 * 0.75
        assertEquals(0.35, state.getEvaluation().get(0), 1e-6);
    }

    @Test
    @DisplayName("MultiObjetivoPuro en un problema de maximización")
    void pureMaximize() {
        State state = new State();
        new MultiObjetivoPuro().evaluationState(state);
        assertEquals(0.8, state.getEvaluation().get(0), 1e-6);
        assertEquals(0.6, state.getEvaluation().get(1), 1e-6);
    }

    @Test
    @DisplayName("MultiObjetivoPuro en un problema de minimización")
    void pureMinimize() {
        problem.setTypeProblem(ProblemType.Minimizar);
        State state = new State();
        new MultiObjetivoPuro().evaluationState(state);
        assertEquals(0.2, state.getEvaluation().get(0), 1e-6);
        assertEquals(0.4, state.getEvaluation().get(1), 1e-6);
    }
}
