package problem.definition;

/** Codificación abstracta de las soluciones de un problema. */
public abstract class Codification {

	/**
	 * Comprueba si un estado es válido para la codificación.
	 *
	 * @param state estado
	 * @return {@code true} si el estado es válido
	 */
	public abstract boolean validState(State state);
	/**
	 * Devuelve un valor aleatorio válido para una variable.
	 *
	 * @param key índice de la variable
	 * @return el valor generado
	 */
	public abstract Object getVariableAleatoryValue(int key);
	/**
	 * Devuelve el índice de una variable elegida al azar.
	 *
	 * @return índice de la variable
	 */
	public abstract int getAleatoryKey ();
	/**
	 * Devuelve el número de variables de la codificación.
	 *
	 * @return número de variables
	 */
	public abstract int getVariableCount();

}