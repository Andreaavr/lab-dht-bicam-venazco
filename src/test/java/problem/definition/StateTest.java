package problem.definition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import metaheuristics.generators.GeneratorType;
import support.BiciamTestSupport;

@DisplayName("State")
class StateTest {

    @Test
    @DisplayName("El constructor por defecto crea un código vacío")
    void defaultConstructorCreatesEmptyCode() {
        State state = new State();
        assertTrue(state.getCode().isEmpty());
    }

    @Test
    @DisplayName("Getters y setters conservan los valores")
    void gettersAndSetters() {
        State state = new State();
        ArrayList<Object> code = new ArrayList<>(List.of(1, 0, 1));
        ArrayList<Double> evaluation = new ArrayList<>(List.of(2.0));
        state.setCode(code);
        state.setEvaluation(evaluation);
        state.setNumber(7);
        state.setTypeGenerator(GeneratorType.HillClimbing);

        assertSame(code, state.getCode());
        assertSame(evaluation, state.getEvaluation());
        assertEquals(7, state.getNumber());
        assertEquals(GeneratorType.HillClimbing, state.getTypeGenerator());
    }

    @Test
    @DisplayName("El constructor de copia duplica el código y conserva los metadatos")
    void copyConstructor() {
        State original = BiciamTestSupport.bits(1, 0, 1);
        original.setNumber(3);
        original.setTypeGenerator(GeneratorType.TabuSearch);

        State copy = new State(original);
        copy.getCode().set(0, 0);

        assertEquals(1, original.getCode().get(0));
        assertEquals(3, copy.getNumber());
        assertEquals(GeneratorType.TabuSearch, copy.getTypeGenerator());
        assertEquals(original.getEvaluation(), copy.getEvaluation());
    }

    @Test
    @DisplayName("getCopy() no comparte la lista de código con el original")
    void getCopyIsIndependent() {
        State original = BiciamTestSupport.bits(1, 1, 1);
        State copy = (State) original.getCopy();
        copy.getCode().set(1, 0);

        assertEquals(List.of(1, 1, 1), original.getCode());
        assertEquals(List.of(1, 0, 1), copy.getCode());
    }

    @Test
    @DisplayName("clone() devuelve una copia independiente")
    void cloneIsIndependent() {
        State original = BiciamTestSupport.bits(0, 1);
        State clone = original.clone();

        assertNotSame(original, clone);
        clone.getCode().set(0, 1);
        clone.getEvaluation().set(0, 99.0);

        assertEquals(0, original.getCode().get(0));
        assertEquals(1.0, original.getEvaluation().get(0));
    }

    @Test
    @DisplayName("clone() admite estados sin evaluar")
    void cloneWithoutEvaluation() {
        State state = new State(new ArrayList<>(List.of(1)));
        assertEquals(state.getCode(), state.clone().getCode());
    }

    @Test
    @DisplayName("Comparator() compara por el contenido del código")
    void comparatorUsesCode() {
        assertTrue(BiciamTestSupport.bits(1, 0).Comparator(BiciamTestSupport.bits(1, 0)));
        assertFalse(BiciamTestSupport.bits(1, 0).Comparator(BiciamTestSupport.bits(0, 1)));
    }

    @Test
    @DisplayName("Distance() es la distancia de Hamming")
    void distanceIsHamming() {
        State a = BiciamTestSupport.bits(1, 0, 1, 1);
        State b = BiciamTestSupport.bits(0, 0, 1, 0);
        assertEquals(2.0, a.Distance(b));
        assertEquals(0.0, a.Distance(a));
    }
}
