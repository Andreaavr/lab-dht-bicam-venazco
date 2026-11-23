package metaheuristics.generators;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import factory_method.FactoryAcceptCandidate;
import local_search.acceptation_type.AcceptableCandidate;
import metaheurictics.strategy.Strategy;

import java.util.ArrayList;
import java.util.List;

import factory_interface.IFFactoryAcceptCandidate;

import problem.definition.State;

import local_search.acceptation_type.AcceptType;
import local_search.candidate_type.CandidateType;
import local_search.candidate_type.CandidateValue;
import local_search.complement.StrategyType;




/** Ascensión de colinas multiobjetivo con reinicio. */
public class MultiobjectiveHillClimbingRestart extends Generator{

	/** El selector de candidatos. */
	protected CandidateValue candidatevalue;
	/** El criterio de aceptación. */
	protected AcceptType typeAcceptation;
	/** El modo de búsqueda. */
	protected StrategyType strategy;
	/** El tipo de candidato. */
	protected CandidateType typeCandidate;
	/** El estado de referencia. */
	protected State stateReferenceHC;
	/** La factoría de criterios de aceptación. */
	protected IFFactoryAcceptCandidate ifacceptCandidate;
	/** El tipo de generador. */
	protected GeneratorType Generatortype;
	/** La lista de estados de referencia. */
	protected List<State> listStateReference = new ArrayList<State>(); 
	/** El peso. */
	protected float weight;
	/** La traza de pesos. */
	protected List<Float> listTrace = new ArrayList<Float>();
	/** Los estados visitados. */
	private List<State> visitedState = new ArrayList<State>();
	/** El tamaño de la vecindad. */
	public static int sizeNeighbors;


	/** Crea una instancia de MultiobjectiveHillClimbingRestart. */
	public MultiobjectiveHillClimbingRestart() {
		super();
		this.typeAcceptation = AcceptType.AcceptNotDominated;
		this.strategy = StrategyType.NORMAL;
		this.typeCandidate = CandidateType.NotDominatedCandidate;
		this.candidatevalue = new CandidateValue();
		this.Generatortype = GeneratorType.MultiobjectiveHillClimbingRestart;
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
		neighborhood = Strategy.getStrategy().getProblem().getOperator().generatedNewState(stateReferenceHC, operatornumber);
		State statecandidate = candidatevalue.stateCandidate(stateReferenceHC, typeCandidate, strategy, operatornumber, neighborhood);
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
	public void updateReference(State stateCandidate, Integer countIterationsCurrent) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		//Agregando la primera solución a la lista de soluciones no dominadas

		if(Strategy.getStrategy().listRefPoblacFinal.size() == 0){
			Strategy.getStrategy().listRefPoblacFinal.add(stateReferenceHC.clone());
		}

		ifacceptCandidate = new FactoryAcceptCandidate();
		AcceptableCandidate candidate = ifacceptCandidate.createAcceptCandidate(typeAcceptation);
		State lastState = Strategy.getStrategy().listRefPoblacFinal.get(Strategy.getStrategy().listRefPoblacFinal.size()-1);
		List<State> neighborhood = new ArrayList<State>();
		neighborhood = Strategy.getStrategy().getProblem().getOperator().generatedNewState(stateReferenceHC, sizeNeighbors);
		int i= 0;

		Boolean accept = candidate.acceptCandidate(lastState, stateCandidate.clone());

		if(accept.equals(true)){
			stateReferenceHC = stateCandidate.clone();
			visitedState = new ArrayList<State>();
			lastState = stateCandidate.clone();
			//tomar xc q pertenesca a la vecindad de xa
		}
		else{
			boolean stop = false;
			while (i < neighborhood.size()&& !stop) {
				if (!Contain(neighborhood.get(i))) {
					stateCandidate = neighborhood.get(i);
					Strategy.getStrategy().getProblem().Evaluate(stateCandidate);  
					visitedState.add(stateCandidate);
					accept = candidate.acceptCandidate(lastState, stateCandidate.clone());
					stop=true;
				}
				i++;
			}
			while (!stop) {
				stateCandidate = Strategy.getStrategy().getProblem().getOperator().generateRandomState(1).get(0);
				if (!Contain(stateCandidate)) {
					Strategy.getStrategy().getProblem().Evaluate(stateCandidate);  
					stop=true;
					accept = candidate.acceptCandidate(lastState, stateCandidate.clone());

				}
			}
			if(accept.equals(true)){
				stateReferenceHC = stateCandidate.clone();
				visitedState = new ArrayList<State>();
				lastState = stateCandidate.clone();
				//tomar xc q pertenesca a la vecindad de xa
			}
		}


		getReferenceList();
	}

	/**
	 * Devuelve la lista de estados de referencia del generador.
	 *
	 * @return la lista de referencias
	 */
	@Override
	public List<State> getReferenceList() {
		listStateReference.add(stateReferenceHC.clone());
		return listStateReference;
	}

	/**
	 * Devuelve el estado de referencia actual del generador.
	 *
	 * @return el estado de referencia
	 */
	@Override
	public State getReference() {
		return stateReferenceHC;
	}

	/**
	 * Establece el estado de referencia.
	 *
	 * @param stateRef estado de referencia
	 */
	public void setStateRef(State stateRef) {
		this.stateReferenceHC = stateRef;
	}

	/**
	 * Establece el estado de referencia inicial del generador.
	 *
	 * @param stateInitialRef estado de referencia inicial
	 */
	@Override
	public void setInitialReference(State stateInitialRef) {
		this.stateReferenceHC = stateInitialRef;
	}

	/**
	 * Devuelve el tipo de generador.
	 *
	 * @return el tipo de generador
	 */
	public GeneratorType getGeneratorType() {
		return Generatortype;
	}

	/**
	 * Establece el tipo de generador.
	 *
	 * @param Generatortype tipo de generador
	 */
	public void setGeneratorType(GeneratorType Generatortype) {
		this.Generatortype = Generatortype;
	}

	/**
	 * Devuelve el tipo de este generador.
	 *
	 * @return el tipo de generador
	 */
	@Override
	public GeneratorType getType() {
		return this.Generatortype;
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
	 * Comprueba si un estado está contenido en una lista.
	 *
	 * @param state estado
	 * @return {@code true} si el estado está en la lista
	 */
	private boolean Contain(State state){
		boolean found = false;
		for (Iterator<State> iter = visitedState.iterator(); iter.hasNext();) {
			State element = (State) iter.next();
			if(element.Comparator(state)){
				found = true;
			}
		}
		return found;
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
	@Override
	public float getWeight() {
		return 0;
	}

	/**
	 * Establece el peso.
	 *
	 * @param weight el peso
	 */
	@Override
	public void setWeight(float weight) {

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
}
