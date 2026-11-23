package evolutionary_algorithms.complement;

/** Tipos de operador de cruce disponibles. */
public enum CrossoverType {
	/** Cruce de un punto: el hijo toma los genes de un padre hasta el punto de corte y del otro a partir de él. */
	OnePointCrossover,
	/** Cruce uniforme: cada gen del hijo procede de uno de los dos padres según una máscara aleatoria. */
	UniformCrossover;
}
