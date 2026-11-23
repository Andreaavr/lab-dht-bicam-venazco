package evolutionary_algorithms.complement;


import java.util.ArrayList;
import java.util.List;

import metaheurictics.strategy.Strategy;

import problem.definition.State;
import problem.definition.Problem.ProblemType;

/** Selección por truncamiento: elige los k mejores individuos de la población. */
public class TruncationSelection extends FatherSelection {
	
	/**
	 * Ordena la lista de estados de mayor a menor evaluación.
	 *
	 * @param listState población de estados
	 * @return la lista ordenada
	 */
	public List<State> OrderBetter (List<State> listState){
		State var = null;
		for (int i = 0; i < listState.size()- 1; i++) {
			for (int j = i+1; j < listState.size(); j++) {
				if(listState.get(i).getEvaluation().get(0) < listState.get(j).getEvaluation().get(0)){
					var = listState.get(i);
					listState.set(i, listState.get(j));
					listState.set(j,var);
				}
			}
		}
		return listState;
	}
	
	/**
	 * Ordena la lista de estados de menor a mayor evaluación.
	 *
	 * @param listState población de estados
	 * @return la lista ordenada
	 */
	public List<State> ascOrderBetter (List<State> listState){
		State var = null;
		for (int i = 0; i < listState.size()- 1; i++) {
			for (int j = i+1; j < listState.size(); j++) {
				if(listState.get(i).getEvaluation().get(0) > listState.get(j).getEvaluation().get(0)){
					var = listState.get(i);
					listState.set(i, listState.get(j));
					listState.set(j,var);
				}
			}
		}
		return listState;
	}
    
	/**
	 * Selecciona padres de la población.
	 *
	 * @param listState población de estados
	 * @param truncation número de padres a seleccionar
	 * @return la lista de padres seleccionados
	 */
	@Override
	public List<State> selection(List<State> listState, int truncation) {
		List<State> AuxList = new ArrayList<State>();
		if (Strategy.getStrategy().getProblem().getTypeProblem().equals(ProblemType.Maximizar)) {
			listState = OrderBetter(listState);
		} else {
			if(Strategy.getStrategy().getProblem().getTypeProblem().equals(ProblemType.Minimizar))
				listState = ascOrderBetter(listState);
		}
		int i = 0;
		while(AuxList.size()< truncation){
			AuxList.add(listState.get(i));
			i++;
		}
		return AuxList;
	}
}
