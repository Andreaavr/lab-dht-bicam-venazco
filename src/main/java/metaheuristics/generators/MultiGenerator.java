package metaheuristics.generators;

import java.util.logging.Level;
import util.RandomUtil;
import java.lang.reflect.InvocationTargetException;
import factory_method.FactoryGenerator;
import metaheurictics.strategy.Strategy;
import problem.definition.Problem.ProblemType;

import java.util.logging.Logger;


import java.util.ArrayList;
import java.util.List;



import problem.definition.State;

/**
 * Portafolio de generadores: elige en cada iteración el generador a aplicar mediante una ruleta de pesos.
 */
public class MultiGenerator extends Generator implements Cloneable {

	/** Registro de errores de la clase. */
	private static final Logger LOGGER = Logger.getLogger(MultiGenerator.class.getName());

	/** El tipo de generador. */
	private GeneratorType Generatortype;
	/** Los generadores del portafolio. */
	private static Generator[] listGenerators = new Generator[GeneratorType.values().length];
	/** La población inicial de los generadores poblacionales. */
	public static List<State> listGeneratedPP = new ArrayList<State> ();
	/** El generador activo del portafolio. */
	public static Generator activeGenerator;
	/** La lista de estados de referencia. */
	public static List<State> listStateReference = new ArrayList<State>(); 
	
	/**
	 * Establece el tipo de generador.
	 *
	 * @param generatortype tipo de generador
	 */
	public void setGeneratortype(GeneratorType generatortype) {
		Generatortype = generatortype;
	}

	/** Crea una instancia de MultiGenerator. */
	public MultiGenerator(){
		super();
		this.Generatortype = GeneratorType.MultiGenerator;
	}
	

	/** Libera el estado estático del portafolio de generadores. */
	public static void destroyMultiGenerator(){
		listGeneratedPP.clear();
		listStateReference.clear();
		activeGenerator = null;
		listGenerators = null;
	}
	
