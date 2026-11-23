package evolutionary_algorithms.complement;


import problem.definition.State;

/** Operador de cruce abstracto de los algoritmos evolutivos. */
public abstract class Crossover {
	
	/**
	 * Cruza dos padres y obtiene un hijo.
	 *
	 * @param father1 primer padre
	 * @param father2 segundo padre
	 * @param PC probabilidad de cruce
	 * @return el hijo generado
	 */
	public abstract State crossover(State father1, State father2, double PC);
}
