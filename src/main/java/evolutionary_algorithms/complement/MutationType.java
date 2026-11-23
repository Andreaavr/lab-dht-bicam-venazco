package evolutionary_algorithms.complement;

/** Tipos de operador de mutación disponibles. */
public enum MutationType {
	/** Mutación de dos puntos: asigna valores aleatorios a dos variables elegidas al azar. */
	TowPointsMutation,
	/** Mutación de un punto: con probabilidad PM cambia el valor de una variable aleatoria. */
	OnePointMutation,
	/** Mutación por inversión para rutas TSP: invierte el tramo comprendido entre dos ciudades aleatorias. */
	AIOMutation;
}
