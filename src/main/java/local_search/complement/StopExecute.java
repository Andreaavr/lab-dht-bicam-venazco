package local_search.complement;


/** Condición de parada por número máximo de iteraciones. */
public class StopExecute {
		
	/**
	 * Indica si se ha alcanzado el número máximo de iteraciones.
	 *
	 * @param countIterationsCurrent iteración actual
	 * @param countmaxIterations número máximo de iteraciones
	 * @return {@code true} si hay que detener la ejecución
	 */
	public Boolean stopIterations(int countIterationsCurrent, int countmaxIterations) {
		if (countIterationsCurrent < countmaxIterations) {
			return false;
		} else {
			return true;
		}
	}
}
