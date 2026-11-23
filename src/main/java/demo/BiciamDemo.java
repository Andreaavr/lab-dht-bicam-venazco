package demo;

import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import demo.onemax.OneMaxProblemFactory;
import local_search.complement.StopExecute;
import local_search.complement.TabuSolutions;
import local_search.complement.UpdateParameter;
import metaheurictics.strategy.Strategy;
import metaheuristics.generators.EvolutionStrategies;
import metaheuristics.generators.GeneratorType;
import metaheuristics.generators.GeneticAlgorithm;
import metaheuristics.generators.SimulatedAnnealing;
import evolutionary_algorithms.complement.CrossoverType;
import evolutionary_algorithms.complement.MutationType;
import evolutionary_algorithms.complement.ReplaceType;
import evolutionary_algorithms.complement.SelectionType;
import problem.definition.State;

/**
 * Aplicación de demostración de BiCIAM (punto de entrada del contenedor Docker).
 *
 * Resuelve el problema OneMax (maximizar el número de bits a 1) con la metaheurística
 * indicada y muestra la mejor solución encontrada.
 *
 * Uso: {@code java -jar biciam.jar [algoritmo] [bits] [iteraciones]}
 */
public final class BiciamDemo {

    /** Algoritmo por defecto. */
    private static final String DEFAULT_ALGORITHM = "HillClimbing";
    /** Número de bits por defecto. */
    private static final int DEFAULT_BITS = 50;
    /** Número de iteraciones por defecto. */
    private static final int DEFAULT_ITERATIONS = 1000;
    /** Límite superior de bits e iteraciones admitidos (evita consumos excesivos). */
    private static final int MAX_VALUE = 1_000_000;
    /** Número de vecinos generados por iteración en la búsqueda local. */
    private static final int NEIGHBOURS = 10;
    /** Tamaño de la población inicial de los algoritmos evolutivos. */
    private static final int POPULATION = 20;

    /** Algoritmos admitidos (sin distinguir mayúsculas) y el generador con el que arranca la estrategia. */
    private static final Map<String, GeneratorType> ALGORITHMS = algorithms();

    /** Clase de utilidades: no se instancia. */
    private BiciamDemo() {
    }

    /**
     * Construye la tabla de algoritmos admitidos.
     *
     * @return mapa inmutable cuyo orden de claves no distingue mayúsculas
     */
    private static Map<String, GeneratorType> algorithms() {
        TreeMap<String, GeneratorType> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (GeneratorType type : List.of(GeneratorType.HillClimbing, GeneratorType.HillClimbingRestart,
                GeneratorType.TabuSearch, GeneratorType.SimulatedAnnealing, GeneratorType.RandomSearch,
                GeneratorType.LimitThreshold, GeneratorType.GeneticAlgorithm, GeneratorType.EvolutionStrategies)) {
            map.put(type.name(), type);
        }
        return Collections.unmodifiableSortedMap(map);
    }

    /**
     * Punto de entrada.
     *
     * @param args algoritmo, número de bits y número de iteraciones (todos opcionales)
     */
    public static void main(String[] args) {
        int status = run(args, System.out, System.err);
        if (status != 0) {
            System.exit(status);
        }
    }

    /**
     * Ejecuta la demostración.
     *
     * @param args argumentos de la línea de órdenes
     * @param out salida para los resultados
     * @param err salida para los errores
     * @return código de salida: 0 si todo fue bien, 2 si los argumentos no son válidos, 1 si falló la ejecución
     */
    static int run(String[] args, PrintStream out, PrintStream err) {
        if (args.length > 0 && ("-h".equals(args[0]) || "--help".equals(args[0]))) {
            printUsage(out);
            return 0;
        }
        String algorithm = args.length > 0 ? args[0] : DEFAULT_ALGORITHM;
        GeneratorType type = ALGORITHMS.get(algorithm);
        if (type == null) {
            err.println("Algoritmo no admitido.");
            printUsage(err);
            return 2;
        }
        int bits;
        int iterations;
        try {
            bits = parse(args, 1, DEFAULT_BITS);
            iterations = parse(args, 2, DEFAULT_ITERATIONS);
        } catch (IllegalArgumentException e) {
            err.println(e.getMessage());
            printUsage(err);
            return 2;
        }
        try {
            State best = solve(type, bits, iterations);
            double ones = best.getEvaluation().get(0);
            out.println("BiCIAM - problema OneMax");
            out.printf(Locale.ROOT, "Algoritmo:    %s%n", type);
            out.printf(Locale.ROOT, "Bits:         %d%n", bits);
            out.printf(Locale.ROOT, "Iteraciones:  %d%n", iterations);
            out.printf(Locale.ROOT, "Mejor valor:  %.0f / %d (%.1f %%)%n", ones, bits, 100.0 * ones / bits);
            out.println("Solucion:     " + toBitString(best));
            return 0;
        } catch (ReflectiveOperationException | RuntimeException e) {
            err.println("Error durante la ejecucion: " + e.getClass().getSimpleName());
            return 1;
        } finally {
            Strategy.destroyExecute();
        }
    }

