package evolutionary_algorithms.complement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import metaheurictics.strategy.Strategy;
import problem.definition.Problem.ProblemType;
import problem.definition.State;
import support.BiciamTestSupport;

@DisplayName("Operadores evolutivos")
class EvolutionaryOperatorsTest {

    private static final int N = 40;

    @BeforeEach
    void setUp() {
        BiciamTestSupport.setUpOneMax(N);
    }

    private static List<State> population() {
        return new ArrayList<>(List.of(
                BiciamTestSupport.evaluated(3),
                BiciamTestSupport.evaluated(9),
                BiciamTestSupport.evaluated(1),
                BiciamTestSupport.evaluated(5)));
    }

    private static int changes(List<Object> code) {
        int changes = 0;
        for (int i = 1; i < code.size(); i++) {
            if (!code.get(i).equals(code.get(i - 1))) {
                changes++;
            }
        }
        return changes;
    }

    // ---------------------------------------------------------------- selección

    @Test
    @DisplayName("TruncationSelection (maximizar) devuelve los k mejores en orden descendente")
    void truncationMaximize() {
        List<State> selected = new TruncationSelection().selection(population(), 2);
        assertEquals(2, selected.size());
        assertEquals(9.0, selected.get(0).getEvaluation().get(0));
        assertEquals(5.0, selected.get(1).getEvaluation().get(0));
    }

    @Test
    @DisplayName("TruncationSelection (minimizar) devuelve los k mejores en orden ascendente")
    void truncationMinimize() {
        Strategy.getStrategy().getProblem().setTypeProblem(ProblemType.Minimizar);
        List<State> selected = new TruncationSelection().selection(population(), 3);
        assertEquals(1.0, selected.get(0).getEvaluation().get(0));
        assertEquals(3.0, selected.get(1).getEvaluation().get(0));
        assertEquals(5.0, selected.get(2).getEvaluation().get(0));
    }

    @Test
    @DisplayName("RouletteSelection devuelve tantos padres como individuos, todos de la población")
    void roulette() {
        List<State> population = population();
        List<State> fathers = new RouletteSelection().selection(population, 2);
        assertTrue(fathers.size() <= population.size());
        assertTrue(population.containsAll(fathers));
    }

    // ---------------------------------------------------------------- reemplazo

    @Test
    @DisplayName("SteadyStateReplace (maximizar) sustituye al peor si el candidato es mejor")
    void steadyStateMaximize() {
        List<State> population = population();
        State candidate = BiciamTestSupport.evaluated(4);
        List<State> result = new SteadyStateReplace().replace(candidate, population);
        assertSame(candidate, result.get(2));
        assertEquals(4, result.size());
    }

    @Test
    @DisplayName("SteadyStateReplace (maximizar) no sustituye si el candidato es peor")
    void steadyStateMaximizeWorse() {
        List<State> population = population();
        State candidate = BiciamTestSupport.evaluated(0);
        assertTrue(!new SteadyStateReplace().replace(candidate, population).contains(candidate));
    }

    @Test
    @DisplayName("SteadyStateReplace (minimizar) sustituye al peor (el mayor)")
    void steadyStateMinimize() {
        Strategy.getStrategy().getProblem().setTypeProblem(ProblemType.Minimizar);
        List<State> population = population();
        State candidate = BiciamTestSupport.evaluated(2);
        List<State> result = new SteadyStateReplace().replace(candidate, population);
        assertSame(candidate, result.get(1));
        State bad = BiciamTestSupport.evaluated(100);
        assertTrue(!new SteadyStateReplace().replace(bad, result).contains(bad));
    }

    @Test
    @DisplayName("MinValue y MaxValue localizan los extremos")
    void minAndMax() {
        SteadyStateReplace replace = new SteadyStateReplace();
        assertEquals(1.0, replace.MinValue(population()).getEvaluation().get(0));
        assertEquals(9.0, replace.MaxValue(population()).getEvaluation().get(0));
    }

    @Test
    @DisplayName("GenerationalReplace elimina el más antiguo y añade el candidato al final")
    void generational() throws Exception {
        List<State> population = population();
        State first = population.get(0);
        State candidate = BiciamTestSupport.evaluated(7);
        List<State> result = new GenerationalReplace().replace(candidate, population);
        assertEquals(4, result.size());
        assertTrue(!result.contains(first));
        assertSame(candidate, result.get(3));
    }

    // ---------------------------------------------------------------- cruce

    @Test
    @DisplayName("OnePointCrossover con PC=0 devuelve una copia del primer padre")
    void onePointNoCrossover() {
        State father1 = BiciamTestSupport.uniform(N, 0);
        State father2 = BiciamTestSupport.uniform(N, 1);
        State child = new OnePointCrossover().crossover(father1, father2, 0.0);
        assertEquals(father1.getCode(), child.getCode());
    }

