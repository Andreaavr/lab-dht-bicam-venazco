package problem.extension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import problem.definition.State;
import support.BiciamTestSupport;

@DisplayName("MetricasMultiobjetivo")
class MetricasMultiobjetivoTest {

    private final MetricasMultiobjetivo metrics = new MetricasMultiobjetivo();

    @Test
    @DisplayName("TasaError: fracción de soluciones fuera del frente verdadero")
    void errorRate() {
        State a = BiciamTestSupport.evaluated(1, 2);
        State b = BiciamTestSupport.evaluated(3, 4);
        List<State> current = List.of(a, b);
        List<State> trueFront = List.of(BiciamTestSupport.evaluated(1, 2));
        assertEquals(0.5, metrics.TasaError(current, trueFront), 1e-9);
        assertEquals(0.0, metrics.TasaError(trueFront, trueFront), 1e-9);
    }

    @Test
    @DisplayName("DistanciaGeneracional es 0 si ambos frentes coinciden")
    void generationalDistanceZero() {
        List<State> front = List.of(BiciamTestSupport.evaluated(1, 1), BiciamTestSupport.evaluated(2, 0));
        assertEquals(0.0, metrics.DistanciaGeneracional(front, front), 1e-9);
    }

    @Test
    @DisplayName("DistanciaGeneracional usa el punto más cercano del frente verdadero")
    void generationalDistanceNearest() {
        List<State> current = List.of(BiciamTestSupport.evaluated(1, 1));
        List<State> trueFront = List.of(BiciamTestSupport.evaluated(0, 0), BiciamTestSupport.evaluated(5, 5));
        assertEquals(Math.sqrt(2.0), metrics.DistanciaGeneracional(current, trueFront), 1e-6);
    }

    @Test
    @DisplayName("Dispersion es 0 con una sola solución y no negativa en general")
    void dispersion() {
        ArrayList<State> single = new ArrayList<>(List.of(BiciamTestSupport.evaluated(1, 1)));
        assertEquals(0.0, metrics.Dispersion(single), 1e-9);

        ArrayList<State> several = new ArrayList<>(List.of(
                BiciamTestSupport.evaluated(1, 1),
                BiciamTestSupport.evaluated(2, 3),
                BiciamTestSupport.evaluated(4, 0)));
        assertTrue(metrics.Dispersion(several) >= 0.0);
    }

    @Test
    @DisplayName("CalcularMin, CalcularMax y CalcularMedia")
    void statistics() {
        ArrayList<Double> values = new ArrayList<>(List.of(3.0, 1.0, 5.0));
        assertEquals(1.0, metrics.CalcularMin(values));
        assertEquals(5.0, metrics.CalcularMax(values));
        assertEquals(3.0, metrics.CalcularMedia(values));
    }
}
