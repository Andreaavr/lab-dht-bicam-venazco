package factory_method;

import java.lang.reflect.InvocationTargetException;

import local_search.acceptation_type.AcceptType;
import local_search.acceptation_type.AcceptableCandidate;


import factory_interface.IFFactoryAcceptCandidate;




/** Factoría que crea criterios de aceptación a partir de su tipo. */
public class FactoryAcceptCandidate implements IFFactoryAcceptCandidate{
	/** El criterio de aceptación. */
	private AcceptableCandidate acceptCandidate;
	
	/**
	 * Crea el criterio de aceptación indicado.
	 *
	 * @param typeacceptation tipo de criterio de aceptación
	 * @return el criterio de aceptación
	 * @throws IllegalArgumentException si algún argumento no es válido
	 * @throws SecurityException si el gestor de seguridad impide la reflexión
	 * @throws ClassNotFoundException si la clase pedida a la factoría no existe o no está permitida
	 * @throws InstantiationException si la clase pedida a la factoría no se puede instanciar
	 * @throws IllegalAccessException si el constructor de la clase no es accesible
	 * @throws InvocationTargetException si el constructor de la clase lanza una excepción
	 * @throws NoSuchMethodException si la clase no tiene constructor sin argumentos
	 */
	public AcceptableCandidate createAcceptCandidate( AcceptType typeacceptation ) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		String className = "local_search.acceptation_type." + typeacceptation.toString();
		acceptCandidate = (AcceptableCandidate) FactoryLoader.getInstance(className);
		return acceptCandidate;
	}
}
