package demo.onemax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import problem.definition.Problem;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

@DisplayName("Problema OneMax de ejemplo")
class OneMaxTest {

    @Test
    @DisplayName("La codificación valida longitud y valores binarios")
    void codification() {
        OneMaxCodification codification = new OneMaxCodification(3);
        assertEquals(3, codification.getVariableCount());
        assertTrue(codification.validState(new State(new ArrayList<>(List.of(0, 1, 1)))));
        assertFalse(codification.validState(new State(new ArrayList<>(List.of(0, 2, 1)))));
        assertFalse(codification.validState(new State(new ArrayList<>(List.of(0, 1)))));
        assertFalse(codification.validState(null));
        int key = codification.getAleatoryKey();
        assertTrue(key >= 0 && key < 3);
        Object value = codification.getVariableAleatoryValue(key);
        assertTrue(value.equals(0) || value.equals(1));
        assertThrows(IllegalArgumentException.class, () -> new OneMaxCodification(0));
    }

    @Test
    @DisplayName("La función objetivo cuenta los unos")
    void function() {
        OneMaxFunction function = new OneMaxFunction();
        assertEquals(ProblemType.Maximizar, function.getTypeProblem());
        assertEquals(2.0, function.Evaluation(new State(new ArrayList<>(List.of(1, 0, 1)))));
    }

    @Test
    @DisplayName("El operador genera vecinos evaluados a distancia 1")
    void operator() {
        OneMaxOperator operator = new OneMaxOperator(5);
        State current = new State(new ArrayList<>(List.of(0, 0, 0, 0, 0)));
        List<State> neighbours = operator.generatedNewState(current, 4);
        assertEquals(4, neighbours.size());
        for (State neighbour : neighbours) {
            assertEquals(1.0, current.Distance(neighbour));
            assertEquals(1.0, neighbour.getEvaluation().get(0));
        }
        List<State> random = operator.generateRandomState(3);
        assertEquals(3, random.size());
        assertEquals(5, random.get(0).getCode().size());
        assertTrue(random.get(0).getEvaluation() != null);
    }

    @Test
    @DisplayName("La factoría crea un problema completo")
    void factory() {
        Problem problem = OneMaxProblemFactory.create(8);
        assertEquals(ProblemType.Maximizar, problem.getTypeProblem());
        assertEquals(8, problem.getCodification().getVariableCount());
        assertEquals(1, problem.getFunction().size());
        assertEquals(2, problem.getPossibleValue());
    }
}
