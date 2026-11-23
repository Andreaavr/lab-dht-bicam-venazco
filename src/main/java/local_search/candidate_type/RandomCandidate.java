/**
 * @(#) AleatoryCandidate.java
 */

package local_search.candidate_type;

import util.RandomUtil;

import java.util.List;

import problem.definition.State;

public class RandomCandidate extends SearchCandidate {

	@Override
	public State stateSearch(List<State> listNeighborhood) {
		int pos = (int)(RandomUtil.nextDouble() * (double)(listNeighborhood.size() - 1));
		State stateAleatory = listNeighborhood.get(pos);
		return stateAleatory;
	}
}
