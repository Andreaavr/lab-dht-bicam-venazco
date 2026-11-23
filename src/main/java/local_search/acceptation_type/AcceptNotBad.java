package local_search.acceptation_type;

import metaheurictics.strategy.Strategy;
import problem.definition.Problem;
import problem.definition.State;
import problem.definition.Problem.ProblemType;

/** Criterio de aceptación que acepta candidatos no peores que la solución actual. */
public class AcceptNotBad extends AcceptableCandidate{

	/**
	 * Decide si el candidato sustituye a la solución actual.
	 *
	 * @param stateCurrent estado actual
	 * @param stateCandidate estado candidato
	 * @return {@code true} si el candidato es aceptado
	 */
	@Override
	public Boolean acceptCandidate(State stateCurrent, State stateCandidate) {
		Boolean accept = false;
		Problem problem = Strategy.getStrategy().getProblem();
		for (int i = 0; i < problem.getFunction().size(); i++) {
		if (problem.getTypeProblem().equals(ProblemType.Maximizar)) {
			if (stateCandidate.getEvaluation().get(0) >= stateCurrent.getEvaluation().get(0)) {
				accept = true;
			} else {
				accept = false;
			}
		} else {
			if (stateCandidate.getEvaluation().get(0) <= stateCurrent.getEvaluation().get(0)) {
				accept = true;
			} else {
				accept = false;
			}
		}}
		return accept;
	}
}
