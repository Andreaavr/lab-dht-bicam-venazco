package problem.definition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import demo.onemax.OneMaxCodification;
import demo.onemax.OneMaxOperator;
import factory_method.FactorySolutionMethod;
import problem.definition.Problem.ProblemType;
import problem.extension.FactoresPonderados;
import problem.extension.MultiObjetivoPuro;
import problem.extension.TypeSolutionMethod;
import support.BiciamTestSupport;

@DisplayName("Problem")
class ProblemTest {

    private Problem problem;

    @BeforeEach
    void setUp() {
        problem = BiciamTestSupport.setUpOneMax(4);
    }

    @Test
    @DisplayName("Evaluate() sin método de solución usa la primera función objetivo")
    void evaluateSingleObjective() throws Exception {
        State state = new State(new ArrayList<>(List.of(1, 1, 0, 1)));
        problem.Evaluate(state);
        assertEquals(List.of(3.0), state.getEvaluation());
    }

    @Test
    @DisplayName("Evaluate() con factores ponderados agrega las funciones")
    void evaluateWeightedFactors() throws Exception {
        ArrayList<ObjetiveFunction> functions = new ArrayList<>();
        functions.add(BiciamTestSupport.constant(0.8, ProblemType.Maximizar, 0.5f));
        functions.add(BiciamTestSupport.constant(0.4, ProblemType.Minimizar, 0.5f));
        problem.setFunction(functions);
        problem.setTypeSolutionMethod(TypeSolutionMethod.FactoresPonderados);

        State state = new State();
        problem.Evaluate(state);

        // 0.8 * 0.5 + (1 - 0.4) * 0.5
        assertEquals(0.7, state.getEvaluation().get(0), 1e-6);
        assertTrue(problem.getFactorySolutionMethod() instanceof FactorySolutionMethod);
    }

    @Test
    @DisplayName("Evaluate() multiobjetivo puro devuelve una evaluación por función")
    void evaluatePureMultiobjective() throws Exception {
        ArrayList<ObjetiveFunction> functions = new ArrayList<>();
        functions.add(BiciamTestSupport.constant(0.8, ProblemType.Maximizar, 1f));
        functions.add(BiciamTestSupport.constant(0.4, ProblemType.Minimizar, 1f));
        problem.setFunction(functions);
        problem.setTypeSolutionMethod(TypeSolutionMethod.MultiObjetivoPuro);

        State state = new State();
        problem.Evaluate(state);

        assertEquals(2, state.getEvaluation().size());
        assertEquals(0.8, state.getEvaluation().get(0), 1e-6);
        assertEquals(0.6, state.getEvaluation().get(1), 1e-6);
    }

    @Test
    @DisplayName("newSolutionMethod() crea la implementación indicada")
    void newSolutionMethod() throws Exception {
        assertTrue(problem.newSolutionMethod(TypeSolutionMethod.FactoresPonderados) instanceof FactoresPonderados);
        assertTrue(problem.newSolutionMethod(TypeSolutionMethod.MultiObjetivoPuro) instanceof MultiObjetivoPuro);
    }

    @Test
    @DisplayName("Getters y setters conservan los valores")
    void gettersAndSetters() {
        Problem p = new Problem();
        OneMaxCodification codification = new OneMaxCodification(3);
        OneMaxOperator operator = new OneMaxOperator(3);
        State state = new State();
        FactorySolutionMethod factory = new FactorySolutionMethod();

        p.setCodification(codification);
        p.setOperator(operator);
        p.setState(state);
        p.setPossibleValue(5);
        p.setTypeProblem(ProblemType.Minimizar);
        p.setTypeSolutionMethod(TypeSolutionMethod.FactoresPonderados);
        p.setFactorySolutionMethod(factory);
        p.setFunction(new ArrayList<>());

        assertSame(codification, p.getCodification());
        assertSame(operator, p.getOperator());
        assertSame(state, p.getState());
        assertEquals(5, p.getPossibleValue());
        assertEquals(ProblemType.Minimizar, p.getTypeProblem());
        assertEquals(TypeSolutionMethod.FactoresPonderados, p.getTypeSolutionMethod());
        assertSame(factory, p.getFactorySolutionMethod());
        assertNotNull(p.getFunction());
    }

    @Test
    @DisplayName("ObjetiveFunction conserva tipo y peso")
    void objectiveFunctionProperties() {
        ObjetiveFunction function = BiciamTestSupport.constant(1.0, ProblemType.Minimizar, 0.25f);
        assertEquals(ProblemType.Minimizar, function.getTypeProblem());
        assertEquals(0.25f, function.getWeight());
    }
}
