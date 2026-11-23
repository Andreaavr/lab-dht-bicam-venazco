package evolutionary_algorithms.complement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import config.tspDynamic.TSPState;
import problem.definition.State;
import support.BiciamTestSupport;

@DisplayName("AIOMutation (problemas de rutas TSP)")
class AIOMutationTest {

    private static final int CITIES = 8;

    @BeforeEach
    void setUp() {
        BiciamTestSupport.setUpOneMax(CITIES);
        AIOMutation.path.clear();
    }

    private static State route() {
        ArrayList<Object> code = new ArrayList<>();
        for (int i = 0; i < CITIES; i++) {
            TSPState city = new TSPState();
            city.setIdCity(i);
            city.setValue(CITIES - i);
            code.add(city);
        }
        return new State(code);
    }

    @Test
    @DisplayName("La mutación conserva todas las ciudades (es una permutación)")
    void keepsPermutation() {
        State mutated = new AIOMutation().mutation(route(), 1.0);
        assertEquals(CITIES, mutated.getCode().size());
        Set<Integer> ids = new HashSet<>();
        for (Object city : mutated.getCode()) {
            ids.add(((TSPState) city).getIdCity());
        }
        assertEquals(CITIES, ids.size());
        assertTrue(AIOMutation.path.isEmpty());
    }

    @Test
    @DisplayName("sortedPathValue ordena la ruta por valor ascendente")
    void sortsByValue() {
        State state = route();
        new AIOMutation().sortedPathValue(state);
        for (int i = 1; i < CITIES; i++) {
            int previous = ((TSPState) state.getCode().get(i - 1)).getValue();
            int current = ((TSPState) state.getCode().get(i)).getValue();
            assertTrue(previous <= current);
        }
    }

    @Test
    @DisplayName("fillPath rellena la ruta con los índices de las variables")
    void fillPath() {
        AIOMutation.fillPath();
        assertEquals(CITIES, AIOMutation.path.size());
        assertEquals(0, AIOMutation.path.get(0));
    }
}
