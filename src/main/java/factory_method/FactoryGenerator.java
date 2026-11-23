package factory_method;


import java.lang.reflect.InvocationTargetException;

import factory_interface.IFFactoryGenerator;

import metaheuristics.generators.Generator;
import metaheuristics.generators.GeneratorType;



/** Factoría que crea generadores (metaheurísticas) a partir de su tipo. */
public class FactoryGenerator implements IFFactoryGenerator {

	/** El generador. */
	private Generator generator;
	
	/**
	 * Crea el generador indicado.
	 *
	 * @param generatorType tipo de generador
	 * @return el generador
	 * @throws IllegalArgumentException si algún argumento no es válido
	 * @throws SecurityException si el gestor de seguridad impide la reflexión
	 * @throws ClassNotFoundException si la clase pedida a la factoría no existe o no está permitida
	 * @throws InstantiationException si la clase pedida a la factoría no se puede instanciar
	 * @throws IllegalAccessException si el constructor de la clase no es accesible
	 * @throws InvocationTargetException si el constructor de la clase lanza una excepción
	 * @throws NoSuchMethodException si la clase no tiene constructor sin argumentos
	 */
	public Generator createGenerator(GeneratorType generatorType) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String className = "metaheuristics.generators." + generatorType.toString();
		generator = (Generator) FactoryLoader.getInstance(className);
		return generator;
	}
}
