package factory_interface;

import java.lang.reflect.InvocationTargetException;

import metaheuristics.generators.Generator;
import metaheuristics.generators.GeneratorType;

/** Interfaz de la factoría de generadores (metaheurísticas). */
public interface IFFactoryGenerator {
	
	/**
	 * Crea el generador indicado.
	 *
	 * @param Generatortype tipo de generador
	 * @return el generador
	 * @throws IllegalArgumentException si algún argumento no es válido
	 * @throws SecurityException si el gestor de seguridad impide la reflexión
	 * @throws ClassNotFoundException si la clase pedida a la factoría no existe o no está permitida
	 * @throws InstantiationException si la clase pedida a la factoría no se puede instanciar
	 * @throws IllegalAccessException si el constructor de la clase no es accesible
	 * @throws InvocationTargetException si el constructor de la clase lanza una excepción
	 * @throws NoSuchMethodException si la clase no tiene constructor sin argumentos
	 */
	Generator createGenerator(GeneratorType Generatortype)throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException ;
}
