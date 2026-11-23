package metaheuristics.generators;

import java.lang.reflect.InvocationTargetException;
import local_search.acceptation_type.AcceptableCandidate;
import metaheurictics.strategy.Strategy;
import factory_method.FactoryAcceptCandidate;

import java.util.ArrayList;
import java.util.List;

import local_search.acceptation_type.AcceptType;
import local_search.candidate_type.CandidateType;
import local_search.candidate_type.CandidateValue;
import local_search.complement.StrategyType;

import problem.definition.State;

import factory_interface.IFFactoryAcceptCandidate;

/** Recocido simulado con enfriamiento geométrico. */
public class SimulatedAnnealing extends Generator {

	/** El selector de candidatos. */
	private CandidateValue candidatevalue;
	/** El criterio de aceptación. */
	private AcceptType typeAcceptation;
	/** El modo de búsqueda. */
	private StrategyType strategy;
	/** El tipo de candidato. */
	private CandidateType typeCandidate;
	/** El estado de referencia. */
	private State stateReferenceSA;
    /** La factoría de criterios de aceptación. */
    private IFFactoryAcceptCandidate ifacceptCandidate;
    /** El factor de enfriamiento. */
    public static Double alpha;
    /** La temperatura actual. */
    public static Double tinitial;
    /** La temperatura final. */
    public static Double tfinal;
    /** La iteración del próximo enfriamiento. */
    public static int countIterationsT;
    /** El número de iteraciones entre enfriamientos. */
    private int countRept;
    /** El tipo de generador. */
    private GeneratorType typeGenerator;
    /** La lista de estados de referencia. */
    private List<State> listStateReference = new ArrayList<State>();
    /** El peso. */
    private float weight;

	//problemas dinamicos
    /** El número de usos del generador en el periodo. */
    public static int countGender = 0;
    /** El número de mejoras del generador en el periodo. */
    public static int countBetterGender = 0;
    /** El número de mejoras por periodo. */
    private int[] listCountBetterGender = new int[10];
    /** El número de usos por periodo. */
    private int[] listCountGender = new int[10];
    /** La traza de pesos. */
    private float[] listTrace = new float[1200000];


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

	/** Crea una instancia de SimulatedAnnealing. */
	public SimulatedAnnealing(){

    	super();
    	

    	this.typeAcceptation = AcceptType.AcceptNotBadT;
		this.strategy = StrategyType.NORMAL;
		this.typeCandidate = CandidateType.RandomCandidate;
		this.candidatevalue = new CandidateValue();
		this.typeGenerator = GeneratorType.SimulatedAnnealing;
		this.weight = 50;
		listTrace[0] = this.weight;
		listCountBetterGender[0] = 0;
		listCountGender[0] = 0;
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
		neighborhood = Strategy.getStrategy().getProblem().getOperator().generatedNewState(stateReferenceSA, operatornumber);
	    State statecandidate = candidatevalue.stateCandidate(stateReferenceSA, typeCandidate, strategy, operatornumber, neighborhood);
	    return statecandidate;
	}

	/**
	 * Devuelve el estado de referencia actual del generador.
	 *
	 * @return el estado de referencia
	 */
	@Override
	public State getReference() {
		return stateReferenceSA;
	}

	/**
	 * Establece el estado de referencia.
	 *
	 * @param stateRef estado de referencia
	 */
	public void setStateRef(State stateRef) {
		this.stateReferenceSA = stateRef;
	}

	/**
	 * Establece el estado de referencia inicial del generador.
	 *
	 * @param stateInitialRef estado de referencia inicial
	 */
	@Override
	public void setInitialReference(State stateInitialRef) {
		this.stateReferenceSA = stateInitialRef;
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
		countRept = countIterationsT;
		ifacceptCandidate = new FactoryAcceptCandidate();
		AcceptableCandidate candidate = ifacceptCandidate.createAcceptCandidate(typeAcceptation);
		Boolean accept = candidate.acceptCandidate(stateReferenceSA, stateCandidate);
		if(accept.equals(true))
		  stateReferenceSA = stateCandidate;
		if(countIterationsCurrent.equals(countIterationsT)){
			tinitial = tinitial * alpha;
			countIterationsT = countIterationsT + countRept;
		}
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
		listStateReference.add(stateReferenceSA);
		return listStateReference;
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
		return this.listCountBetterGender;
	}

	/**
	 * Devuelve el número de usos por periodo.
	 *
	 * @return el número de usos por periodo
	 */
	@Override
	public int[] getListCountGender() {
		return this.listCountGender;
	}

	/**
	 * Devuelve la traza de pesos del generador en el portafolio.
	 *
	 * @return la traza de pesos
	 */
	@Override
	public float[] getTrace() {
		return this.listTrace;
	}

}
