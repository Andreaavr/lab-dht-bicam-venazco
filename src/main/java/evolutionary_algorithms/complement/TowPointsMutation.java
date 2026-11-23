package evolutionary_algorithms.complement;



import metaheurictics.strategy.Strategy;
import problem.definition.State;
/** Mutación de dos puntos: asigna valores aleatorios a dos variables elegidas al azar. */
public class TowPointsMutation extends Mutation {

	

	/**
	 * Muta un individuo.
	 *
	 * @param newind individuo a mutar
	 * @param PM probabilidad de mutación
	 * @return el individuo mutado
	 */
	@Override
	public State mutation(State newind, double PM) {
		Object key1 = Strategy.getStrategy().getProblem().getCodification().getAleatoryKey();
		Object key2 = Strategy.getStrategy().getProblem().getCodification().getAleatoryKey();
		Object value1 = Strategy.getStrategy().getProblem().getCodification().getVariableAleatoryValue((Integer) key1);
		Object value2 = Strategy.getStrategy().getProblem().getCodification().getVariableAleatoryValue((Integer) key2);
		newind.getCode().set((Integer) key1, (Integer)value2);
		newind.getCode().set((Integer) key2, (Integer)value1);
		return newind;
	}
}
  