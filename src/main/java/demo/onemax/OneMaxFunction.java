package demo.onemax;

import problem.definition.ObjetiveFunction;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

/**
 * @brief Función objetivo de OneMax: número de bits a 1 (a maximizar).
 */
public class OneMaxFunction extends ObjetiveFunction {

    /** Crea la función con tipo {@link ProblemType#Maximizar} y peso 1. */
    public OneMaxFunction() {
        setTypeProblem(ProblemType.Maximizar);
        setWeight(1f);
    }

    /**
     * Cuenta los bits a 1 del estado.
     *
     * @param state estado a evaluar
     * @return número de variables con valor 1
     */
    @Override
    public Double Evaluation(State state) {
        double ones = 0;
        for (Object value : state.getCode()) {
            if (Integer.valueOf(1).equals(value)) {
                ones++;
            }
        }
        return ones;
    }
}
