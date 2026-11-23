package metaheuristics.generators;

import java.lang.reflect.InvocationTargetException;
import metaheurictics.strategy.Strategy;
import problem.definition.Problem.ProblemType;
import evolutionary_algorithms.complement.FatherSelection;
import evolutionary_algorithms.complement.Replace;
import evolutionary_algorithms.complement.ReplaceType;
import evolutionary_algorithms.complement.Sampling;
import evolutionary_algorithms.complement.SelectionType;
import factory_method.FactoryFatherSelection;
import factory_method.FactoryReplace;
import factory_method.FactorySampling;

import java.util.ArrayList;
import java.util.List;


import problem.definition.State;

import evolutionary_algorithms.complement.DistributionType;
import evolutionary_algorithms.complement.SamplingType;
import factory_interface.IFFSampling;
import factory_interface.IFFactoryFatherSelection;
import factory_interface.IFFactoryReplace;

/** Algoritmo de estimación de distribuciones (EDA). */
public class DistributionEstimationAlgorithm extends Generator {

	/** El estado de referencia. */
	private State stateReferenceDA;
	/** La lista de referencia. */
	private List<State> referenceList = new ArrayList<State>(); 
	/** La lista de hijos. */
	public static List<State> sonList = new ArrayList<State>(); 
	/** La factoría de selección de padres. */
	private IFFactoryFatherSelection iffatherselection;
	/** La factoría de muestreos. */
	private IFFSampling iffsampling;
	/** La factoría de reemplazos. */
	private IFFactoryReplace iffreplace;
	/** El tipo de distribución. */
	private DistributionType distributionType;
	/** El tipo de muestreo. */
	private SamplingType Samplingtype;
	
	/** El tipo de reemplazo. */
	public static ReplaceType replaceType;
	/** El tipo de selección. */
	public static SelectionType selectionType;
	
	/** El tipo de generador. */
	private GeneratorType generatorType;
	/** El número de padres seleccionados. */
	public static int truncation;
	/** El tamaño de la población de referencia. */
	public static int countRef = 0;
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
	
	
	/** Crea una instancia de DistributionEstimationAlgorithm. */
	public DistributionEstimationAlgorithm() {
		super();
		this.referenceList = getListStateRef(); // llamada al método que devuelve la lista. 
		this.generatorType = GeneratorType.DistributionEstimationAlgorithm;
		this.distributionType = DistributionType.Univariate;
		this.Samplingtype = SamplingType.ProbabilisticSampling;
		this.weight = 50;
		listTrace[0] = weight;
		listCountBetterGender[0] = 0;
		listCountGender[0] = 0;
	}
	