    @Test
    @DisplayName("OnePointCrossover con PC=1 produce un hijo de un único punto de corte")
    void onePointCrossover() {
        State father1 = BiciamTestSupport.uniform(N, 0);
        State father2 = BiciamTestSupport.uniform(N, 1);
        for (int i = 0; i < 50; i++) {
            State child = new OnePointCrossover().crossover(father1, father2, 1.0);
            assertEquals(N, child.getCode().size());
            assertEquals(1, changes(child.getCode()));
        }
        assertEquals(BiciamTestSupport.uniform(N, 0).getCode(), father1.getCode());
        assertEquals(BiciamTestSupport.uniform(N, 1).getCode(), father2.getCode());
    }

    @Test
    @DisplayName("UniformCrossover toma cada gen de uno de los padres y mezcla ambos")
    void uniformCrossover() {
        State father1 = BiciamTestSupport.uniform(N, 0);
        State father2 = BiciamTestSupport.uniform(N, 1);
        State child = new UniformCrossover().crossover(father1, father2, 1.0);
        assertTrue(child.getCode().contains(0));
        assertTrue(child.getCode().contains(1));
        assertEquals(BiciamTestSupport.uniform(N, 0).getCode(), father1.getCode());
    }

    @Test
    @DisplayName("La máscara del cruce uniforme se rellena en todas sus posiciones")
    void uniformMask() {
        int[] mask = new UniformCrossover().mascara(200);
        int ones = 0;
        for (int value : mask) {
            assertTrue(value == 0 || value == 1);
            ones += value;
        }
        assertTrue(ones > 0 && ones < 200);
    }

    // ---------------------------------------------------------------- mutación

    @Test
    @DisplayName("OnePointMutation con PM=0 no modifica el individuo")
    void onePointMutationOff() {
        State state = BiciamTestSupport.uniform(N, 0);
        new OnePointMutation().mutation(state, 0.0);
        assertEquals(BiciamTestSupport.uniform(N, 0).getCode(), state.getCode());
    }

    @Test
    @DisplayName("OnePointMutation con PM=1 cambia como mucho un gen")
    void onePointMutationOn() {
        State state = BiciamTestSupport.uniform(N, 0);
        State reference = BiciamTestSupport.uniform(N, 0);
        new OnePointMutation().mutation(state, 1.0);
        assertTrue(reference.Distance(state) <= 1);
    }

    @Test
    @DisplayName("TowPointsMutation cambia como mucho dos genes")
    void twoPointsMutation() {
        State state = BiciamTestSupport.uniform(N, 0);
        State reference = BiciamTestSupport.uniform(N, 0);
        new TowPointsMutation().mutation(state, 1.0);
        assertTrue(reference.Distance(state) <= 2);
        assertEquals(N, state.getCode().size());
    }

    // ---------------------------------------------------------------- EDA

    @Test
    @DisplayName("Univariate calcula la frecuencia de cada valor por variable")
    void univariate() {
        List<State> fathers = List.of(BiciamTestSupport.bits(0, 1), BiciamTestSupport.bits(0, 0));
        List<Probability> probabilities = new Univariate().distribution(fathers);
        assertEquals(3, probabilities.size());
        assertEquals(0, probabilities.get(0).getKey());
        assertEquals(0, probabilities.get(0).getValue());
        assertEquals(1.0f, probabilities.get(0).getProbability());
        assertEquals(0.5f, probabilities.get(1).getProbability());
        assertEquals(0.5f, probabilities.get(2).getProbability());
    }

    @Test
    @DisplayName("Univariate.getListKey devuelve las claves del mapa")
    void univariateKeys() {
        TreeMap<String, Object> map = new TreeMap<>();
        map.put("x0", 1);
        map.put("x1", 2);
        assertEquals(List.of("x0", "x1"), new Univariate().getListKey(map));
    }

    @Test
    @DisplayName("ProbabilisticSampling genera individuos con valores observados en los padres")
    void probabilisticSampling() {
        List<State> fathers = List.of(BiciamTestSupport.bits(1, 1, 1), BiciamTestSupport.bits(1, 1, 1));
        List<State> sons = new ProbabilisticSampling().sampling(fathers, 4);
        assertEquals(4, sons.size());
        for (State son : sons) {
            assertEquals(List.of(1, 1, 1), son.getCode());
        }
    }

    @Test
    @DisplayName("Probability y Range conservan sus valores")
    void probabilityAndRange() {
        Probability probability = new Probability();
        probability.setKey("k");
        probability.setValue(3);
        probability.setProbability(0.25f);
        Range range = new Range();
        range.setData(probability);
        range.setMin(0.1f);
        range.setMax(0.9f);
        assertEquals("k", probability.getKey());
        assertEquals(3, probability.getValue());
        assertEquals(0.25f, probability.getProbability());
        assertSame(probability, range.getData());
        assertEquals(0.1f, range.getMin());
        assertEquals(0.9f, range.getMax());
    }
}
