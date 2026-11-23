package evolutionary_algorithms.complement;

import java.util.List;

import problem.definition.State;


/** Operador abstracto de muestreo de nuevos individuos (EDA). */
public abstract class Sampling {
	/**
	 * Genera nuevos individuos a partir de los padres.
	 *
	 * @param fathers padres seleccionados
	 * @param countInd número de individuos a generar
	 * @return la lista de individuos generados
	 */
	public abstract List<State> sampling (List<State> fathers, int countInd);
}
