package evolutionary_algorithms.complement;

import java.util.List;

import problem.definition.State;


/** Operador abstracto de selección de padres. */
public abstract class FatherSelection {
	
	/**
	 * Selecciona padres de la población.
	 *
	 * @param listState población de estados
	 * @param truncation número de padres a seleccionar
	 * @return la lista de padres seleccionados
	 */
	public abstract List<State> selection(List<State> listState, int truncation);

}
