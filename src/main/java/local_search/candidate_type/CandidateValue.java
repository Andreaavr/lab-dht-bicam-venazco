package local_search.candidate_type;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import problem.definition.State;
import local_search.complement.StrategyType;
import local_search.complement.TabuSolutions;
import metaheurictics.strategy.Strategy;
import factory_interface.IFFactoryCandidate;
import factory_method.FactoryCandidate;





/**
 * Obtiene el candidato de una vecindad aplicando la estrategia de búsqueda y, si procede, la lista tabú.
 */
public class CandidateValue {

	/** El modo de búsqueda. */
	private StrategyType strategy;

	/** La factoría de candidatos. */
	private IFFactoryCandidate ifFactory;

	/** El tipo de candidato. */
	private CandidateType typecand;

	/** La lista tabú. */
	private TabuSolutions tabusolution;

	/** La estrategia de selección de candidatos. */
	private SearchCandidate searchcandidate;

	/** Crea una instancia de CandidateValue. */
	public CandidateValue(){}

	/**
	 * Crea una instancia de CandidateValue con los valores indicados.
	 *
	 * @param strategy modo de búsqueda (normal o tabú)
	 * @param ifFactory la factoría de candidatos
	 * @param typecand el tipo de candidato
	 * @param tabusolution la lista tabú
	 * @param searchcandidate la estrategia de selección de candidatos
	 */
	public CandidateValue(StrategyType strategy, IFFactoryCandidate ifFactory, CandidateType typecand, 
			TabuSolutions tabusolution, SearchCandidate searchcandidate) { //, Strategy executegenerator
		super();
		this.strategy = strategy;
		this.ifFactory = ifFactory;
		this.typecand = typecand;
		this.tabusolution = tabusolution;
		this.searchcandidate = searchcandidate;
	}

	/**
	 * Crea la estrategia de selección de candidatos indicada.
	 *
	 * @param typecandidate tipo de candidato
	 * @return la estrategia creada
	 * @throws IllegalArgumentException si algún argumento no es válido
	 * @throws SecurityException si el gestor de seguridad impide la reflexión
	 * @throws ClassNotFoundException si la clase pedida a la factoría no existe o no está permitida
	 * @throws InstantiationException si la clase pedida a la factoría no se puede instanciar
	 * @throws IllegalAccessException si el constructor de la clase no es accesible
	 * @throws InvocationTargetException si el constructor de la clase lanza una excepción
	 * @throws NoSuchMethodException si la clase no tiene constructor sin argumentos
	 */
	public SearchCandidate newSearchCandidate(CandidateType typecandidate) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		ifFactory = new FactoryCandidate();
		searchcandidate = ifFactory.createSearchCandidate(typecandidate);
		return searchcandidate;
	}

	/**
	 * Obtiene el candidato de la vecindad según el tipo de candidato y la estrategia.
	 *
	 * @param stateCurrent estado actual
	 * @param typeCandidate tipo de candidato
	 * @param strategy modo de búsqueda (normal o tabú)
	 * @param operatornumber número de vecinos o estados a generar
	 * @param neighborhood vecindad del estado actual
	 * @return el estado candidato
	 * @throws IllegalArgumentException si algún argumento no es válido
	 * @throws SecurityException si el gestor de seguridad impide la reflexión
	 * @throws ClassNotFoundException si la clase pedida a la factoría no existe o no está permitida
	 * @throws InstantiationException si la clase pedida a la factoría no se puede instanciar
	 * @throws IllegalAccessException si el constructor de la clase no es accesible
	 * @throws InvocationTargetException si el constructor de la clase lanza una excepción
	 * @throws NoSuchMethodException si la clase no tiene constructor sin argumentos
	 */
	public State stateCandidate(State stateCurrent, CandidateType typeCandidate, StrategyType strategy, Integer operatornumber, List<State> neighborhood) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		State stateCandidate;
		List<State> auxList = new ArrayList<State>();
		for (int i = 0; i < neighborhood.size(); i++) {
			auxList.add(neighborhood.get(i));
		}
		this.tabusolution = new TabuSolutions();
		if (strategy.equals(StrategyType.TABU)) {
			try {
				auxList = this.tabusolution.filterNeighborhood(auxList);
			}
			catch (IllegalStateException e) {
				Strategy strategys = Strategy.getStrategy();
				if(strategys.hasProblem()){
					neighborhood = strategys.getProblem().getOperator().generatedNewState(neighborhood.get(0), operatornumber);
				}
				return stateCandidate(stateCurrent, typeCandidate, strategy, operatornumber, neighborhood);
			}
		}
		SearchCandidate searchCand = newSearchCandidate(typeCandidate);
		stateCandidate = searchCand.stateSearch(auxList);
		return stateCandidate;
	}

	/**
	 * Devuelve la lista tabú.
	 *
	 * @return la lista tabú
	 */
	public TabuSolutions getTabusolution() {
		return tabusolution;
	}

	/**
	 * Establece la lista tabú.
	 *
	 * @param tabusolution la lista tabú
	 */
	public void setTabusolution(TabuSolutions tabusolution) {
		this.tabusolution = tabusolution;
	}

	/**
	 * Devuelve el modo de búsqueda con el que se configuró el selector.
	 *
	 * @return el modo de búsqueda (normal o tabú), o {@code null} si no se configuró
	 */
	public StrategyType getStrategyType() {
		return strategy;
	}

	/**
	 * Devuelve el tipo de candidato con el que se configuró el selector.
	 *
	 * @return el tipo de candidato, o {@code null} si no se configuró
	 */
	public CandidateType getTypeCandidate() {
		return typecand;
	}
}
