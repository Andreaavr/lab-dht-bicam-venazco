package metaheurictics.strategy;

import java.util.logging.Level;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.TreeMap;
import factory_method.FactoryGenerator;
import problem.definition.Problem.ProblemType;
import local_search.acceptation_type.Dominance;
import metaheuristics.generators.DistributionEstimationAlgorithm;
import metaheuristics.generators.EvolutionStrategies;
import metaheuristics.generators.GeneticAlgorithm;
import metaheuristics.generators.MultiGenerator;
import metaheuristics.generators.ParticleSwarmOptimization;
import metaheuristics.generators.RandomSearch;

import java.util.logging.Logger;

import java.util.ArrayList;
import java.util.SortedMap;

import factory_interface.IFFactoryGenerator;

import problem.definition.Problem;
import problem.definition.State;

import local_search.complement.StopExecute;
import local_search.complement.UpdateParameter;
import metaheuristics.generators.Generator;
import metaheuristics.generators.GeneratorType;

/** Estrategia (singleton) que ejecuta una metaheurística sobre el problema configurado. */
public class Strategy {

	/** Registro de eventos de la estrategia. */
	private static final Logger LOGGER = Logger.getLogger(Strategy.class.getName());

	/** El modo de búsqueda. */
	private static Strategy strategy = null;
	/** El mejor estado encontrado. */
	private State bestState;
	/** El problema. */
	private Problem problem;
	/** El mapa de generadores por tipo. */
	public SortedMap<GeneratorType, Generator> mapGenerators;
	/** La condición de parada. */
	private StopExecute stopexecute;
	/** El actualizador de parámetros. */
	private UpdateParameter updateparameter;
	/** La factoría de generadores. */
	private IFFactoryGenerator ifFactoryGenerator;
	/** La iteración actual. */
	private int countCurrent;
	/** El número máximo de iteraciones. */
	private int countMax;
	/** El generador. */
	public Generator generator;
	/** El umbral de aceptación. */
	public double threshold;

	/** La lista de todos los estados generados. */
	public ArrayList<State> listStates; //lista de todos los estados generados en cada iteracion
	/** La lista del mejor estado de cada iteración. */
	public ArrayList<State> listBest; //lista de la mejor solucion en cada iteracion
	/** Las soluciones no dominadas encontradas. */
	public List<State> listRefPoblacFinal = new ArrayList<State> (); //lista de soluciones no dominadas
	/** El gestor de dominancia del frente de Pareto. */
	public Dominance notDominated; 

	
	/** El indicador de guardado de los estados generados. */
	public boolean saveListStates; //guardar lista de estados generados
	/** El indicador de guardado de los mejores estados. */
	public boolean saveListBestStates; // guardar lista con los mejores estados encontrados en cada iteracion
	/** El indicador de guardado del frente de Pareto. */
	public boolean saveFreneParetoMonoObjetivo; //guardar lista de soluciones no dominadas de una ejecución
	/** El indicador de cálculo del tiempo de ejecución. */
	public boolean calculateTime; // calcular tiempo de ejecución de un algoritmo
	
	//calculo del Tiempo inicial y final
	/** El instante de inicio. */
	long initialTime;
	/** El instante de fin. */
	long finalTime;
	/** El tiempo de ejecución en milisegundos. */
	public static long timeExecute;
	
	/** Los valores de offline performance. */
	public float[] listOfflineError = new float[100]; // para almacenar la metrica offlinePerformance
	/** El número de iteraciones entre cambios. */
	public int countPeriodChange = 0; // cantidad de iteeraciones antes de un cambio
	/** La iteración del próximo cambio. */
	public int countChange = 0;
	/** La iteración de fin del periodo actual. */
	private int countPeriodo; // contador para saber cada cuantas iteraciones tiene que guardar en la lista listCountBetterGender de cada generador
	/** El periodo actual. */
	private int periodo; //contador para controlar el periodo que esta guardando


	/** Crea una instancia de Strategy. */
	private Strategy(){
		super();
	}

	/**
	 * Devuelve la instancia única de la estrategia, creándola si no existe.
	 *
	 * @return la estrategia
	 */
	public static Strategy getStrategy() {
		if (strategy == null) {
			strategy = new Strategy();
		}
		return strategy;
	}

