package evolutionary_algorithms.complement;

import util.RandomUtil;


import metaheurictics.strategy.Strategy;
import problem.definition.State;

public class OnePointMutation extends Mutation {

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
