package local_search.candidate_type;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import local_search.acceptation_type.Dominance;
import metaheurictics.strategy.Strategy;

import problem.definition.State;

/** Elige un vecino no dominado recorriendo la vecindad. */
public class NotDominatedCandidate extends SearchCandidate {

	/**
	 * Elige un candidato de la vecindad.
	 *
	 * @param listNeighborhood vecindad de la que elegir
	 * @return el estado elegido
	 * @throws IllegalArgumentException si algún argumento no es válido
	 * @throws SecurityException si el gestor de seguridad impide la reflexión
	 * @throws ClassNotFoundException si la clase pedida a la factoría no existe o no está permitida
	 * @throws InstantiationException si la clase pedida a la factoría no se puede instanciar
	 * @throws IllegalAccessException si el constructor de la clase no es accesible
	 * @throws InvocationTargetException si el constructor de la clase lanza una excepción
	 * @throws NoSuchMethodException si la clase no tiene constructor sin argumentos
	 */
	@Override
	public State stateSearch(List<State> listNeighborhood) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		State state = new State();
		State stateA = listNeighborhood.get(0);
		boolean stop = false;
		if(listNeighborhood.size() == 1){
			stop = true;
			state = stateA;
		}
		else {
			Strategy.getStrategy().getProblem().Evaluate(stateA);
			State stateB = new State();
			Dominance dominance = new Dominance();
			for (int i = 1; i < listNeighborhood.size(); i++) {
				while(!stop){
					stateB = listNeighborhood.get(i);
					Strategy.getStrategy().getProblem().Evaluate(stateB);
					if(dominance.dominance(stateB, stateA)){
						stateA = stateB;
					}else{
						stop = true;
						state = stateA;
					}
				}
			}
		}
		return state;
	}

}
