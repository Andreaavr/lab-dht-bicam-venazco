package factory_method;

import java.lang.reflect.InvocationTargetException;

import local_search.candidate_type.CandidateType;
import local_search.candidate_type.SearchCandidate;

import factory_interface.IFFactoryCandidate;



/** Factoría que crea estrategias de selección de candidatos a partir de su tipo. */
public class FactoryCandidate implements IFFactoryCandidate{
	/** La estrategia de selección de candidatos. */
	private SearchCandidate searchcandidate;
	
	/**
	 * Crea la estrategia de selección de candidatos indicada.
	 *
	 * @param typeCandidate tipo de candidato
	 * @return la estrategia de selección
	 * @throws IllegalArgumentException si algún argumento no es válido
	 * @throws SecurityException si el gestor de seguridad impide la reflexión
	 * @throws ClassNotFoundException si la clase pedida a la factoría no existe o no está permitida
	 * @throws InstantiationException si la clase pedida a la factoría no se puede instanciar
	 * @throws IllegalAccessException si el constructor de la clase no es accesible
	 * @throws InvocationTargetException si el constructor de la clase lanza una excepción
	 * @throws NoSuchMethodException si la clase no tiene constructor sin argumentos
	 */
	public SearchCandidate createSearchCandidate(CandidateType typeCandidate) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String className = "local_search.candidate_type." + typeCandidate.toString();
		searchcandidate = (SearchCandidate) FactoryLoader.getInstance(className);
		return searchcandidate;
	}
}
