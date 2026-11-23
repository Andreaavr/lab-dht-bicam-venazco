package metaheuristics.generators;

import util.RandomUtil;
import java.util.List;
import metaheurictics.strategy.Strategy;
import problem.definition.Problem.ProblemType;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;


import problem.definition.State;

/** Partícula del algoritmo de optimización por enjambre (PSO). */
public class Particle extends Generator {

	/** La mejor posición de la partícula. */
	private State statePBest;
	/** La posición actual de la partícula. */
	private State stateActual;
	/** La velocidad de la partícula. */
	private ArrayList<Object> velocity;
	
	
	/** Crea una instancia de Particle. */
	public Particle() {
		super();
		this.stateActual = new State();
		this.statePBest = new State();
		this.velocity = new ArrayList<Object>();
	}
	
	/**
	 * Crea una instancia de Particle con los valores indicados.
	 *
	 * @param statePBest la mejor posición de la partícula
	 * @param stateActual la posición actual de la partícula
	 * @param velocity la velocidad de la partícula
	 */
	public Particle(State statePBest, State stateActual, ArrayList<Object> velocity) {
		super();
		this.statePBest = statePBest;
		this.stateActual = stateActual;
		this.velocity = velocity;
	}

	/**
	 * Devuelve la velocidad de la partícula.
	 *
	 * @return la velocidad de la partícula
	 */
	public ArrayList<Object> getVelocity() {
		return velocity;
	}

	/**
	 * Establece la velocidad de la partícula.
	 *
	 * @param velocity la velocidad de la partícula
	 */
	public void setVelocity(ArrayList<Object> velocity) {
		this.velocity = velocity;
	}

	/**
	 * Devuelve la mejor posición de la partícula.
	 *
	 * @return la mejor posición de la partícula
	 */
	public State getStatePBest() {
		return statePBest;
	}

	/**
	 * Establece la mejor posición de la partícula.
	 *
	 * @param statePBest la mejor posición de la partícula
	 */
	public void setStatePBest(State statePBest) {
		this.statePBest = statePBest;
	}

	/**
	 * Devuelve la posición actual de la partícula.
	 *
	 * @return la posición actual de la partícula
	 */
	public State getStateActual() {
		return stateActual;
	}

	/**
	 * Establece la posición actual de la partícula.
	 *
	 * @param stateActual la posición actual de la partícula
	 */
	public void setStateActual(State stateActual) {
		this.stateActual = stateActual;
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
		
		ArrayList<Object> actualVelocity = UpdateVelocity();
		ArrayList<Object> newCode = UpdateCode(actualVelocity);
		this.velocity = actualVelocity;
		this.stateActual.setCode(newCode);
		return null;
	}
	
	
	/**
	 * Actualiza la velocidad de la partícula según su mejor posición y la del enjambre.
	 *
	 * @return la nueva velocidad
	 */
	private ArrayList<Object> UpdateVelocity(){ // actualizar velocidad
    	double w = ParticleSwarmOptimization.wmax - ((ParticleSwarmOptimization.wmax - ParticleSwarmOptimization.wmin) / Strategy.getStrategy().getCountMax()) * ParticleSwarmOptimization.countCurrentIterPSO;  //CALCULO DE LA INERCIA
    	double rand1 = (double)(RandomUtil.nextDouble() * (double)(1));
    	double rand2 = (double)(RandomUtil.nextDouble() * (double)(1));
    	double inertia, cognitive, social;
    	int learning = ParticleSwarmOptimization.learning1 + ParticleSwarmOptimization.learning2; // ratios de aprendizaje cognitivo y social
    	ParticleSwarmOptimization.constriction = 2/(Math.abs(2 - learning-Math.sqrt((learning * learning)- 4 * learning)));   // Factor de costriccion
    	ArrayList<Object> actualVelocity = new ArrayList<Object>();
    	if(velocity.isEmpty()){
    		for (int i = 0; i < Strategy.getStrategy().getProblem().getState().getCode().size(); i++){
    			velocity.add(0.0);
    		}
    	}
    	// recorre el vector velocidad y lo actualiza
    	for (int i = 0; i < Strategy.getStrategy().getProblem().getState().getCode().size(); i++) {  
    		// cumulo donde se encuentra la particula
    		int swarm = ParticleSwarmOptimization.countParticle / ParticleSwarmOptimization.countParticleBySwarm; 
           	inertia = w * (Double)velocity.get(i);  
           	if(ParticleSwarmOptimization.binary){
           		cognitive = (Double)(ParticleSwarmOptimization.learning1 * rand1 * ((Integer)(this.statePBest.getCode().get(i)) - (Integer)(stateActual.getCode().get(i))));
           		social = (Double)(ParticleSwarmOptimization.learning2 * rand2 * (((Integer)(((State) ParticleSwarmOptimization.lBest[swarm]).getCode().get(i))) - ((Integer)(stateActual.getCode().get(i)))));
           	}
           	else{
           		cognitive = (Double)(ParticleSwarmOptimization.learning1 * rand1 * ((Double)(this.statePBest.getCode().get(i)) - (Double)(stateActual.getCode().get(i))));
           		social = (Double)(ParticleSwarmOptimization.learning2 * rand2 * (((Double)(((State) ParticleSwarmOptimization.lBest[swarm]).getCode().get(i))) - ((Double)(stateActual.getCode().get(i)))));
           	}
        	actualVelocity.add(ParticleSwarmOptimization.constriction*(inertia + cognitive + social));
        }
    
        return actualVelocity;
    }
	
	/**
	 * Actualiza el código (posición) de la partícula a partir de su velocidad.
	 *
	 * @param actualVelocity velocidad actual
	 * @return el resultado de la operación
	 */
	private ArrayList<Object> UpdateCode(ArrayList<Object> actualVelocity) {  // CALCULO DE LA NUEA POSICION DE LA PARTICULA
		ArrayList<Object> newCode = new ArrayList<Object>();
		ArrayList<Object> binaryCode = new ArrayList<Object>();
		//poner la condicion de si se esta trabajando con valores continuos o binarios
		if(!ParticleSwarmOptimization.binary){	
			for (int i = 0; i < stateActual.getCode().size(); i++) {
					newCode.add( (Double)(stateActual.getCode().get(i)) + (Double)(actualVelocity.get(i)) );
		    }
			return newCode;
	    }
		 else{                                                  //cálculo de la posicion para codificacion binaria
			  for (int i = 0; i < stateActual.getCode().size(); i++){
				  double rand = (double)(RandomUtil.nextDouble() * (double)(1));
				  double s = 1/(1 + 1.72 * (Double)(actualVelocity.get(i))); // 
				  if (rand < s){
				     binaryCode.add(1);
				  }
				   else{
				    	binaryCode.add(0);
				    	}
			  }
	          return binaryCode;
    }
			
		
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
		if(Strategy.getStrategy().getProblem().getTypeProblem().equals(ProblemType.Maximizar)){
			if(stateActual.getEvaluation().get(0) > statePBest.getEvaluation().get(0)){
				statePBest.setCode(new ArrayList<Object>(stateActual.getCode()));
				statePBest.setEvaluation(stateActual.getEvaluation());
			}
		}
		else{
			if(stateCandidate.getEvaluation().get(0) < statePBest.getEvaluation().get(0)){
				statePBest.setCode(new ArrayList<Object>(stateCandidate.getCode()));
				statePBest.setEvaluation(stateCandidate.getEvaluation());
			}
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
		return null;
	}

	/**
	 * Devuelve la lista de estados de referencia del generador.
	 *
	 * @return la lista de referencias
	 */
	@Override
	public List<State> getReferenceList() {
		return null;
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