	/**
	 * Busca el estado de mayor evaluación.
	 *
	 * @param listInd lista de individuos
	 * @return el estado con la evaluación máxima
	 */
	public State MaxValue (List<State> listInd){
		State state = new State(listInd.get(0));
		double max = state.getEvaluation().get(0);
		for (int i = 1; i < listInd.size(); i++) {
			if(listInd.get(i).getEvaluation().get(0) > max){
				max = listInd.get(i).getEvaluation().get(0);
				state = new State(listInd.get(i));
			}
		}
		return state;
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
	public State generate(Integer operatornumber) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException,	NoSuchMethodException {
		//********************selection*****************************
		/*State candidate = new State();//(State) Strategy.getStrategy().getProblem().getState().clone()
		List<State> fathers = new ArrayList<State>();
		fathers = getfathersList();
		//**************************distribution***********************************
		iffdistribution = new FactoryDistribution();
    	Distribution distribution = iffdistribution.createDistribution(distributionType);
    	List<Probability> probability = distribution.distribution(fathers);
		
    	//***************************muestreo*****************************************
    	iffsampling = new FactorySampling();
    	Sampling samplingG = iffsampling.createSampling(Samplingtype);
    	List<State> ind = samplingG.sampling(probability, referenceList.size());
    	sonList = ind;
    	candidate = MaxValue(ind);
    	return candidate;*/	
    	
    	//***************************version 1.0
    	//ArrayList<State> listcandidate = new ArrayList<State>();//(State) Strategy.getStrategy().getProblem().getState().clone()
		
    	List<State> fathers = new ArrayList<State>();
		fathers = getfathersList();
		iffsampling = new FactorySampling();
    	Sampling samplingG = iffsampling.createSampling(Samplingtype);
    	List<State> ind = samplingG.sampling(fathers, operatornumber);
    	State candidate = null;
    	if(ind.size() > 1){
    		for (int i = 0; i < ind.size(); i++) {
    			double evaluation = Strategy.getStrategy().getProblem().getFunction().get(0).Evaluation(ind.get(i));
    			ArrayList<Double> listEval = new ArrayList<Double>();
    			listEval.add(evaluation);
    			ind.get(0).setEvaluation(listEval);
    		}
    		candidate = MaxValue(ind);
    	}
    	else{
    		candidate = ind.get(0);
    	}
    	return candidate;
    	
    }
    		
	/**
	 * Devuelve el estado de referencia actual del generador.
	 *
	 * @return el estado de referencia
	 */
	@Override
	public State getReference() {
		stateReferenceDA = referenceList.get(0);
		if(Strategy.getStrategy().getProblem().getTypeProblem().equals(ProblemType.Maximizar)){
			for (int i = 1; i < referenceList.size(); i++) {
				if(stateReferenceDA.getEvaluation().get(0) < referenceList.get(i).getEvaluation().get(0))
					stateReferenceDA = referenceList.get(i);
			}
		}
		else{
			for (int i = 1; i < referenceList.size(); i++) {
				if(stateReferenceDA.getEvaluation().get(0) > referenceList.get(i).getEvaluation().get(0))
					stateReferenceDA = referenceList.get(i);
			}
		}
		return stateReferenceDA;
	}

	/**
	 * Devuelve la lista de estados de referencia del generador.
	 *
	 * @return la lista de referencias
	 */
	@Override
	public List<State> getReferenceList() {
		List<State> ReferenceList = new ArrayList<State>();
		for (int i = 0; i < referenceList.size(); i++) {
			State value = referenceList.get(i);
			ReferenceList.add( value);
		}
		return ReferenceList;
	}

	/**
	 * Devuelve el tipo de este generador.
	 *
	 * @return el tipo de generador
	 */
	@Override
	public GeneratorType getType() {
		return this.generatorType;
	}

	/**
	 * Establece el estado de referencia inicial del generador.
	 *
	 * @param stateInitialRef estado de referencia inicial
	 */
	@Override
	public void setInitialReference(State stateInitialRef) {
		this.stateReferenceDA = stateInitialRef;
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
	public void updateReference(State stateCandidate, Integer countIterationsCurrent) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException,	NoSuchMethodException {
		iffreplace = new FactoryReplace();
		Replace replace = iffreplace.createReplace(replaceType);
		referenceList = replace.replace(stateCandidate, referenceList);
	}
	
	/**
	 * Obtiene la población inicial de referencia a partir de los estados generados por la búsqueda aleatoria.
	 *
	 * @return la población de referencia
	 */
	public List<State> getListStateRef(){
		Boolean found = false;
		List<String> key = Strategy.getStrategy().getListKey();
		int count = 0;
		/*if(RandomSearch.listStateReference.size() == 0){
			return this.referenceList = new ArrayList<State>();
		}*/
		while((found.equals(false)) && (Strategy.getStrategy().mapGenerators.size() > count)){
			if(key.get(count).equals(GeneratorType.DistributionEstimationAlgorithm.toString())){
				GeneratorType keyGenerator = GeneratorType.valueOf(String.valueOf(key.get(count)));
				DistributionEstimationAlgorithm generator = (DistributionEstimationAlgorithm)Strategy.getStrategy().mapGenerators.get(keyGenerator);
				if(generator.getListReference().isEmpty()){
					referenceList.addAll(RandomSearch.listStateReference);
				}
				else{
					referenceList = generator.getListReference();
				}
			    found = true;
			}
			count++;
		}
		return referenceList;
	}

	/**
	 * Devuelve la lista de referencia.
	 *
	 * @return la lista de referencia
	 */
	public List<State> getListReference() {
		return referenceList;
	}

	/**
	 * Establece la lista de referencia.
	 *
	 * @param listReference la lista de referencia
	 */
	public void setListReference(List<State> listReference) {
		referenceList = listReference;
	}

	/**
	 * Devuelve el tipo de generador.
	 *
	 * @return el tipo de generador
	 */
	public GeneratorType getGeneratorType() {
		return generatorType;
	}

	/**
	 * Establece el tipo de generador.
	 *
	 * @param generatorType tipo de generador
	 */
	public void setGeneratorType(GeneratorType generatorType) {
		this.generatorType = generatorType;
	}

	/**
	 * Devuelve la lista de padres seleccionados.
	 *
	 * @return la lista de padres
	 * @throws IllegalArgumentException si algún argumento no es válido
	 * @throws SecurityException si el gestor de seguridad impide la reflexión
	 * @throws ClassNotFoundException si la clase pedida a la factoría no existe o no está permitida
	 * @throws InstantiationException si la clase pedida a la factoría no se puede instanciar
	 * @throws IllegalAccessException si el constructor de la clase no es accesible
	 * @throws InvocationTargetException si el constructor de la clase lanza una excepción
	 * @throws NoSuchMethodException si la clase no tiene constructor sin argumentos
	 */
	public List<State> getfathersList() throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		List<State> refList = new ArrayList<State>(this.referenceList); 
    	iffatherselection = new FactoryFatherSelection();
    	FatherSelection selection = iffatherselection.createSelectFather(selectionType);
    	List<State> fathers = selection.selection(refList, truncation);
    	return fathers;
	}

	/**
	 * Devuelve la lista de hijos generados.
	 *
	 * @return la lista de hijos o {@code null} si no aplica
	 */
	@Override
	public List<State> getSonList() {
		return sonList;
	}

	/**
	 * Indica si el candidato mejora la referencia del generador (usado por el portafolio).
	 *
	 * @param stateCandidate estado candidato
	 * @return {@code true} si el candidato mejora la referencia
	 */
	public boolean awardUpdateREF(State stateCandidate) {
		boolean find = false;
		int i = 0;
		while (!find && i < this.referenceList.size()) {
			if(stateCandidate.equals(this.referenceList.get(i)))
				find = true;
			else i++;
		}
		return find;
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
	 * Devuelve el tipo de distribución.
	 *
	 * @return el tipo de distribución
	 */
	public DistributionType getDistributionType() {
		return distributionType;
	}

	/**
	 * Establece el tipo de distribución.
	 *
	 * @param distributionType tipo de distribución
	 */
	public void setDistributionType(DistributionType distributionType) {
		this.distributionType = distributionType;
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