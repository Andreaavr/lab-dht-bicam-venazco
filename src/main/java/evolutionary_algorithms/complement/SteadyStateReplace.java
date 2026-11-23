package evolutionary_algorithms.complement;


import java.util.List;
import metaheurictics.strategy.Strategy;
import problem.definition.State;
import problem.definition.Problem.ProblemType;


/** Reemplazo de estado estacionario: el candidato sustituye al peor individuo si lo mejora. */
public class SteadyStateReplace extends Replace {

	/**
	 * Incorpora el candidato a la población según la política de reemplazo.
	 *
	 * @param stateCandidate estado candidato
	 * @param listState población de estados
	 * @return la población actualizada
	 */
	@Override
	public List<State> replace(State stateCandidate, List<State> listState) {
		State stateREP = null;
		if (Strategy.getStrategy().getProblem().getTypeProblem().equals(ProblemType.Maximizar)) {
			stateREP = MinValue(listState);
			if(stateCandidate.getEvaluation().get(0) >= stateREP.getEvaluation().get(0)){
				Boolean find = false;
		        int count = 0;
		        while ((find.equals(false)) && (listState.size() > count)){
		        	if(listState.get(count).equals(stateREP)){
		        		listState.remove(count);
						listState.add(count, stateCandidate);
						find = true;
					}
		        	else count ++;
				}
			}
		}
		else {
			if(Strategy.getStrategy().getProblem().getTypeProblem().equals(ProblemType.Minimizar)){
				stateREP = MaxValue(listState);
				if(stateCandidate.getEvaluation().get(0) <= stateREP.getEvaluation().get(0)){
					Boolean find = false;
			        int count = 0;
			        while ((find.equals(false)) && (listState.size() > count)){
			        	if(listState.get(count).equals(stateREP)){
			        		listState.remove(count);
							listState.add(count, stateCandidate);
							find = true;
						}
			        	else count ++;
					}
				}
			}
		}
		return listState;
	}
	
	/**
	 * Busca el estado de menor evaluación.
	 *
	 * @param listState población de estados
	 * @return el estado con la evaluación mínima
	 */
	public State MinValue (List<State> listState){
		State value = listState.get(0);
		double min = listState.get(0).getEvaluation().get(0);
		for (int i = 1; i < listState.size(); i++) {
			if(listState.get(i).getEvaluation().get(0) < min){
				min = listState.get(i).getEvaluation().get(0);
				value = listState.get(i);
			}
		}
		return value;
	}
	
	/**
	 * Busca el estado de mayor evaluación.
	 *
	 * @param listState población de estados
	 * @return el estado con la evaluación máxima
	 */
	public State MaxValue (List<State> listState){
		State value = listState.get(0);
		double max = listState.get(0).getEvaluation().get(0);
		for (int i = 1; i < listState.size(); i++) {
			if(listState.get(i).getEvaluation().get(0) > max){
				max = listState.get(i).getEvaluation().get(0);
				value = listState.get(i);
			}
		}
		return value;
	}
}
