package factory_method;

import java.lang.reflect.InvocationTargetException;

import problem.extension.SolutionMethod;
import problem.extension.TypeSolutionMethod;
import factory_interface.IFFactorySolutionMethod;

/** Factoría que crea métodos de solución multiobjetivo a partir de su tipo. */
public class FactorySolutionMethod implements IFFactorySolutionMethod {

	/** El método de solución. */
	private SolutionMethod solutionMethod;
	
	/**
	 * Crea el método de solución multiobjetivo indicado.
	 *
	 * @param method tipo de método de solución
	 * @return el método de solución
	 * @throws IllegalArgumentException si algún argumento no es válido
	 * @throws SecurityException si el gestor de seguridad impide la reflexión
	 * @throws ClassNotFoundException si la clase pedida a la factoría no existe o no está permitida
	 * @throws InstantiationException si la clase pedida a la factoría no se puede instanciar
	 * @throws IllegalAccessException si el constructor de la clase no es accesible
	 * @throws InvocationTargetException si el constructor de la clase lanza una excepción
	 * @throws NoSuchMethodException si la clase no tiene constructor sin argumentos
	 */
	@Override
	public SolutionMethod createdSolutionMethod(TypeSolutionMethod method) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String className = "problem.extension." + method.toString();
		solutionMethod = (SolutionMethod) FactoryLoader.getInstance(className);
		return solutionMethod;
	}

}