	/**
	 * Inicializa la lista de generadores del portafolio.
	 *
	 * @throws IllegalArgumentException si algún argumento no es válido
	 * @throws SecurityException si el gestor de seguridad impide la reflexión
	 * @throws ClassNotFoundException si la clase pedida a la factoría no existe o no está permitida
	 * @throws InstantiationException si la clase pedida a la factoría no se puede instanciar
	 * @throws IllegalAccessException si el constructor de la clase no es accesible
	 * @throws InvocationTargetException si el constructor de la clase lanza una excepción
	 * @throws NoSuchMethodException si la clase no tiene constructor sin argumentos
	 */
	public static void initializeListGenerator()throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		listGenerators = new Generator[4];
		Generator generator1 = new HillClimbing();
		Generator generator2 = new EvolutionStrategies();
		Generator generator3 = new LimitThreshold();
		Generator generator4 = new GeneticAlgorithm();
		listGenerators[0] = generator1;
		listGenerators[1] = generator2;
		listGenerators[2] = generator3;
		listGenerators[3] = generator4;
	}
	
	/**
	 * Crea el mapa de generadores para el portafolio.
	 *
	 * @throws IllegalArgumentException si algún argumento no es válido
	 * @throws SecurityException si el gestor de seguridad impide la reflexión
	 * @throws ClassNotFoundException si la clase pedida a la factoría no existe o no está permitida
	 * @throws InstantiationException si la clase pedida a la factoría no se puede instanciar
	 * @throws IllegalAccessException si el constructor de la clase no es accesible
	 * @throws InvocationTargetException si el constructor de la clase lanza una excepción
	 * @throws NoSuchMethodException si la clase no tiene constructor sin argumentos
	 */
	public static void initializeGenerators() throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		initializeListGenerator();
		State stateREF = new State(Strategy.getStrategy().getProblem().getState());
		listStateReference.add(stateREF);
		for (int i = 0; i < listGenerators.length; i++) {
			if ((listGenerators[i].getType().equals(GeneratorType.HillClimbing)) || (listGenerators[i].getType().equals(GeneratorType.HillClimbingRestart)) || (listGenerators[i].getType().equals(GeneratorType.RandomSearch)) || (listGenerators[i].getType().equals(GeneratorType.TabuSearch)) || (listGenerators[i].getType().equals(GeneratorType.SimulatedAnnealing) || (listGenerators[i].getType().equals(GeneratorType.LimitThreshold)))){
				listGenerators[i].setInitialReference(stateREF);
			}
		}
		createInstanceGeneratorsBPP();
		Strategy.getStrategy().listStates = MultiGenerator.getListGeneratedPP();
		
		FactoryGenerator ifFactoryGeneratorEE = new FactoryGenerator();
		Generator generatorEE = ifFactoryGeneratorEE.createGenerator(GeneratorType.EvolutionStrategies);
		
		FactoryGenerator ifFactoryGeneratorGA = new FactoryGenerator();
		Generator generatorGA = ifFactoryGeneratorGA.createGenerator(GeneratorType.GeneticAlgorithm);
		
		FactoryGenerator ifFactoryGeneratorEDA = new FactoryGenerator();
		Generator generatorEDA = ifFactoryGeneratorEDA.createGenerator(GeneratorType.DistributionEstimationAlgorithm);
		
		for (int i = 0; i < MultiGenerator.getListGenerators().length; i++) {
			if(MultiGenerator.getListGenerators()[i].getType().equals(GeneratorType.EvolutionStrategies)){
				MultiGenerator.getListGenerators()[i] = generatorEE;
			}
			if(MultiGenerator.getListGenerators()[i].getType().equals(GeneratorType.GeneticAlgorithm)){
				MultiGenerator.getListGenerators()[i] = generatorGA;
			}
			if(MultiGenerator.getListGenerators()[i].getType().equals(GeneratorType.DistributionEstimationAlgorithm)){
				MultiGenerator.getListGenerators()[i] = generatorEDA;
			}
		}
		
		
	}

	
	/** Genera la población inicial compartida por los generadores poblacionales del portafolio. */
	public static void createInstanceGeneratorsBPP() {
		Generator generator = new RandomSearch();
		int j = 0;
		while (j < EvolutionStrategies.countRef){
			State stateCandidate;
			try {
				stateCandidate = generator.generate(1);
				Strategy.getStrategy().getProblem().Evaluate(stateCandidate);
				
				stateCandidate.setNumber(j);
				stateCandidate.setTypeGenerator(generator.getType());
				listGeneratedPP.add(stateCandidate);
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Error al crear el generador", e);
			} 
			j++;
		}
	}
	
	/**
	 * Devuelve la población inicial de los generadores poblacionales.
	 *
	 * @return la población inicial de los generadores poblacionales
	 */
	private static ArrayList<State> getListGeneratedPP() {
		return (ArrayList<State>) listGeneratedPP;
	}

	/**
	 * Devuelve los generadores del portafolio.
	 *
	 * @return los generadores del portafolio
	 */
	public static Generator[] getListGenerators() {
		return listGenerators;
	}

	/**
	 * Establece los generadores del portafolio.
	 *
	 * @param listGenerators los generadores del portafolio
	 */
	public static void setListGenerators(Generator[] listGenerators) {
		MultiGenerator.listGenerators = listGenerators;
	}

	/**
	 * Devuelve el generador activo del portafolio.
	 *
	 * @return el generador activo del portafolio
	 */
	public static Generator getActiveGenerator() {
		return activeGenerator;
	}

	/**
	 * Establece el generador activo del portafolio.
	 *
	 * @param activeGenerator el generador activo del portafolio
	 */
	public static void setActiveGenerator(Generator activeGenerator) {
		MultiGenerator.activeGenerator = activeGenerator;
	}

	/**
	 * Establece la población inicial de los generadores poblacionales.
	 *
	 * @param listGeneratedPP la población inicial de los generadores poblacionales
	 */
	public static void setListGeneratedPP(List<State> listGeneratedPP) {
		MultiGenerator.listGeneratedPP = listGeneratedPP;
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
	public State generate(Integer operatornumber)
			throws IllegalArgumentException, SecurityException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		Strategy.getStrategy().generator = roulette();
		activeGenerator = Strategy.getStrategy().generator;
		activeGenerator.countGender++;
		State state = Strategy.getStrategy().generator.generate(1);
		return state;
	}

	/**
	 * Devuelve el estado de referencia actual del generador.
	 *
	 * @return el estado de referencia
	 */
	@Override
	public State getReference() {
		return null;
	}

	/**
	 * Devuelve la lista de estados de referencia del generador.
	 *
	 * @return la lista de referencias
	 */
	@Override
	public List<State> getReferenceList() {
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
	 * Devuelve el tipo de este generador.
	 *
	 * @return el tipo de generador
	 */
	@Override
	public GeneratorType getType() {
		return this.Generatortype;
	}

	/**
	 * Establece el estado de referencia inicial del generador.
	 *
	 * @param stateInitialRef estado de referencia inicial
	 */
	@Override
	public void setInitialReference(State stateInitialRef) {

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
	public void updateReference(State stateCandidate,
			Integer countIterationsCurrent) throws IllegalArgumentException,
			SecurityException, ClassNotFoundException, InstantiationException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		updateWeight(stateCandidate);
		tournament(stateCandidate, countIterationsCurrent);
		
	}
	
	/**
	 * Reinicia el peso de todos los generadores del portafolio.
	 *
	 * @param stateCandidate estado candidato
	 */
	public void updateWeight(State stateCandidate) { 
		boolean search = searchState(stateCandidate);//premio por calidad. 
		if(!search)
			updateAwardImp();
		else updateAwardSC();
	}
	
	/**
	 * Busca un estado en la lista de referencia.
	 *
	 * @param stateCandidate estado candidato
	 * @return el estado encontrado
	 */
	public boolean searchState(State stateCandidate) {
		if(Strategy.getStrategy().getProblem().getTypeProblem().equals(ProblemType.Maximizar)){
			if(stateCandidate.getEvaluation().get(0) > Strategy.getStrategy().getBestState().getEvaluation().get(0)){
				if(stateCandidate.getEvaluation().get(0) > Strategy.getStrategy().getBestState().getEvaluation().get(0))
					activeGenerator.countBetterGender++;
				return true;
			}
			else return false;
		}
		else {
			if(stateCandidate.getEvaluation().get(0) < Strategy.getStrategy().getBestState().getEvaluation().get(0)){
				if(stateCandidate.getEvaluation().get(0) < Strategy.getStrategy().getBestState().getEvaluation().get(0))
					activeGenerator.countBetterGender++;
				return true;
			}
			else return false;
		}
		
		
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
	 * Selecciona un generador mediante una ruleta proporcional a los pesos.
	 *
	 * @return el generador elegido
	 */
	public Generator roulette() {
		float totalWeight = 0;
		for (int i = 0; i < listGenerators.length; i++) {
			totalWeight = listGenerators[i].getWeight() + totalWeight;
		}
		List<Float> listProb = new ArrayList<Float>();
		for (int i = 0; i < listGenerators.length; i++) {
			float probF = listGenerators[i].getWeight() / totalWeight;
			listProb.add(probF);
		}
		List<LimitRoulette> listLimit = new ArrayList<LimitRoulette>();
		float limitHigh = 0;
		float limitLow = 0;
		for (int i = 0; i < listProb.size(); i++) {
			LimitRoulette limitRoulette = new LimitRoulette();
			limitHigh = listProb.get(i) + limitHigh;
			limitRoulette.setLimitHigh(limitHigh);
			limitRoulette.setLimitLow(limitLow);
			limitLow = limitHigh;
			limitRoulette.setGenerator(listGenerators[i]);
			listLimit.add(limitRoulette);
		}
		float numbAleatory = (float) (RandomUtil.nextDouble() * (double)(1));
		boolean find = false;
		int i = 0;
		while ((!find) && (i < listLimit.size())){
			if((listLimit.get(i).getLimitLow() <= numbAleatory) && (numbAleatory <= listLimit.get(i).getLimitHigh())){
				find = true;
			}
			else i++;
		}
		if (find) {
			return listLimit.get(i).getGenerator();
		}
		else return listLimit.get(listLimit.size() - 1).getGenerator();
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
	
	/** Actualiza el peso del generador activo cuando no hay mejora (premio por éxito continuado). */
	@SuppressWarnings("static-access")
	public void updateAwardSC() {
		float weightLast = activeGenerator.getWeight();
		float weightUpdate = (float) (weightLast * (1 - 0.1) + 10);
		activeGenerator.setWeight(weightUpdate);
		for (int i = 0; i < listGenerators.length; i++) {
			if(listGenerators[i].equals(activeGenerator))
				activeGenerator.getTrace()[Strategy.getStrategy().getCountCurrent()] = weightUpdate;
			else{
				if(!listGenerators[i].getType().equals(Generatortype.MultiGenerator)){
					float trace = listGenerators[i].getWeight();
					listGenerators[i].getTrace() [Strategy.getStrategy().getCountCurrent()] = trace;
				}
			}
		}
	}

	/** Actualiza el peso del generador activo según la mejora obtenida (premio por mejora). */
	@SuppressWarnings("static-access")
	public void updateAwardImp() {
		float weightLast = activeGenerator.getWeight();
		float weightUpdate = (float) (weightLast * (1 - 0.1));
		activeGenerator.setWeight(weightUpdate);
		for (int i = 0; i < listGenerators.length; i++) {
			if(listGenerators[i].equals(activeGenerator))
				activeGenerator.getTrace()[Strategy.getStrategy().getCountCurrent()] = weightUpdate;
			else{
				if(!listGenerators[i].getType().equals(Generatortype.MultiGenerator)){
					float trace = listGenerators[i].getWeight();
					listGenerators[i].getTrace() [Strategy.getStrategy().getCountCurrent()] = trace;
				}
			}
		}
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
	 * Aplica el generador elegido y actualiza los pesos del portafolio.
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
	@SuppressWarnings("static-access")
	public void tournament(State stateCandidate,Integer countIterationsCurrent) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		State stateTem = new State(stateCandidate);
		for (int i = 0; i < MultiGenerator.getListGenerators().length; i++) {
			if(!listGenerators[i].getType().equals(Generatortype.MultiGenerator))
				MultiGenerator.getListGenerators()[i].updateReference(stateTem, countIterationsCurrent);
		}
	}
	
	/**
	 * Crea una copia del objeto.
	 *
	 * @return la copia
	 */
	@Override
	public Object clone(){
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError("MultiGenerator implementa Cloneable", e);
		}
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
