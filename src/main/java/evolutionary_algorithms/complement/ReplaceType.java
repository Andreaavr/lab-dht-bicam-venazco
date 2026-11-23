package evolutionary_algorithms.complement;

/** Tipos de operador de reemplazo disponibles. */
public enum ReplaceType {
	/** Reemplazo de estado estacionario: el candidato sustituye al peor individuo si lo mejora. */
	SteadyStateReplace,
	/** Reemplazo generacional: elimina el individuo más antiguo e incorpora el candidato. */
	GenerationalReplace;
}
