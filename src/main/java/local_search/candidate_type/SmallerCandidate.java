package local_search.candidate_type;


import java.lang.reflect.InvocationTargetException;
import java.util.List;

import problem.definition.State;

/** Elige el vecino de menor evaluación. */
public class SmallerCandidate extends SearchCandidate {

	/**
	 * Elige un candidato de la vecindad.
	 *
	 * @param listNeighborhood vecindad de la que elegir
	 * @return el estado elegido
	 * @throws IllegalArgumentException si algún argumento no es válido
	 * @throws SecurityException si el gestor de seguridad impide la reflexión
	 * @throws ClassNotFoundException si la clase pedida a la factoría no existe o no está permitida
	 * @throws InstantiationException si la clase pedida a la factoría no se puede instanciar
	 * @throws IllegalAccessException si el constructor de la clase no es accesible
	 * @throws InvocationTargetException si el constructor de la clase lanza una excepción
	 * @throws NoSuchMethodException si la clase no tiene constructor sin argumentos
	 */
	@Override
	public State stateSearch(List<State> listNeighborhood) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		State stateSmaller = listNeighborhood.get(0);
		if(listNeighborhood.size() > 1){
			double counter = 0;
			double currentCount = listNeighborhood.get(0).getEvaluation().get(0);
			for (int i = 1; i < listNeighborhood.size(); i++) {
				counter = listNeighborhood.get(i).getEvaluation().get(0);
				if (counter < currentCount) {
					currentCount = counter;
					stateSmaller = listNeighborhood.get(i);
				}
				counter = 0;
			}
		}
		return stateSmaller;
	}
}