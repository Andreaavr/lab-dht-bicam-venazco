package demo.onemax;

import problem.definition.Codification;
import problem.definition.State;
import util.RandomUtil;

/**
 * Codificación binaria del problema OneMax.
 *
 * Cada estado es un vector de {@code n} variables con valores 0 o 1.
 */
public class OneMaxCodification extends Codification {

    /** Número de variables (bits) de cada solución. */
    private final int variableCount;

    /**
     * Crea la codificación.
     *
     * @param variableCount número de bits; debe ser positivo
     */
    public OneMaxCodification(int variableCount) {
        if (variableCount <= 0) {
            throw new IllegalArgumentException("El número de variables debe ser positivo");
        }
        this.variableCount = variableCount;
    }

    /**
     * Comprueba que el estado tiene la longitud correcta y solo contiene 0 y 1.
     *
     * @param state estado a validar
     * @return {@code true} si el estado es válido
     */
    @Override
    public boolean validState(State state) {
        if (state == null || state.getCode().size() != variableCount) {
            return false;
        }
        for (Object value : state.getCode()) {
            if (!Integer.valueOf(0).equals(value) && !Integer.valueOf(1).equals(value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Devuelve un valor aleatorio (0 o 1) para una variable.
     *
     * @param key índice de la variable (no influye en el valor)
     * @return 0 o 1
     */
    @Override
    public Object getVariableAleatoryValue(int key) {
        return RandomUtil.nextInt(2);
    }

    /**
     * Devuelve el índice de una variable elegida al azar.
     *
     * @return índice en [0, n)
     */
    @Override
    public int getAleatoryKey() {
        return RandomUtil.nextInt(variableCount);
    }

    /**
     * Devuelve el número de variables.
     *
     * @return número de bits de cada solución
     */
    @Override
    public int getVariableCount() {
        return variableCount;
    }
}
