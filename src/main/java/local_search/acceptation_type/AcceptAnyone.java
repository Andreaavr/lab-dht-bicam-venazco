package local_search.acceptation_type;

import problem.definition.State;


/** Criterio de aceptación que acepta cualquier candidato. */
public class AcceptAnyone extends AcceptableCandidate{

	/**
	 * Decide si el candidato sustituye a la solución actual.
	 *
	 * @param stateCurrent estado actual
	 * @param stateCandidate estado candidato
	 * @return {@code true} si el candidato es aceptado
	 */
	@Override
	public Boolean acceptCandidate(State stateCurrent, State stateCandidate) {
		Boolean accept = true;
		return accept;
	}
	
}
