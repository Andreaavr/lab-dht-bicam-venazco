package problem_operators;

import java.util.ArrayList;
import java.util.List;

import metaheurictics.strategy.Strategy;

import problem.definition.Codification;
import problem.definition.Operator;
import problem.definition.State;


/** Operador de vecindad genérico basado en la mutación de una variable aleatoria. */
public class MutationOperator extends Operator {

	/**
	 * Genera vecinos de un estado.
	 *
	 * @param stateCurrent estado actual
	 * @param operatornumber número de vecinos o estados a generar
	 * @return la lista de vecinos
	 */
	public List<State> generatedNewState(State stateCurrent, Integer operatornumber){
		List<State> listNeigborhood = new ArrayList<State>();
		for (int i = 0; i < operatornumber; i++){
			int key = Strategy.getStrategy().getProblem().getCodification().getAleatoryKey();
			Object candidate = Strategy.getStrategy().getProblem().getCodification().getVariableAleatoryValue(key);
			State state = (State) stateCurrent.getCopy();
			state.getCode().set(key, candidate);
			listNeigborhood.add(state);
		}
		return listNeigborhood;
	}

	/**
	 * Genera estados aleatorios del problema: cada variable toma un valor aleatorio
	 * de la codificación.
	 *
	 * @param operatornumber número de vecinos o estados a generar
	 * @return la lista de estados generados (nunca {@code null})
	 */
	@Override
	public List<State> generateRandomState(Integer operatornumber) {
		List<State> states = new ArrayList<State>();
		Codification codification = Strategy.getStrategy().getProblem().getCodification();
		for (int i = 0; i < operatornumber; i++) {
			ArrayList<Object> code = new ArrayList<Object>();
			for (int key = 0; key < codification.getVariableCount(); key++) {
				code.add(codification.getVariableAleatoryValue(key));
			}
			states.add(new State(code));
		}
		return states;
	}

}
