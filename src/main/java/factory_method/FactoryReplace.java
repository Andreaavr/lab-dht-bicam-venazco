package factory_method;

import java.lang.reflect.InvocationTargetException;


import evolutionary_algorithms.complement.Replace;
import evolutionary_algorithms.complement.ReplaceType;
import factory_interface.IFFactoryReplace;





/** Factoría que crea operadores de reemplazo a partir de su tipo. */
public class FactoryReplace implements IFFactoryReplace {

/** El operador de reemplazo. */
private Replace replace;
	
	/**
	 * Crea el operador de reemplazo indicado.
	 *
	 * @param typereplace tipo de reemplazo
	 * @return el operador de reemplazo
	 * @throws IllegalArgumentException si algún argumento no es válido
	 * @throws SecurityException si el gestor de seguridad impide la reflexión
	 * @throws ClassNotFoundException si la clase pedida a la factoría no existe o no está permitida
	 * @throws InstantiationException si la clase pedida a la factoría no se puede instanciar
	 * @throws IllegalAccessException si el constructor de la clase no es accesible
	 * @throws InvocationTargetException si el constructor de la clase lanza una excepción
	 * @throws NoSuchMethodException si la clase no tiene constructor sin argumentos
	 */
	public Replace createReplace( ReplaceType typereplace ) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		String className = "evolutionary_algorithms.complement." + typereplace.toString();
		replace = (Replace) FactoryLoader.getInstance(className);
		return replace;
	}
}