    /**
     * Configura la estrategia y ejecuta el algoritmo.
     *
     * @param type generador solicitado
     * @param bits número de bits del problema
     * @param iterations número de iteraciones
     * @return el mejor estado encontrado
     * @throws ReflectiveOperationException si falla la creación de algún generador
     */
    static State solve(GeneratorType type, int bits, int iterations) throws ReflectiveOperationException {
        Strategy.destroyExecute();
        TabuSolutions.listTabu.clear();
        TabuSolutions.maxelements = 10;
        SimulatedAnnealing.alpha = 0.93;
        SimulatedAnnealing.tinitial = 250.0;
        SimulatedAnnealing.tfinal = 41.66;
        SimulatedAnnealing.countIterationsT = 50;
        GeneticAlgorithm.countRef = 0;
        EvolutionStrategies.countRef = 0;

        GeneratorType start = type;
        if (type == GeneratorType.GeneticAlgorithm) {
            GeneticAlgorithm.countRef = POPULATION;
            GeneticAlgorithm.PC = 0.9;
            GeneticAlgorithm.PM = 0.2;
            GeneticAlgorithm.truncation = POPULATION / 2;
            GeneticAlgorithm.selectionType = SelectionType.TruncationSelection;
            GeneticAlgorithm.crossoverType = CrossoverType.UniformCrossover;
            GeneticAlgorithm.mutationType = MutationType.OnePointMutation;
            GeneticAlgorithm.replaceType = ReplaceType.SteadyStateReplace;
            start = GeneratorType.RandomSearch;
        } else if (type == GeneratorType.EvolutionStrategies) {
            EvolutionStrategies.countRef = POPULATION;
            EvolutionStrategies.PM = 0.5;
            EvolutionStrategies.truncation = POPULATION / 2;
            EvolutionStrategies.selectionType = SelectionType.TruncationSelection;
            EvolutionStrategies.mutationType = MutationType.OnePointMutation;
            EvolutionStrategies.replaceType = ReplaceType.SteadyStateReplace;
            start = GeneratorType.RandomSearch;
        }

        Strategy strategy = Strategy.getStrategy();
        strategy.setProblem(OneMaxProblemFactory.create(bits));
        strategy.setStopexecute(new StopExecute());
        strategy.setUpdateparameter(new UpdateParameter());
        strategy.executeStrategy(iterations, iterations + 1, NEIGHBOURS, start);
        return strategy.getBestState();
    }

    /**
     * Lee un argumento entero positivo.
     *
     * @param args argumentos de la línea de órdenes
     * @param index posición del argumento
     * @param defaultValue valor si el argumento no se indica
     * @return el valor leído
     * @throws IllegalArgumentException si el valor no es un entero en [1, 1000000]
     */
    static int parse(String[] args, int index, int defaultValue) {
        if (args.length <= index) {
            return defaultValue;
        }
        try {
            int value = Integer.parseInt(args[index].trim());
            if (value < 1 || value > MAX_VALUE) {
                throw new IllegalArgumentException("El argumento " + (index + 1) + " debe estar entre 1 y " + MAX_VALUE);
            }
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El argumento " + (index + 1) + " debe ser un numero entero", e);
        }
    }

    /**
     * Representa el código de un estado como cadena de bits.
     *
     * @param state estado a representar
     * @return cadena de ceros y unos
     */
    static String toBitString(State state) {
        StringBuilder sb = new StringBuilder(state.getCode().size());
        for (Object bit : state.getCode()) {
            sb.append(bit);
        }
        return sb.toString();
    }

    /**
     * Muestra la ayuda de uso.
     *
     * @param stream salida en la que escribir
     */
    private static void printUsage(PrintStream stream) {
        stream.println("Uso: java -jar biciam.jar [algoritmo] [bits] [iteraciones]");
        stream.println("Algoritmos: HillClimbing (por defecto), HillClimbingRestart, TabuSearch,");
        stream.println("            SimulatedAnnealing, RandomSearch, LimitThreshold,");
        stream.println("            GeneticAlgorithm, EvolutionStrategies");
        stream.println("Por defecto: " + DEFAULT_BITS + " bits y " + DEFAULT_ITERATIONS + " iteraciones.");
    }
}
