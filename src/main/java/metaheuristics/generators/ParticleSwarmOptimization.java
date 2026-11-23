package metaheuristics.generators;

import java.lang.reflect.InvocationTargetException;
import metaheurictics.strategy.Strategy;
import problem.definition.Problem.ProblemType;

import java.util.ArrayList;
import java.util.List;




import problem.definition.State;

/** Optimización por enjambre de partículas (PSO). */
public class ParticleSwarmOptimization extends Generator {
	
	/** El estado de referencia. */
	private State stateReferencePSO;
	/** La lista de estados de referencia. */
	private List<State> listStateReference = new ArrayList<State>(); 
	/** Las partículas del enjambre. */
	private List<Particle> listParticle =  new ArrayList<Particle> ();
	/** El tipo de generador. */
	private GeneratorType generatorType;
	/** El tamaño de la población de referencia. */
	public static int countRef = 0;            // CANTIDAD DE PARTICULAS TOTAL = coutSwarm * countParticleSwarm
	/** El número de partículas movidas. */
	public static int countParticle = 0;       // CANTIDAD DE PARTICULAS QUE SE HAN MOVIDO EN CADA CUMULO
	/** El número de cúmulos. */
	public static int coutSwarm = 0;           //CANTIDAD DE CUMULOS
	/** El número de partículas por cúmulo. */
	public static int countParticleBySwarm = 0; //CANTIDAD DE PARTICULAS POR CUMULO
	/** El peso. */
	private float weight = 50;
	/** La inercia máxima. */
	public static double wmax = 0.9;
	/** La inercia mínima. */
	public static double wmin = 0.2;
	/** Los coeficientes de aprendizaje. */
	public static int learning1 = 2, learning2 = 2;
	/** El coeficiente de constricción. */
	public static double constriction;
	/** El indicador de PSO binario. */
	public static boolean binary = false;
	/** Las mejores posiciones locales de cada cúmulo. */
	public static State[] lBest; 
	/** La mejor posición global del enjambre. */
	public static State gBest;
	/** La iteración actual del PSO. */
	public static int countCurrentIterPSO;
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
			
