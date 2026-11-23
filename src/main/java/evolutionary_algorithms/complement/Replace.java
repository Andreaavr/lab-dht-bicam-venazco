package evolutionary_algorithms.complement;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import problem.definition.State;


/** Operador abstracto de reemplazo de la población. */
public abstract class Replace {

	/**
	 * Incorpora el candidato a la población según la política de reemplazo.
	 *
	 * @param stateCandidate estado candidato
	 * @param listState población de estados
	 * @return la población actualizada
	 * @throws IllegalArgumentException si algún argumento no es válido
	 * @throws SecurityException si el gestor de seguridad impide la reflexión
	 * @throws ClassNotFoundException si la clase pedida a la factoría no existe o no está permitida
	 * @throws InstantiationException si la clase pedida a la factoría no se puede instanciar
	 * @throws IllegalAccessException si el constructor de la clase no es accesible
	 * @throws InvocationTargetException si el constructor de la clase lanza una excepción
	 * @throws NoSuchMethodException si la clase no tiene constructor sin argumentos
	 */
	public abstract List<State> replace(State stateCandidate, List<State>listState) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;
}
