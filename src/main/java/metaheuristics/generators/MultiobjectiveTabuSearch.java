package metaheuristics.generators;

import factory_method.FactoryAcceptCandidate;
import local_search.acceptation_type.AcceptableCandidate;
import local_search.complement.TabuSolutions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import factory_interface.IFFactoryAcceptCandidate;
import local_search.acceptation_type.AcceptType;
import local_search.candidate_type.CandidateType;
import local_search.candidate_type.CandidateValue;
import local_search.complement.StrategyType;
import metaheurictics.strategy.Strategy;
import problem.definition.Problem;
import problem.definition.State;




/** Búsqueda tabú multiobjetivo. */
public class MultiobjectiveTabuSearch extends Generator {

	/** El selector de candidatos. */
	private CandidateValue candidatevalue;
	/** El criterio de aceptación. */
	private AcceptType typeAcceptation;
	/** El modo de búsqueda. */
	private StrategyType strategy;
	/** El tipo de candidato. */
	private CandidateType typeCandidate;
	/** El estado de referencia. */
	private State stateReferenceTS;
    /** La factoría de criterios de aceptación. */
    private IFFactoryAcceptCandidate ifacceptCandidate;
    /** El tipo de generador. */
    private GeneratorType typeGenerator;
    /** La lista de estados de referencia. */
    private List<State> listStateReference = new ArrayList<State>();
    /** El peso. */
    private float weight;
	/** La traza de pesos. */
	private List<Float> listTrace = new ArrayList<Float>();

    /**
     * Devuelve el estado de referencia.
     *
     * @return el estado de referencia
     */
    public State getStateReferenceTS() {
		return stateReferenceTS;
	}

	/**
	 * Establece el estado de referencia.
	 *
	 * @param stateReferenceTS el estado de referencia
	 */
	public void setStateReferenceTS(State stateReferenceTS) {
		this.stateReferenceTS = stateReferenceTS;
	}

	/**
	 * Devuelve el tipo de generador.
	 *
	 * @return el tipo de generador
	 */
	public GeneratorType getTypeGenerator() {
		return typeGenerator;
	}

	/**
	 * Establece el tipo de generador.
	 *
	 * @param typeGenerator el tipo de generador
	 */
	public void setTypeGenerator(GeneratorType typeGenerator) {
		this.typeGenerator = typeGenerator;
	}

	/** Crea una instancia de MultiobjectiveTabuSearch. */
	public MultiobjectiveTabuSearch() {
    	super();
		this.typeAcceptation = AcceptType.AcceptNotDominatedTabu;
		this.strategy = StrategyType.TABU;
		@SuppressWarnings("unused")
		Problem problem = Strategy.getStrategy().getProblem();
		this.typeCandidate = CandidateType.RandomCandidate;
		this.candidatevalue = new CandidateValue();
		this.typeGenerator = GeneratorType.MultiobjectiveTabuSearch;
		this.weight = 50;
		listTrace.add(weight);
	}