	/**
	 * Ejecuta la metaheurística indicada hasta alcanzar el número máximo de iteraciones.
	 *
	 * @param countmaxIterations número máximo de iteraciones
	 * @param countIterationsChange iteraciones entre cambios del problema dinámico
	 * @param operatornumber número de vecinos o estados a generar
	 * @param generatorType tipo de generador
	 * @throws IllegalArgumentException si algún argumento no es válido
	 * @throws SecurityException si el gestor de seguridad impide la reflexión
	 * @throws ClassNotFoundException si la clase pedida a la factoría no existe o no está permitida
	 * @throws InstantiationException si la clase pedida a la factoría no se puede instanciar
	 * @throws IllegalAccessException si el constructor de la clase no es accesible
	 * @throws InvocationTargetException si el constructor de la clase lanza una excepción
	 * @throws NoSuchMethodException si la clase no tiene constructor sin argumentos
	 */
	public void executeStrategy (int countmaxIterations, int countIterationsChange, int operatornumber, GeneratorType generatorType) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		// si se quiere calcular el tiempo de ejecucion del un algoritmo
		if(calculateTime){
			initialTime = System.currentTimeMillis();
		}
		this.countMax = countmaxIterations; // max cantidad de iteraciones
		//generar estado inicial de la estrategia
		Generator randomInitial = new RandomSearch();
		State initialState = randomInitial.generate(operatornumber);
		problem.Evaluate(initialState); //evaluar ese estado
		initialState.setTypeGenerator(generatorType);
		getProblem().setState(initialState);
		//si se va a salvar la lista de estados generados, adicionar el estado
		if(saveListStates){
			listStates = new ArrayList<State>(); //list de estados generados
			listStates.add(initialState);
		}
		//si se va a salvar la lista de mejores soluciones encontradas en cada iteracion
		if(saveListBestStates){
			listBest = new ArrayList<State>(); // list de mejores estados encontrados
			listBest.add(initialState);
		}
		if(saveFreneParetoMonoObjetivo){
			notDominated = new Dominance();
		}
		// crear el generador a ejecutar
		generator = newGenerator(generatorType);
		generator.setInitialReference(initialState);
		bestState = initialState;
		countCurrent = 0;
		listRefPoblacFinal = new ArrayList<State>();
		MultiGenerator multiGenerator = null;
		countPeriodChange = countIterationsChange;
		countChange = countIterationsChange;
		countPeriodo = countIterationsChange / 10; //cantidad de iteraciones de un periodo 
		//verificar que es portafolio e inicializar los generadores del portafolio
		if(generatorType.equals(GeneratorType.MultiGenerator)){
			initializeGenerators();
			MultiGenerator.initializeGenerators();
			MultiGenerator.listGeneratedPP.clear();
			multiGenerator = (MultiGenerator)((MultiGenerator)generator).clone();
		}
		else initialize(); //crea el mapa de generadores
		update(countCurrent);
		
