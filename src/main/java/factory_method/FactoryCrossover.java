package factory_method;

import java.lang.reflect.InvocationTargetException;


import evolutionary_algorithms.complement.Crossover;
import evolutionary_algorithms.complement.CrossoverType;
import factory_interface.IFFactoryCrossover;




/** Factoría que crea operadores de cruce a partir de su tipo. */
public class FactoryCrossover implements IFFactoryCrossover {
	/** El operador de cruce. */
	private Crossover crossing;

	/**
	 * Crea el operador de cruce indicado.
	 *
	 * @param Crossovertype tipo de cruce
	 * @return el operador de cruce
	 * @throws IllegalArgumentException si algún argumento no es válido
	 * @throws SecurityException si el gestor de seguridad impide la reflexión
	 * @throws ClassNotFoundException si la clase pedida a la factoría no existe o no está permitida
	 * @throws InstantiationException si la clase pedida a la factoría no se puede instanciar
	 * @throws IllegalAccessException si el constructor de la clase no es accesible
	 * @throws InvocationTargetException si el constructor de la clase lanza una excepción
	 * @throws NoSuchMethodException si la clase no tiene constructor sin argumentos
	 */
	public Crossover createCrossover(CrossoverType Crossovertype) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		String className = "evolutionary_algorithms.complement." + Crossovertype.toString();
		crossing = (Crossover) FactoryLoader.getInstance(className);
		return crossing;
	}
}