	/**
	 * Genera un nuevo estado candidato.
	 *
	 * @param operatornumber número de vecinos o estados a generar
	 * @return el estado candidato
	 * @throws IllegalArgumentException si algún argumento no es válido
	 * @throws SecurityException si el gestor de seguridad impide la reflexión
	 * @throws ClassNotFoundException si la clase pedida a la factoría no existe o no está permitida
	 * @throws InstantiationException si la clase pedida a la factoría no se puede instanciar
	 * @throws IllegalAccessException si el constructor de la clase no es accesible
	 * @throws InvocationTargetException si el constructor de la clase lanza una excepción
	 * @throws NoSuchMethodException si la clase no tiene constructor sin argumentos
	 */
	@Override
	public State generate(Integer operatornumber) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		List<State> neighborhood = new ArrayList<State>();
		Problem problem = Strategy.getStrategy().getProblem();
		//Devuelve la lista de soluciones no dominadas de todos los vecinos posibles de stateReferenceTS que no se encuentran en la lista Tabu
		neighborhood = problem.getOperator().generatedNewState(stateReferenceTS, operatornumber);
		//Se escoge uno aleatoriamente como vecino con RandomCandidate
		State statecandidate = candidatevalue.stateCandidate(stateReferenceTS, typeCandidate, strategy, operatornumber, neighborhood);
	    return statecandidate;
	}

	/**
	 * Actualiza el estado de referencia del generador con el candidato.
	 *
	 * @param stateCandidate estado candidato
	 * @param countIterationsCurrent iteración actual
	 * @throws IllegalArgumentException si algún argumento no es válido
	 * @throws SecurityException si el gestor de seguridad impide la reflexión
	 * @throws ClassNotFoundException si la clase pedida a la factoría no existe o no está permitida
	 * @throws InstantiationException si la clase pedida a la factoría no se puede instanciar
	 * @throws IllegalAccessException si el constructor de la clase no es accesible
	 * @throws InvocationTargetException si el constructor de la clase lanza una excepción
	 * @throws NoSuchMethodException si la clase no tiene constructor sin argumentos
	 */
	@Override
	public void updateReference(State stateCandidate, Integer countIterationsCurrent)throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		ifacceptCandidate = new FactoryAcceptCandidate();
		AcceptableCandidate candidate = ifacceptCandidate.createAcceptCandidate(typeAcceptation);
		Boolean acept = candidate.acceptCandidate(stateReferenceTS, stateCandidate);
		if(acept.equals(true))
		  stateReferenceTS = stateCandidate;

		if (strategy.equals(StrategyType.TABU) && acept.equals(true)) {
			if (TabuSolutions.listTabu.size() < TabuSolutions.maxelements) {
				Boolean find = false;
				int count = 0;
				while ((TabuSolutions.listTabu.size() > count) && (find.equals(false))) {
					if (TabuSolutions.listTabu.get(count).equals(stateCandidate)) {
						find = true;
					}
					count++;
				}
				if (find.equals(false)) {
					TabuSolutions.listTabu.add(stateCandidate);
				}
			} else {
				TabuSolutions.listTabu.remove(0);
				Boolean find = false;
				int count = 0;
				while (TabuSolutions.listTabu.size() > count && find.equals(false)) {
					if (TabuSolutions.listTabu.get(count).equals(stateCandidate)) {
						find = true;
					}
					count++;
				}
				if (find.equals(false)) {
					TabuSolutions.listTabu.add(stateCandidate);
				}
			}
	}
		getReferenceList();
  }
	
	/**
	 * Devuelve el tipo de este generador.
	 *
	 * @return el tipo de generador
	 */
	@Override
	public GeneratorType getType() {
		return this.typeGenerator;
	}

	/**
	 * Devuelve la lista de estados de referencia del generador.
	 *
	 * @return la lista de referencias
	 */
	@Override
	public List<State> getReferenceList() {
		listStateReference.add(stateReferenceTS);
		return listStateReference;
	}

	/**
	 * Devuelve el estado de referencia actual del generador.
	 *
	 * @return el estado de referencia
	 */
	@Override
	public State getReference() {
		return stateReferenceTS;
	}

	/**
	 * Establece el estado de referencia inicial del generador.
	 *
	 * @param stateInitialRef estado de referencia inicial
	 */
	@Override
	public void setInitialReference(State stateInitialRef) {
		this.stateReferenceTS = stateInitialRef;
	}

	/**
	 * Establece el estado de referencia.
	 *
	 * @param stateRef estado de referencia
	 */
	public void setStateRef(State stateRef) {
		this.stateReferenceTS = stateRef;
	}
	
	/**
	 * Devuelve la lista de hijos generados.
	 *
	 * @return la lista de hijos o {@code null} si no aplica
	 */
	@Override
	public List<State> getSonList() {
		return null;
	}

	/**
	 * Establece el tipo de candidato.
	 *
	 * @param typeCandidate tipo de candidato
	 */
	public void setTypeCandidate(CandidateType typeCandidate){
		this.typeCandidate = typeCandidate;
	}

	/**
	 * Indica si el candidato mejora la referencia del generador (usado por el portafolio).
	 *
	 * @param stateCandidate estado candidato
	 * @return {@code true} si el candidato mejora la referencia
	 */
	@Override
	public boolean awardUpdateREF(State stateCandidate) {
		return false;
	}


	/**
	 * Devuelve el peso.
	 *
	 * @return el peso
	 */
	public float getWeight() {
		return this.weight;
	}

	/**
	 * Establece el peso.
	 *
	 * @param weight el peso
	 */
	@Override
	public void setWeight(float weight) {
		this.weight = weight;
	}

	/**
	 * Devuelve el número de mejoras por periodo.
	 *
	 * @return el número de mejoras por periodo
	 */
	@Override
	public int[] getListCountBetterGender() {
		return null;
	}

	/**
	 * Devuelve el número de usos por periodo.
	 *
	 * @return el número de usos por periodo
	 */
	@Override
	public int[] getListCountGender() {
		return null;
	}

	/**
	 * Devuelve la traza de pesos del generador en el portafolio.
	 *
	 * @return la traza de pesos
	 */
	@Override
	public float[] getTrace() {
		return null;
	}

}


