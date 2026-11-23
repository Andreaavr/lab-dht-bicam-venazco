package demo.onemax;

import java.util.ArrayList;
import java.util.List;

import problem.definition.Operator;
import problem.definition.State;
import util.RandomUtil;

/**
 * Operador de vecindad de OneMax: invierte un bit elegido al azar.
 */
public class OneMaxOperator extends Operator {

    /** Función usada para evaluar los estados generados. */
    private final OneMaxFunction function = new OneMaxFunction();

    /** Número de variables de cada solución. */
    private final int variableCount;

    /**
     * Crea el operador.
     *
     * @param variableCount número de bits de cada solución
     */
    public OneMaxOperator(int variableCount) {
        this.variableCount = variableCount;
    }

    /**
     * Genera vecinos del estado actual invirtiendo un bit aleatorio en cada uno.
     *
     * @param stateCurrent estado de partida (no se modifica)
     * @param operatornumber número de vecinos a generar
     * @return lista de vecinos
     */
    @Override
    public List<State> generatedNewState(State stateCurrent, Integer operatornumber) {
        List<State> neighbours = new ArrayList<>();
        for (int i = 0; i < operatornumber; i++) {
            State neighbour = (State) stateCurrent.getCopy();
            int key = RandomUtil.nextInt(variableCount);
            int bit = (Integer) neighbour.getCode().get(key);
            neighbour.getCode().set(key, 1 - bit);
            evaluate(neighbour);
            neighbours.add(neighbour);
        }
        return neighbours;
    }

    /**
     * Genera estados completamente aleatorios.
     *
     * @param operatornumber número de estados a generar
     * @return lista de estados aleatorios
     */
    @Override
    public List<State> generateRandomState(Integer operatornumber) {
        List<State> states = new ArrayList<>();
        for (int i = 0; i < operatornumber; i++) {
            ArrayList<Object> code = new ArrayList<>();
            for (int j = 0; j < variableCount; j++) {
                code.add(RandomUtil.nextInt(2));
            }
            State state = new State(code);
            evaluate(state);
            states.add(state);
        }
        return states;
    }

    /**
     * Asigna al estado su evaluación, ya que los algoritmos de búsqueda local
     * de BiCIAM comparan los vecinos por su evaluación.
     *
     * @param state estado a evaluar
     */
    private void evaluate(State state) {
        ArrayList<Double> evaluation = new ArrayList<>();
        evaluation.add(function.Evaluation(state));
        state.setEvaluation(evaluation);
    }
}
