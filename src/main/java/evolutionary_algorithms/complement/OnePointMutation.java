package evolutionary_algorithms.complement;

import util.RandomUtil;


import metaheurictics.strategy.Strategy;
import problem.definition.State;

/** Mutación de un punto: con probabilidad PM cambia el valor de una variable aleatoria. */
public class OnePointMutation extends Mutation {

	/**
	 * Muta un individuo.
	 *
	 * @param state estado
	 * @param PM probabilidad de mutación
	 * @return el individuo mutado
	 */
	@Override
	public State mutation(State state, double PM) {
		double probM = (double)(RandomUtil.nextDouble() * (double)(1));
		if(PM >= probM)
		{
			Object key = Strategy.getStrategy().getProblem().getCodification().getAleatoryKey();
			Object value = Strategy.getStrategy().getProblem().getCodification().getVariableAleatoryValue((Integer)key);
			state.getCode().set((Integer) key, value);
		}
		return state;
	}
}
