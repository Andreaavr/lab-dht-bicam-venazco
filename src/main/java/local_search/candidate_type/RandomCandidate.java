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
		int pos = RandomUtil.nextInt(listNeighborhood.size());
		State stateAleatory = listNeighborhood.get(pos);
		return stateAleatory;
	}
}
