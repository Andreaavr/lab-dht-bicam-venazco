package evolutionary_algorithms.complement;

import java.util.List;

import problem.definition.State;


/** Estimador abstracto de la distribución de probabilidad de una población (EDA). */
public abstract class Distribution {
	/**
	 * Estima la distribución de probabilidad de los padres.
	 *
	 * @param fathers padres seleccionados
	 * @return la lista de probabilidades por variable y valor
	 */
	public abstract List<Probability> distribution(List<State> fathers);

}
