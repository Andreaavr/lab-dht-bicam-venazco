package local_search.acceptation_type;

import java.lang.reflect.InvocationTargetException;

import problem.definition.State;

/** Criterio abstracto que decide si un candidato sustituye a la solución actual. */
public abstract class AcceptableCandidate {
  
	/**
	 * Decide si el candidato sustituye a la solución actual.
	 *
	 * @param stateCurrent estado actual
	 * @param stateCandidate estado candidato
	 * @return {@code true} si el candidato es aceptado
	 * @throws IllegalArgumentException si algún argumento no es válido
	 * @throws SecurityException si el gestor de seguridad impide la reflexión
	 * @throws ClassNotFoundException si la clase pedida a la factoría no existe o no está permitida
	 * @throws InstantiationException si la clase pedida a la factoría no se puede instanciar
	 * @throws IllegalAccessException si el constructor de la clase no es accesible
	 * @throws InvocationTargetException si el constructor de la clase lanza una excepción
	 * @throws NoSuchMethodException si la clase no tiene constructor sin argumentos
	 */
	public abstract Boolean acceptCandidate(State stateCurrent, State stateCandidate) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException ;
}
