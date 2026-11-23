package factory_interface;

import java.lang.reflect.InvocationTargetException;

import evolutionary_algorithms.complement.Distribution;
import evolutionary_algorithms.complement.DistributionType;




/** Interfaz de la factoría de distribuciones de probabilidad. */
public interface IFFactoryDistribution {
	/**
	 * Crea la distribución de probabilidad indicada.
	 *
	 * @param typedistribution tipo de distribución
	 * @return la distribución
	 * @throws IllegalArgumentException si algún argumento no es válido
	 * @throws SecurityException si el gestor de seguridad impide la reflexión
	 * @throws ClassNotFoundException si la clase pedida a la factoría no existe o no está permitida
	 * @throws InstantiationException si la clase pedida a la factoría no se puede instanciar
	 * @throws IllegalAccessException si el constructor de la clase no es accesible
	 * @throws InvocationTargetException si el constructor de la clase lanza una excepción
	 * @throws NoSuchMethodException si la clase no tiene constructor sin argumentos
	 */
	Distribution createDistribution(DistributionType typedistribution) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;
}