	/** Crea una instancia de ParticleSwarmOptimization. */
	public ParticleSwarmOptimization(){
		super();
		countRef = coutSwarm * countParticleBySwarm;
		this.setListParticle(getListStateRef()); 
		this.generatorType = GeneratorType.ParticleSwarmOptimization;
		this.weight = 50;
		lBest = new State[coutSwarm];
		if(!listParticle.isEmpty()){
			countCurrentIterPSO = 1;
			inicialiceLBest();
			gBest = gBestInicial();	
		}
		countParticle = 0;
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
	public State generate(Integer operatornumber) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{ //PSO
		if (countParticle >= countRef)
			countParticle = 0;
		listParticle.get(countParticle).generate(1);
	    return listParticle.get(countParticle).getStateActual();
	}
   	
	/** Inicializa la mejor posición local de cada cúmulo. */
	public void inicialiceLBest (){
		for (int j = 0; j < coutSwarm; j++) {
			State reference = new State();
			reference = listParticle.get(countParticle).getStatePBest();
			int iterator = countParticleBySwarm + countParticle;
			if(Strategy.getStrategy().getProblem().getTypeProblem().equals(ProblemType.Maximizar)){
				for (int i = countParticle; i < iterator; i++) {
					if (listParticle.get(i).getStatePBest().getEvaluation().get(0) > reference.getEvaluation().get(0))
						reference = listParticle.get(i).getStatePBest();
					countParticle++;
				}
			}
			else{
				for (int i = countParticle; i < iterator; i++) {
					if (listParticle.get(i).getStatePBest().getEvaluation().get(0) < reference.getEvaluation().get(0))
						reference = listParticle.get(i).getStatePBest();
					countParticle++;
				}
			}
			
			lBest[j] = reference;
		}
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
	 * Obtiene la población inicial de referencia a partir de los estados generados por la búsqueda aleatoria.
	 *
	 * @return la población de referencia
	 */
	private List<Particle> getListStateRef() {
		Boolean found = false;
		List<String> key = Strategy.getStrategy().getListKey();
		int count = 0;
		if(RandomSearch.listStateReference.size() == 0){
			return this.setListParticle(new ArrayList<Particle>());
		}
		while((found.equals(false)) && (Strategy.getStrategy().mapGenerators.size() > count)){
			//recorrer la lista de generadores, hasta que encuentre el PSO
			if(key.get(count).equals(GeneratorType.ParticleSwarmOptimization.toString())){
				//creo el generador PSO, y si su lista de particulas esta vacia entonces es la primera vez que lo estoy creando, y cada estado lo convierto en particulas
				GeneratorType keyGenerator = GeneratorType.valueOf(String.valueOf(key.get(count)));
				ParticleSwarmOptimization generator = (ParticleSwarmOptimization) Strategy.getStrategy().mapGenerators.get(keyGenerator);
				if(generator.getListParticle().isEmpty()){
					//convertir los estados en particulas
					for (int j = 0; j < RandomSearch.listStateReference.size(); j++) {
						//si el estado es creado con el generator RandomSearch entonces la convierto en particula
						if(getListParticle().size() != countRef){
							ArrayList<Object> velocity = new ArrayList<Object>();
							State stateAct = (State) RandomSearch.listStateReference.get(j).getCopy();
							stateAct.setCode(new ArrayList<Object>(RandomSearch.listStateReference.get(j).getCode()));
							stateAct.setEvaluation(RandomSearch.listStateReference.get(j).getEvaluation());
							
							State statePBest = (State) RandomSearch.listStateReference.get(j).getCopy();
							statePBest.setCode(new ArrayList<Object>(RandomSearch.listStateReference.get(j).getCode()));
							statePBest.setEvaluation(RandomSearch.listStateReference.get(j).getEvaluation());
							
							Particle particle = new Particle(stateAct, statePBest, velocity);
							getListParticle().add(particle);
						}
					}  
				}
				else{
					setListParticle(generator.getListStateReference());
				}
			        found = true;
			}
			count++;
		}
		return getListParticle();
	}


	/**
	 * Devuelve el estado de referencia.
	 *
	 * @return el estado de referencia
	 */
	public State getStateReferencePSO() {
		return stateReferencePSO;
	}

	/**
	 * Establece el estado de referencia.
	 *
	 * @param stateReferencePSO el estado de referencia
	 */
	public void setStateReferencePSO(State stateReferencePSO) {
		this.stateReferencePSO = stateReferencePSO;
	}

	/**
	 * Devuelve la lista de estados de referencia.
	 *
	 * @return la lista de estados de referencia
	 */
	public List<Particle> getListStateReference() {
		return this.getListParticle();
	}

	/**
	 * Establece la lista de estados de referencia.
	 *
	 * @param listStateReference la lista de estados de referencia
	 */
	public void setListStateReference(List<State> listStateReference) {
		this.listStateReference = listStateReference;
	}

	/**
	 * Devuelve las partículas del enjambre.
	 *
	 * @return las partículas del enjambre
	 */
	public List<Particle> getListParticle() {
		return listParticle;
	}

	/**
	 * Establece las partículas del enjambre.
	 *
	 * @param listParticle las partículas del enjambre
	 * @return el resultado de la operación
	 */
	public List<Particle> setListParticle(List<Particle> listParticle) {
		this.listParticle = listParticle;
		return listParticle;
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
	 * Devuelve el tamaño de la población de referencia.
	 *
	 * @return el tamaño de la población de referencia
	 */
	public static int getCountRef() {
		return countRef;
	}

	/**
	 * Establece el tamaño de la población de referencia.
	 *
	 * @param countRef el tamaño de la población de referencia
	 */
	public static void setCountRef(int countRef) {
		ParticleSwarmOptimization.countRef = countRef;
	}


	//*****************************************
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
	public void updateReference(State stateCandidate,Integer countIterationsCurrent) throws IllegalArgumentException,SecurityException, ClassNotFoundException, InstantiationException,IllegalAccessException, InvocationTargetException,NoSuchMethodException {
		Particle particle = new Particle();
		particle = listParticle.get(countParticle);
		int swarm = countParticle/countParticleBySwarm;
		if(Strategy.getStrategy().getProblem().getTypeProblem().equals(ProblemType.Maximizar)){
			if ((lBest[swarm]).getEvaluation().get(0) < particle.getStatePBest().getEvaluation().get(0)){
				lBest[swarm] = particle.getStatePBest();
				if(lBest[swarm].getEvaluation().get(0) > getReferenceList().get(getReferenceList().size() - 1).getEvaluation().get(0)){
					gBest = new State();
					gBest.setCode(new ArrayList<Object>(lBest[swarm].getCode()));
					gBest.setEvaluation(lBest[swarm].getEvaluation());
					gBest.setTypeGenerator(lBest[swarm].getTypeGenerator());
				}
			}
		}
		else {
			particle.updateReference(stateCandidate, countIterationsCurrent);
			if ((lBest[swarm]).getEvaluation().get(0) > particle.getStatePBest().getEvaluation().get(0)){
				lBest[swarm] = particle.getStatePBest();
				if(lBest[swarm].getEvaluation().get(0) < getReferenceList().get(getReferenceList().size() - 1).getEvaluation().get(0)){
					gBest = new State();
					gBest.setCode(new ArrayList<Object>(lBest[swarm].getCode()));
					gBest.setEvaluation(lBest[swarm].getEvaluation());
					gBest.setTypeGenerator(lBest[swarm].getTypeGenerator());
				}
			}
		}
		listStateReference.add(gBest);
		countParticle++;
		countCurrentIterPSO++;
	}
	
	/**
	 * Obtiene la mejor posición inicial de todo el enjambre.
	 *
	 * @return la mejor posición global
	 */
	public State gBestInicial (){
		State stateBest = lBest[0];
		for (int i = 1; i < lBest.length; i++) {
			if(Strategy.getStrategy().getProblem().getTypeProblem().equals(ProblemType.Maximizar)){
				if (lBest[i].getEvaluation().get(0) > stateBest.getEvaluation().get(0)){
					stateBest = lBest[i];
				}
			}
			else{
				if (lBest[i].getEvaluation().get(0) < stateBest.getEvaluation().get(0)){
					stateBest = lBest[i];
				}
			}
		}
		return stateBest;
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
		return this.listStateReference;
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
	 * Establece el peso.
	 *
	 * @param weight el peso
	 */
	@Override
	public void setWeight(float weight) {

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
