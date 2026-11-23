package problem_operators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import problem.definition.State;
import support.BiciamTestSupport;

@DisplayName("MutationOperator")
class MutationOperatorTest {

    private static final int BITS = 12;

    @BeforeEach
    void setUp() {
        BiciamTestSupport.setUpOneMax(BITS);
    }

    @Test
    @DisplayName("generatedNewState cambia como mucho una variable de cada vecino")
    void neighbours() {
        State current = BiciamTestSupport.uniform(BITS, 0);
        List<State> neighbours = new MutationOperator().generatedNewState(current, 5);
        assertEquals(5, neighbours.size());
        for (State neighbour : neighbours) {
            assertTrue(current.Distance(neighbour) <= 1);
        }
        assertEquals(BiciamTestSupport.uniform(BITS, 0).getCode(), current.getCode());
    }

    @Test
    @DisplayName("generateRandomState genera estados completos y válidos (nunca null)")
    void randomStates() {
        List<State> states = new MutationOperator().generateRandomState(4);
        assertEquals(4, states.size());
        for (State state : states) {
            assertEquals(BITS, state.getCode().size());
            for (Object bit : state.getCode()) {
                assertTrue(bit.equals(0) || bit.equals(1));
            }
        }
    }
}
