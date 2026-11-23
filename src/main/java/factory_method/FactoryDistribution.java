package factory_method;

import java.lang.reflect.InvocationTargetException;


import evolutionary_algorithms.complement.Distribution;
import evolutionary_algorithms.complement.DistributionType;
import factory_interface.IFFactoryDistribution;




/** Factoría que crea distribuciones de probabilidad a partir de su tipo. */
public class FactoryDistribution implements IFFactoryDistribution {
	/** La distribución de probabilidad. */
	private Distribution distribution;

	/**
	 * Crea la distribución de probabilidad indicada.
	 *
	 * @param distributiontype tipo de distribución
	 * @return la distribución
	 * @throws IllegalArgumentException si algún argumento no es válido
	 * @throws SecurityException si el gestor de seguridad impide la reflexión
	 * @throws ClassNotFoundException si la clase pedida a la factoría no existe o no está permitida
	 * @throws InstantiationException si la clase pedida a la factoría no se puede instanciar
	 * @throws IllegalAccessException si el constructor de la clase no es accesible
	 * @throws InvocationTargetException si el constructor de la clase lanza una excepción
	 * @throws NoSuchMethodException si la clase no tiene constructor sin argumentos
	 */
	public Distribution createDistribution(DistributionType distributiontype) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		String className = "evolutionary_algorithms.complement." + distributiontype.toString();
		distribution = (Distribution) FactoryLoader.getInstance(className);
		return distribution;
	}
}
