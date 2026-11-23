package metaheuristics.generators;

import util.RandomUtil;
import java.lang.reflect.InvocationTargetException;
import metaheurictics.strategy.Strategy;
import problem.definition.Problem.ProblemType;
import evolutionary_algorithms.complement.Crossover;
import evolutionary_algorithms.complement.FatherSelection;
import evolutionary_algorithms.complement.Mutation;
import evolutionary_algorithms.complement.Replace;
import factory_method.FactoryCrossover;
import factory_method.FactoryFatherSelection;
import factory_method.FactoryMutation;
import factory_method.FactoryReplace;


import java.util.ArrayList;
import java.util.List;


import problem.definition.State;

import evolutionary_algorithms.complement.CrossoverType;
import evolutionary_algorithms.complement.MutationType;
import evolutionary_algorithms.complement.ReplaceType;
import evolutionary_algorithms.complement.SelectionType;
import factory_interface.IFFactoryCrossover;
import factory_interface.IFFactoryFatherSelection;
import factory_interface.IFFactoryMutation;
import factory_interface.IFFactoryReplace;

/** Algoritmo genético: selección, cruce, mutación y reemplazo. */
public class GeneticAlgorithm extends Generator {

	/** El estado de referencia. */
	private State stateReferenceGA;
	/** La población de estados. */
	private List<State> listState = new ArrayList<State>(); 
	/** La factoría de selección de padres. */
	private IFFactoryFatherSelection iffatherselection;
	/** La factoría de cruces. */
	private IFFactoryCrossover iffactorycrossover;
	/** La factoría de mutaciones. */
	private IFFactoryMutation iffactorymutation;
	/** La factoría de reemplazos. */
	private IFFactoryReplace iffreplace;
	
	/** El tipo de mutación. */
	public static MutationType mutationType;
	/** El tipo de cruce. */
	public static CrossoverType crossoverType;
	/** El tipo de reemplazo. */
	public static ReplaceType replaceType;
	/** El tipo de selección. */
	public static SelectionType selectionType;
	
	/** El tipo de generador. */
	private GeneratorType generatorType;
	/** La probabilidad de cruce. */
	public static double PC;
	/** La probabilidad de mutación. */
	public static double PM;
	/** El tamaño de la población de referencia. */
	public static int countRef = 0;
	/** El número de padres seleccionados. */
	public static int truncation;
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
	
    /** Crea una instancia de GeneticAlgorithm. */
    public GeneticAlgorithm() {
		super();
		this.listState = getListStateRef(); // llamada al método que devuelve la lista. 
		this.generatorType = GeneratorType.GeneticAlgorithm;
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
    	
		//******************selection*******************************
		List<State> refList = new ArrayList<State>(this.listState); 
    	iffatherselection = new FactoryFatherSelection();
    	FatherSelection selection = iffatherselection.createSelectFather(selectionType);
    	List<State> fathers = selection.selection(refList, truncation);
    	int pos1 = (int)(RandomUtil.nextDouble() * fathers.size());
    	int pos2 = (int)(RandomUtil.nextDouble() * fathers.size());
    	
    	State auxState1 = (State) Strategy.getStrategy().getProblem().getState().getCopy();
    	auxState1.setCode(new ArrayList<Object>(fathers.get(pos1).getCode()));
    	auxState1.setEvaluation(fathers.get(pos1).getEvaluation());
    	auxState1.setNumber(fathers.get(pos1).getNumber());
    	auxState1.setTypeGenerator(fathers.get(pos1).getTypeGenerator());
    	
    	State auxState2 = (State) Strategy.getStrategy().getProblem().getState().getCopy();
    	auxState2.setCode(new ArrayList<Object>(fathers.get(pos2).getCode()));
    	auxState2.setEvaluation(fathers.get(pos2).getEvaluation());
    	auxState2.setNumber(fathers.get(pos2).getNumber());
    	auxState2.setTypeGenerator(fathers.get(pos2).getTypeGenerator());
    	
	    //**********cruzamiento*************************************
	    iffactorycrossover = new FactoryCrossover();
    	Crossover crossover = iffactorycrossover.createCrossover(crossoverType);
    	auxState1 = crossover.crossover(auxState1, auxState2, PC);
	    
    	//**********mutacion******************************************** 	
    	iffactorymutation = new FactoryMutation();
    	Mutation mutation =iffactorymutation.createMutation(mutationType);
    	auxState1 = mutation.mutation(auxState1, PM);
    	
    	return auxState1;
	}
    
	/**
	 * Devuelve el estado de referencia actual del generador.
	 *
	 * @return el estado de referencia
	 */
	@Override
	public State getReference() {
		stateReferenceGA = listState.get(0);
		if(Strategy.getStrategy().getProblem().getTypeProblem().equals(ProblemType.Maximizar)){
			for (int i = 1; i < listState.size(); i++) {
				if(stateReferenceGA.getEvaluation().get(0) < listState.get(i).getEvaluation().get(0))
					stateReferenceGA = listState.get(i);
			}
		}
		else{
			for (int i = 1; i < listState.size(); i++) {
				if(stateReferenceGA.getEvaluation().get(0) > listState.get(i).getEvaluation().get(0))
					stateReferenceGA = listState.get(i);
			}
		}
		return stateReferenceGA;
	}

	/**
	 * Establece el estado de referencia.
	 *
	 * @param stateRef estado de referencia
	 */
	public void setStateRef(State stateRef) {
		this.stateReferenceGA = stateRef;
	}
	/**
	 * Establece el estado de referencia inicial del generador.
	 *
	 * @param stateInitialRef estado de referencia inicial
	 */
	@Override
	public void setInitialReference(State stateInitialRef) {
		this.stateReferenceGA = stateInitialRef;
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
		listState = replace.replace(stateCandidate, listState);
	}
	
	/**
	 * Devuelve la población de estados.
	 *
	 * @return la población de estados
	 */
	public List<State> getListState() {
		return listState;
	}

	/**
	 * Establece la población de estados.
	 *
	 * @param listState población de estados
	 */
	public void setListState(List<State> listState) {
		this.listState = listState;
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
			return this.listState = new ArrayList<State>();
		}*/
		while((found.equals(false)) && (Strategy.getStrategy().mapGenerators.size() > count)){
			if(key.get(count).equals(GeneratorType.GeneticAlgorithm.toString())){
				GeneratorType keyGenerator = GeneratorType.valueOf(String.valueOf(key.get(count)));
				GeneticAlgorithm generator = (GeneticAlgorithm) Strategy.getStrategy().mapGenerators.get(keyGenerator);
				if(generator.getListState().isEmpty()){
					listState.addAll(RandomSearch.listStateReference);
				}
				else{
					listState = generator.getListState();
				}
			        found = true;
			}
			count++;
		}
		return listState;
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
	 * Devuelve el tipo de este generador.
	 *
	 * @return el tipo de generador
	 */
	@Override
	public GeneratorType getType() {
		return this.generatorType;
	}

	/**
	 * Devuelve la lista de estados de referencia del generador.
	 *
	 * @return la lista de referencias
	 */
	@Override
	public List<State> getReferenceList() {
		List<State> ReferenceList = new ArrayList<State>();
		for (int i = 0; i < listState.size(); i++) {
			State value = listState.get(i);
			ReferenceList.add(value);
		}
		return ReferenceList;
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