		float sumMax = 0; // suma acumulativa para almacenar la evaluacion de la mejor solucion encotrada y calcular el OfflinePerformance
		int countOff = 0; // variable par contar los OfflinePerformance que se van salvando en el arreglo
		//ciclio de ejecución del algoritmo
		while (!stopexecute.stopIterations(countCurrent, countmaxIterations)){
			//si se detecta un cambio
			if(countCurrent == countChange){
				//calcular offlinePerformance
				calculateOffLinePerformance(sumMax, countOff);
				countOff++;
				sumMax = 0;
//				countIterationsChange = countIterationsChange + countPeriodChange; // actualizar la cantidad de iteraciones
				//actualizar la referencia luego de un cambio
				updateRef(generatorType);
				countChange = countChange + countPeriodChange;
				//generar un nuevo candidato en la iteracion, dependiendo del generador
				State stateCandidate = null;
				if(generatorType.equals(GeneratorType.MultiGenerator)){
					if(countPeriodo == countCurrent){
						updateCountGender();
						countPeriodo = countPeriodo + countPeriodChange / 10;
						periodo = 0;
						MultiGenerator.activeGenerator.countBetterGender = 0;
					}
					updateWeight();//actualizar el peso de los generadores si se reinician cuando ocurre un cambio
					//generar el estado candidato de la iteración
					stateCandidate = multiGenerator.generate(operatornumber);
					problem.Evaluate(stateCandidate);
					stateCandidate.setEvaluation(stateCandidate.getEvaluation());
					stateCandidate.setNumber(countCurrent);
					stateCandidate.setTypeGenerator(generatorType);
					multiGenerator.updateReference(stateCandidate, countCurrent);
				}
				else {
					stateCandidate = generator.generate(operatornumber);
					problem.Evaluate(stateCandidate);
					stateCandidate.setEvaluation(stateCandidate.getEvaluation());
					stateCandidate.setNumber(countCurrent);
					stateCandidate.setTypeGenerator(generatorType);
					generator.updateReference(stateCandidate, countCurrent);
					if(saveListStates){
						listStates.add(stateCandidate);
					}
				}
				//actualizar el mejor estado encontrado solo tiene sentido para algoritmos monoobjetivos
				
				//actualizar el mejor estado encontrado solo tiene sentido para algoritmos monoobjetivos
				if ((getProblem().getTypeProblem().equals(ProblemType.Maximizar)) && bestState.getEvaluation().get(bestState.getEvaluation().size() - 1) < stateCandidate.getEvaluation().get(bestState.getEvaluation().size() - 1)) {
					bestState = stateCandidate;
				}
				if ((problem.getTypeProblem().equals(ProblemType.Minimizar)) && bestState.getEvaluation().get(bestState.getEvaluation().size() - 1) > stateCandidate.getEvaluation().get(bestState.getEvaluation().size() - 1)) {
					bestState = stateCandidate;
				}
				if(saveListBestStates){
					listBest.add(bestState);
				}
				sumMax = (float) (sumMax + bestState.getEvaluation().get(0));

			}
			// no ha ocurrido un cambio
			else {
				State stateCandidate = null;
				if(generatorType.equals(GeneratorType.MultiGenerator)){
					if(countPeriodo == countCurrent){
						updateCountGender();
						countPeriodo = countPeriodo + countPeriodChange / 10;
						periodo++;
						MultiGenerator.activeGenerator.countBetterGender = 0;
					}
					stateCandidate = multiGenerator.generate(operatornumber);
					problem.Evaluate(stateCandidate);
					stateCandidate.setEvaluation(stateCandidate.getEvaluation());
					stateCandidate.setNumber(countCurrent);
					stateCandidate.setTypeGenerator(generatorType);
					multiGenerator.updateReference(stateCandidate, countCurrent);
				}
				else {
					//generar estado candidato y evaluar si es aceptado o no 
					stateCandidate = generator.generate(operatornumber);
					problem.Evaluate(stateCandidate);
					stateCandidate.setEvaluation(stateCandidate.getEvaluation());
					stateCandidate.setNumber(countCurrent);
					stateCandidate.setTypeGenerator(generatorType);
					generator.updateReference(stateCandidate, countCurrent); // actualizar la referencia del estado
					if(saveListStates){
						listStates.add(stateCandidate);
					}
					if(saveFreneParetoMonoObjetivo){
						notDominated.ListDominance(stateCandidate, listRefPoblacFinal);
					}
				}
				countCurrent = UpdateParameter.updateParameter(countCurrent);
				//actualizar el mejor estado encontrado solo tiene sentido para algoritmos monoobjetivos
				if ((getProblem().getTypeProblem().equals(ProblemType.Maximizar)) && bestState.getEvaluation().get(bestState.getEvaluation().size() - 1) < stateCandidate.getEvaluation().get(bestState.getEvaluation().size() - 1)) {
					bestState = stateCandidate;
				}
				if ((problem.getTypeProblem().equals(ProblemType.Minimizar)) && bestState.getEvaluation().get(bestState.getEvaluation().size() - 1) > stateCandidate.getEvaluation().get(bestState.getEvaluation().size() - 1)) {
					bestState = stateCandidate;
				}
				if(saveListBestStates){
					listBest.add(bestState);
				}
				sumMax = (float) (sumMax + bestState.getEvaluation().get(0));
			}
		}
		//calcular tiempo final
		if(calculateTime){
			finalTime = System.currentTimeMillis();
			timeExecute = finalTime - initialTime;
			LOGGER.log(Level.INFO, "Tiempo de ejecucion (ms): {0}", timeExecute);
		}
		if(generatorType.equals(GeneratorType.MultiGenerator)){
			listBest = (ArrayList<State>) multiGenerator.getReferenceList();
			//calcular offlinePerformance
			calculateOffLinePerformance(sumMax, countOff);
			if(countPeriodo == countCurrent){
				updateCountGender();
			}
		}
		else{
			listBest = (ArrayList<State>) generator.getReferenceList();
			calculateOffLinePerformance(sumMax, countOff);
		} 
	}
	
	/** Acumula en cada generador las veces que se usó y que mejoró durante el periodo. */
	public void updateCountGender(){ // actualizar la cantidad de mejoras y cantidad de veces que se uso un generador en un periodo dado
		for (int i = 0; i < MultiGenerator.getListGenerators().length; i++) {
			if(!MultiGenerator.getListGenerators()[i].getType().equals(GeneratorType.MultiGenerator) ){/*&& !MultiGenerator.getListGenerators()[i].getType().equals(GeneratorType.MultiCaseSimulatedAnnealing) &&
				!MultiGenerator.getListGenerators()[i].getType().equals(GeneratorType.MultiobjectiveHillClimbingDistance) && !MultiGenerator.getListGenerators()[i].getType().equals(GeneratorType.MultiobjectiveHillClimbingRestart) &&
				!MultiGenerator.getListGenerators()[i].getType().equals(GeneratorType.MultiobjectiveStochasticHillClimbing) && !MultiGenerator.getListGenerators()[i].getType().equals(GeneratorType.MultiobjectiveTabuSearch) && 
				!MultiGenerator.getListGenerators()[i].getType().equals(GeneratorType.ParticleSwarmOptimization)*/
				MultiGenerator.getListGenerators()[i].getListCountGender()[periodo] = MultiGenerator.getListGenerators()[i].countGender + MultiGenerator.getListGenerators()[i].getListCountGender()[periodo];
				MultiGenerator.getListGenerators()[i].getListCountBetterGender()[periodo] = MultiGenerator.getListGenerators()[i].countBetterGender + MultiGenerator.getListGenerators()[i].getListCountBetterGender()[periodo];
				MultiGenerator.getListGenerators()[i].countGender = 0;
				MultiGenerator.getListGenerators()[i].countBetterGender = 0;
			}
		}
	}
	
	/** Reinicia el peso de todos los generadores del portafolio. */
	public void updateWeight(){
		for (int i = 0; i < MultiGenerator.getListGenerators().length; i++) {
			if(!MultiGenerator.getListGenerators()[i].getType().equals(GeneratorType.MultiGenerator)){
				MultiGenerator.getListGenerators()[i].setWeight((float) 50.0);
			}
		}
	}
	
	
	/**
	 * Cambia al generador poblacional correspondiente cuando se alcanza su countRef.
	 *
	 * @param countIterationsCurrent iteración actual
	 * @throws IllegalArgumentException si algún argumento no es válido
	 * @throws SecurityException si el gestor de seguridad impide la reflexión
	 * @throws ClassNotFoundException si la clase pedida a la factoría no existe o no está permitida
	 * @throws InstantiationException si la clase pedida a la factoría no se puede instanciar
	 * @throws IllegalAccessException si el constructor de la clase no es accesible
	 * @throws InvocationTargetException si el constructor de la clase lanza una excepción
	 * @throws NoSuchMethodException si la clase no tiene constructor sin argumentos
	 */
	public void update(Integer countIterationsCurrent) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {//HashMap<String, Object> map, 
		//		Here update parameter for update and change generator.
		if(countIterationsCurrent.equals(GeneticAlgorithm.countRef - 1)){
			ifFactoryGenerator = new FactoryGenerator();
			Strategy.getStrategy().generator = ifFactoryGenerator.createGenerator(GeneratorType.GeneticAlgorithm);
		}
		if(countIterationsCurrent.equals(EvolutionStrategies.countRef - 1)){
			ifFactoryGenerator = new FactoryGenerator();
			Strategy.getStrategy().generator = ifFactoryGenerator.createGenerator(GeneratorType.EvolutionStrategies);
		}			
		if(countIterationsCurrent.equals(DistributionEstimationAlgorithm.countRef - 1)){
			ifFactoryGenerator = new FactoryGenerator();
			Strategy.getStrategy().generator = ifFactoryGenerator.createGenerator(GeneratorType.DistributionEstimationAlgorithm);
		}
		if(countIterationsCurrent.equals(ParticleSwarmOptimization.countRef - 1)){
			ifFactoryGenerator = new FactoryGenerator();
			Strategy.getStrategy().generator = ifFactoryGenerator.createGenerator(GeneratorType.ParticleSwarmOptimization);
		}
	}

	/**
	 * Crea un generador mediante la factoría.
	 *
	 * @param Generatortype tipo de generador
	 * @return el generador creado
	 * @throws IllegalArgumentException si algún argumento no es válido
	 * @throws SecurityException si el gestor de seguridad impide la reflexión
	 * @throws ClassNotFoundException si la clase pedida a la factoría no existe o no está permitida
	 * @throws InstantiationException si la clase pedida a la factoría no se puede instanciar
	 * @throws IllegalAccessException si el constructor de la clase no es accesible
	 * @throws InvocationTargetException si el constructor de la clase lanza una excepción
	 * @throws NoSuchMethodException si la clase no tiene constructor sin argumentos
	 */
	public Generator newGenerator(GeneratorType Generatortype) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		ifFactoryGenerator = new FactoryGenerator();
		Generator generator = ifFactoryGenerator.createGenerator(Generatortype);
		return generator;
	}

	/**
	 * Devuelve el mejor estado encontrado.
	 *
	 * @return el mejor estado encontrado
	 */
	public State getBestState() {
		return bestState;
	}

	/**
	 * Establece el mejor estado encontrado.
	 *
	 * @param besState mejor estado
	 */
	public void setBestState(State besState) {
		this.bestState = besState;
	}
	/**
	 * Devuelve la condición de parada.
	 *
	 * @return la condición de parada
	 */
	public StopExecute getStopexecute() {
		return stopexecute;
	}

	/**
	 * Devuelve el número máximo de iteraciones.
	 *
	 * @return el número máximo de iteraciones
	 */
	public int getCountMax() {
		return countMax;
	}

	/**
	 * Establece el número máximo de iteraciones.
	 *
	 * @param countMax el número máximo de iteraciones
	 */
	public void setCountMax(int countMax) {
		this.countMax = countMax;
	}

	/**
	 * Establece la condición de parada.
	 *
	 * @param stopexecute la condición de parada
	 */
	public void setStopexecute(StopExecute stopexecute) {
		this.stopexecute = stopexecute;
	}

	/**
	 * Devuelve el actualizador de parámetros.
	 *
	 * @return el actualizador de parámetros
	 */
	public UpdateParameter getUpdateparameter() {
		return updateparameter;
	}

	/**
	 * Establece el actualizador de parámetros.
	 *
	 * @param updateparameter el actualizador de parámetros
	 */
	public void setUpdateparameter(UpdateParameter updateparameter) {
		this.updateparameter = updateparameter;
	}

	/**
	 * Devuelve el problema.
	 *
	 * @return el problema
	 */
	public Problem getProblem() {
		return problem;
	}

	/**
	 * Establece el problema.
	 *
	 * @param problem el problema
	 */
	public void setProblem(Problem problem) {
		this.problem = problem;
	}

	/**
	 * Obtiene los nombres de las claves del mapa.
	 *
	 * @return la lista de claves
	 */
	public ArrayList<String> getListKey(){
		ArrayList<String> listKeys = new ArrayList<String>();
		String key = mapGenerators.keySet().toString();
		String returnString = key.substring(1, key.length() - 1);
		returnString = returnString + ", ";
		int countKey = mapGenerators.size();
		for (int i = 0; i < countKey; i++) {
			String r = returnString.substring(0, returnString.indexOf(','));
			returnString = returnString.substring(returnString.indexOf(',') + 2);
			listKeys.add(r);
		}
		return listKeys;
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
	public void initializeGenerators()throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		List<GeneratorType>	listType = new ArrayList<GeneratorType>();
		this.mapGenerators = new TreeMap<GeneratorType, Generator>();
		GeneratorType type[]= GeneratorType.values();
		for (GeneratorType generator : type) {
			listType.add(generator);
		}
		for (int i = 0; i < listType.size(); i++) {
			Generator generator = newGenerator(listType.get(i));
			mapGenerators.put(listType.get(i), generator);
		}
	}

	/**
	 * Crea el mapa con una instancia de cada tipo de generador.
	 *
	 * @throws IllegalArgumentException si algún argumento no es válido
	 * @throws SecurityException si el gestor de seguridad impide la reflexión
	 * @throws ClassNotFoundException si la clase pedida a la factoría no existe o no está permitida
	 * @throws InstantiationException si la clase pedida a la factoría no se puede instanciar
	 * @throws IllegalAccessException si el constructor de la clase no es accesible
	 * @throws InvocationTargetException si el constructor de la clase lanza una excepción
	 * @throws NoSuchMethodException si la clase no tiene constructor sin argumentos
	 */
	public void initialize()throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		List<GeneratorType>	listType = new ArrayList<GeneratorType>();
		this.mapGenerators = new TreeMap<GeneratorType, Generator>();
		GeneratorType type[]= GeneratorType.values();
		for (GeneratorType generator : type) {
			listType.add(generator);
		}
		for (int i = 0; i < listType.size(); i++) {
			Generator generator = newGenerator(listType.get(i));
			mapGenerators.put(listType.get(i), generator);
		}
	}

	/**
	 * Devuelve la iteración actual.
	 *
	 * @return la iteración actual
	 */
	public int getCountCurrent() {
		return countCurrent;
	}

	/**
	 * Establece la iteración actual.
	 *
	 * @param countCurrent la iteración actual
	 */
	public void setCountCurrent(int countCurrent) {
		this.countCurrent = countCurrent;
	}

	/** Destruye la estrategia actual para poder iniciar una ejecución nueva. */
	public static void destroyExecute() {
		strategy = null;
		RandomSearch.listStateReference = null;
	}
	
	/**
	 * Devuelve el umbral de aceptación.
	 *
	 * @return el umbral de aceptación
	 */
	public double getThreshold() {
		return threshold;
	}

	/**
	 * Establece el umbral de aceptación.
	 *
	 * @param threshold el umbral de aceptación
	 */
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	
	/**
	 * Calcula y guarda la métrica offline performance del periodo actual.
	 *
	 * @param sumMax suma de las mejores evaluaciones del periodo
	 * @param countOff posición en la que guardar la métrica
	 */
	public void calculateOffLinePerformance(float sumMax, int countOff){
		float off = sumMax / countPeriodChange;
		listOfflineError[countOff] = off;
	}
	
	/**
	 * Actualiza la referencia del generador tras un cambio en el problema dinámico.
	 *
	 * @param generatorType tipo de generador
	 */
	public void updateRef(GeneratorType generatorType){
		if(generatorType.equals(GeneratorType.MultiGenerator)){
			updateRefMultiG();
			bestState = MultiGenerator.listStateReference.get( MultiGenerator.listStateReference.size() - 1);
		}
		else{
			updateRefGenerator(generator);
			bestState = generator.getReference();
		}
	}
	/** Reevalúa las referencias de todos los generadores del portafolio. */
	public void updateRefMultiG() {
		for (int i = 0; i < MultiGenerator.getListGenerators().length; i++) {
			updateRefGenerator(MultiGenerator.getListGenerators()[i]);
		}
	}
	/**
	 * Reevalúa la referencia de un generador tras un cambio en el problema dinámico.
	 *
	 * @param generator el generador
	 */
	public void updateRefGenerator(Generator generator) {
		if(generator.getType().equals(GeneratorType.HillClimbing) || generator.getType().equals(GeneratorType.HillClimbingRestart) || generator.getType().equals(GeneratorType.TabuSearch) || generator.getType().equals(GeneratorType.RandomSearch) || generator.getType().equals(GeneratorType.SimulatedAnnealing)){
			double evaluation = getProblem().getFunction().get(0).Evaluation(generator.getReference());
			generator.getReference().getEvaluation().set(0, evaluation);
			
		}
		if(generator.getType().equals(GeneratorType.GeneticAlgorithm) || generator.getType().equals(GeneratorType.DistributionEstimationAlgorithm) || generator.getType().equals(GeneratorType.EvolutionStrategies)){
			for (int j = 0; j < generator.getReferenceList().size(); j++) {
				double evaluation = getProblem().getFunction().get(0).Evaluation(generator.getReferenceList().get(j));
				generator.getReferenceList().get(j).getEvaluation().set(0, evaluation);
			}
		}
	}

}
