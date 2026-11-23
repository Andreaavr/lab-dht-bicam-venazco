package factory_method;

import java.lang.reflect.InvocationTargetException;


import evolutionary_algorithms.complement.Mutation;
import evolutionary_algorithms.complement.MutationType;
import factory_interface.IFFactoryMutation;




/** Factoría que crea operadores de mutación a partir de su tipo. */
public class FactoryMutation implements IFFactoryMutation {
	/** El operador de mutación. */
	private Mutation mutation;

	/**
	 * Crea el operador de mutación indicado.
	 *
	 * @param typeMutation tipo de mutación
	 * @return el operador de mutación
	 * @throws IllegalArgumentException si algún argumento no es válido
	 * @throws SecurityException si el gestor de seguridad impide la reflexión
	 * @throws ClassNotFoundException si la clase pedida a la factoría no existe o no está permitida
	 * @throws InstantiationException si la clase pedida a la factoría no se puede instanciar
	 * @throws IllegalAccessException si el constructor de la clase no es accesible
	 * @throws InvocationTargetException si el constructor de la clase lanza una excepción
	 * @throws NoSuchMethodException si la clase no tiene constructor sin argumentos
	 */
	public Mutation createMutation(MutationType typeMutation) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		String className = "evolutionary_algorithms.complement." + typeMutation.toString();
		mutation = (Mutation) FactoryLoader.getInstance(className);
		return mutation;
	}
}
