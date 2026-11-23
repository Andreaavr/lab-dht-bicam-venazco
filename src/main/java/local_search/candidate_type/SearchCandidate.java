package local_search.candidate_type;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import problem.definition.State;


/** Estrategia abstracta para elegir un candidato de una vecindad. */
public abstract class SearchCandidate {
	
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
	public abstract State stateSearch(List<State> listNeighborhood) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException ;
}
