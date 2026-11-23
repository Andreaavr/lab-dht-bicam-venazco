package factory_interface;

import java.lang.reflect.InvocationTargetException;

import evolutionary_algorithms.complement.Sampling;
import evolutionary_algorithms.complement.SamplingType;




/** Interfaz de la factoría de operadores de muestreo. */
public interface IFFSampling {
	/**
	 * Crea el operador de muestreo indicado.
	 *
	 * @param typesampling tipo de muestreo
	 * @return el operador de muestreo
	 * @throws IllegalArgumentException si algún argumento no es válido
	 * @throws SecurityException si el gestor de seguridad impide la reflexión
	 * @throws ClassNotFoundException si la clase pedida a la factoría no existe o no está permitida
	 * @throws InstantiationException si la clase pedida a la factoría no se puede instanciar
	 * @throws IllegalAccessException si el constructor de la clase no es accesible
	 * @throws InvocationTargetException si el constructor de la clase lanza una excepción
	 * @throws NoSuchMethodException si la clase no tiene constructor sin argumentos
	 */
	Sampling createSampling(SamplingType typesampling) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;
}
