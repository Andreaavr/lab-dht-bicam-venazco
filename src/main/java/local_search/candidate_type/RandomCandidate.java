package local_search.candidate_type;

import util.RandomUtil;

import java.util.List;

import problem.definition.State;

/** Elige un vecino al azar. */
public class RandomCandidate extends SearchCandidate {

	/**
	 * Elige un candidato de la vecindad.
	 *
	 * @param listNeighborhood vecindad de la que elegir
	 * @return el estado elegido
	 */
	@Override
	public State stateSearch(List<State> listNeighborhood) {
		int pos = RandomUtil.nextInt(listNeighborhood.size());
		State stateAleatory = listNeighborhood.get(pos);
		return stateAleatory;
	}
}
