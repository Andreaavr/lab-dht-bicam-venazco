package problem.extension;

import problem.definition.State;

/** Método abstracto de evaluación de soluciones con varias funciones objetivo. */
public abstract class SolutionMethod {

	/**
	 * Evalúa un estado con todas las funciones objetivo y le asigna la evaluación.
	 *
	 * @param state estado
	 */
	public abstract void evaluationState(State state);
}
