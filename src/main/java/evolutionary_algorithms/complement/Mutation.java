package evolutionary_algorithms.complement;

import problem.definition.State;

/** Operador de mutación abstracto de los algoritmos evolutivos. */
public abstract class Mutation {
	
	/**
	 * Muta un individuo.
	 *
	 * @param state estado
	 * @param PM probabilidad de mutación
	 * @return el individuo mutado
	 */
	public abstract State mutation (State state, double PM);

}
