package demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import support.BiciamTestSupport;

@DisplayName("Aplicación de demostración")
class BiciamDemoTest {

    private final ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errBytes = new ByteArrayOutputStream();
    private final PrintStream out = new PrintStream(outBytes, true, StandardCharsets.UTF_8);
    private final PrintStream err = new PrintStream(errBytes, true, StandardCharsets.UTF_8);

    @AfterEach
    void tearDown() {
        BiciamTestSupport.resetStatics();
    }

    private String output() {
        return outBytes.toString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("Sin argumentos resuelve OneMax con Hill Climbing")
    void defaults() {
        assertEquals(0, BiciamDemo.run(new String[0], out, err));
        assertTrue(output().contains("HillClimbing"));
        assertTrue(output().contains("50 / 50"));
    }

    @Test
    @DisplayName("Acepta cada algoritmo admitido")
    void everyAlgorithm() {
        for (String algorithm : new String[] {"HillClimbing", "hillclimbingrestart", "TabuSearch",
                "SimulatedAnnealing", "RandomSearch", "LimitThreshold", "GeneticAlgorithm",
                "EvolutionStrategies"}) {
            assertEquals(0, BiciamDemo.run(new String[] {algorithm, "16", "200"}, out, err), algorithm);
        }
    }

    @Test
    @DisplayName("Muestra la ayuda con --help")
    void help() {
        assertEquals(0, BiciamDemo.run(new String[] {"--help"}, out, err));
        assertTrue(output().startsWith("Uso:"));
    }

    @Test
    @DisplayName("Rechaza algoritmos y números no válidos")
    void invalidArguments() {
        assertEquals(2, BiciamDemo.run(new String[] {"NoExiste"}, out, err));
        assertEquals(2, BiciamDemo.run(new String[] {"HillClimbing", "abc"}, out, err));
        assertEquals(2, BiciamDemo.run(new String[] {"HillClimbing", "10", "0"}, out, err));
        assertThrows(IllegalArgumentException.class, () -> BiciamDemo.parse(new String[] {"x", "2000000"}, 1, 5));
        assertEquals(5, BiciamDemo.parse(new String[0], 1, 5));
    }
}
