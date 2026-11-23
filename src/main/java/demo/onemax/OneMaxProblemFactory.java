package demo.onemax;

import java.util.ArrayList;

import problem.definition.ObjetiveFunction;
import problem.definition.Problem;
import problem.definition.Problem.ProblemType;

/**
 * Construye instancias de {@link Problem} para OneMax.
 */
public final class OneMaxProblemFactory {

    /** Clase de utilidades: no se instancia. */
    private OneMaxProblemFactory() {
    }

    /**
     * Crea un problema OneMax de maximización.
     *
     * @param variableCount número de bits de cada solución
     * @return problema listo para asignarse a la estrategia
     */
    public static Problem create(int variableCount) {
        Problem problem = new Problem();
        ArrayList<ObjetiveFunction> functions = new ArrayList<>();
        functions.add(new OneMaxFunction());
        problem.setFunction(functions);
        problem.setTypeProblem(ProblemType.Maximizar);
        problem.setCodification(new OneMaxCodification(variableCount));
        problem.setOperator(new OneMaxOperator(variableCount));
        problem.setPossibleValue(2);
        return problem;
    }
}
