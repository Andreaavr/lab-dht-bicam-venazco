package support;

import java.util.ArrayList;
import java.util.Arrays;

import demo.onemax.OneMaxFunction;
import demo.onemax.OneMaxProblemFactory;
import evolutionary_algorithms.complement.CrossoverType;
import evolutionary_algorithms.complement.MutationType;
import evolutionary_algorithms.complement.ReplaceType;
import evolutionary_algorithms.complement.SelectionType;
import local_search.complement.StopExecute;
import local_search.complement.TabuSolutions;
import local_search.complement.UpdateParameter;
import metaheurictics.strategy.Strategy;
import metaheuristics.generators.DistributionEstimationAlgorithm;
import metaheuristics.generators.EvolutionStrategies;
import metaheuristics.generators.GeneticAlgorithm;
import metaheuristics.generators.MultiCaseSimulatedAnnealing;
import metaheuristics.generators.ParticleSwarmOptimization;
import metaheuristics.generators.RandomSearch;
import metaheuristics.generators.SimulatedAnnealing;
import problem.definition.ObjetiveFunction;
import problem.definition.Problem;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

/**
 * Utilidades comunes de las pruebas: preparan la estrategia (singleton) con un
 * problema OneMax y restauran el estado estático que BiCIAM comparte entre ejecuciones.
 */
public final class BiciamTestSupport {

    /** Número de variables por defecto del problema OneMax de las pruebas. */
    public static final int BITS = 20;

    private BiciamTestSupport() {
    }

    /**
     * Reinicia la estrategia y la configura con un problema OneMax de maximización.
     *
     * @param bits número de variables
     * @return el problema asignado a la estrategia
     */
    public static Problem setUpOneMax(int bits) {
        resetStatics();
        Problem problem = OneMaxProblemFactory.create(bits);
        Strategy strategy = Strategy.getStrategy();
        strategy.setProblem(problem);
        strategy.setStopexecute(new StopExecute());
        strategy.setUpdateparameter(new UpdateParameter());
        return problem;
    }

    /**
     * Igual que {@link #setUpOneMax(int)} pero fijando el tipo de problema.
     *
     * @param type maximizar o minimizar
     * @return el problema asignado a la estrategia
     */
    public static Problem setUpOneMax(ProblemType type) {
        Problem problem = setUpOneMax(BITS);
        problem.setTypeProblem(type);
        return problem;
    }

    /** Restaura todos los parámetros estáticos de los generadores a valores conocidos. */
    public static void resetStatics() {
        Strategy.destroyExecute();
        RandomSearch.listStateReference = new ArrayList<>();
        TabuSolutions.listTabu.clear();
        TabuSolutions.maxelements = 10;

        SimulatedAnnealing.alpha = 0.93;
        SimulatedAnnealing.tinitial = 250.0;
        SimulatedAnnealing.tfinal = 41.66;
        SimulatedAnnealing.countIterationsT = 50;
        MultiCaseSimulatedAnnealing.alpha = 0.93;
        MultiCaseSimulatedAnnealing.tinitial = 250.0;
        MultiCaseSimulatedAnnealing.tfinal = 41.66;
        MultiCaseSimulatedAnnealing.countIterationsT = 50;

        GeneticAlgorithm.countRef = 0;
        GeneticAlgorithm.PC = 0.9;
        GeneticAlgorithm.PM = 0.1;
        GeneticAlgorithm.truncation = 5;
        GeneticAlgorithm.mutationType = MutationType.OnePointMutation;
        GeneticAlgorithm.crossoverType = CrossoverType.UniformCrossover;
        GeneticAlgorithm.replaceType = ReplaceType.SteadyStateReplace;
        GeneticAlgorithm.selectionType = SelectionType.TruncationSelection;

        EvolutionStrategies.countRef = 0;
        EvolutionStrategies.PM = 0.1;
        EvolutionStrategies.truncation = 5;
        EvolutionStrategies.mutationType = MutationType.OnePointMutation;
        EvolutionStrategies.replaceType = ReplaceType.SteadyStateReplace;
        EvolutionStrategies.selectionType = SelectionType.TruncationSelection;

        DistributionEstimationAlgorithm.countRef = 0;
        ParticleSwarmOptimization.coutSwarm = 0;
        ParticleSwarmOptimization.countParticleBySwarm = 0;
        ParticleSwarmOptimization.countRef = 0;
    }

    /**
     * Crea un estado con la evaluación indicada y un código vacío.
     *
     * @param evaluations valores de las funciones objetivo
     * @return estado evaluado
     */
    public static State evaluated(double... evaluations) {
        State state = new State();
        ArrayList<Double> list = new ArrayList<>();
        for (double value : evaluations) {
            list.add(value);
        }
        state.setEvaluation(list);
        return state;
    }

    /**
     * Crea un estado OneMax a partir de sus bits y lo evalúa.
     *
     * @param bits valores 0/1
     * @return estado evaluado con el número de unos
     */
    public static State bits(Integer... bits) {
        State state = new State(new ArrayList<Object>(Arrays.asList((Object[]) bits)));
        ArrayList<Double> evaluation = new ArrayList<>();
        evaluation.add(new OneMaxFunction().Evaluation(state));
        state.setEvaluation(evaluation);
        return state;
    }

    /**
     * Crea un estado de {@code n} bits iguales.
     *
     * @param n número de bits
     * @param value valor de todos los bits
     * @return estado evaluado
     */
    public static State uniform(int n, int value) {
        Integer[] code = new Integer[n];
        Arrays.fill(code, value);
        return bits(code);
    }

    /**
     * Función objetivo que devuelve siempre el mismo valor.
     *
     * @param value valor devuelto
     * @param type sentido de optimización de la función
     * @param weight peso en los métodos de agregación
     * @return función objetivo constante
     */
    public static ObjetiveFunction constant(double value, ProblemType type, float weight) {
        ObjetiveFunction function = new ObjetiveFunction() {
            @Override
            public Double Evaluation(State state) {
                return value;
            }
        };
        function.setTypeProblem(type);
        function.setWeight(weight);
        return function;
    }
}
