package local_search.acceptation_type;

import util.RandomUtil;


import metaheuristics.generators.*;
import metaheurictics.strategy.*;

import java.util.List;

import problem.definition.State;

/** Criterio de aceptación multicaso del recocido simulado multiobjetivo (MC-MOSA). */
public class AcceptMulticase extends AcceptableCandidate {

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
		List<State> list = Strategy.getStrategy().listRefPoblacFinal;
		
		if(list.size() == 0){
			list.add(stateCurrent.clone());
		}
		Double T = MultiCaseSimulatedAnnealing.tinitial;
		double pAccept = 0;
		Dominance dominance= new Dominance();
		//Verificando si la solución candidata domina a la solución actual
		//Si la solución candidata domina a la solución actual
		if(dominance.dominance(stateCandidate, stateCurrent)){
			//Se asigna como solución actual la solución candidata con probabilidad 1
			pAccept = 1; 
		}
		else if(!dominance.dominance(stateCandidate, stateCurrent)){	
			if(DominanceCounter(stateCandidate, list) > 0){
				pAccept = 1;
			}
			else if(DominanceRank(stateCandidate, list) == 0){
				pAccept = 1;
			}
			else if(DominanceRank(stateCandidate, list) < DominanceRank(stateCurrent, list)){
				pAccept = 1;
			}
			else if(DominanceRank(stateCandidate, list) == DominanceRank(stateCurrent, list)){
				//Calculando la probabilidad de aceptación
				List<Double> evaluations = stateCurrent.getEvaluation();
				double total = 0;
				for (int i = 0; i < evaluations.size()-1; i++) {
					Double evalA = evaluations.get(i);
					Double evalB = stateCandidate.getEvaluation().get(i);
					if (evalA != 0 && evalB != 0) {
						total += (evalA - evalB)/((evalA + evalB)/2);
					}
				}	
				pAccept = Math.exp(-(1-total)/T);
			}
			else if (DominanceRank(stateCandidate, list) > DominanceRank(stateCurrent, list) && DominanceRank(stateCurrent, list)!= 0){
				float value = (float) DominanceRank(stateCandidate, list) / DominanceRank(stateCurrent, list);
				pAccept = Math.exp(-(value+1)/T);
			}
			else{
				//Calculando la probabilidad de aceptación
				List<Double> evaluations = stateCurrent.getEvaluation();
				double total = 0;
				for (int i = 0; i < evaluations.size()-1; i++) {
					Double evalA = evaluations.get(i);
					Double evalB = stateCandidate.getEvaluation().get(i);
					if (evalA != 0 && evalB != 0) {
						total += (evalA - evalB)/((evalA + evalB)/2);
					}
				}
				pAccept = Math.exp(-(1-total)/T);
			}
		}
		//Generar un número aleatorio
		if((RandomUtil.nextFloat()) < pAccept){
			stateCurrent = stateCandidate.clone();
			//Verificando que la solución candidata domina a alguna de las soluciones
			accept = dominance.ListDominance(stateCandidate, list);
		}
		return accept;
	}


	/**
	 * Cuenta cuántas soluciones de la lista son dominadas por el candidato.
	 *
	 * @param stateCandidate estado candidato
	 * @param list lista de soluciones no dominadas
	 * @return número de soluciones dominadas
	 */
	private int DominanceCounter(State stateCandidate, List<State> list) { //chequea el número de soluciones de Pareto que son dominados por la nueva solución
		int counter = 0;
		for (int i = 0; i < list.size(); i++) {
			State solution = list.get(i);
			Dominance dominance = new Dominance();
			if(dominance.dominance(stateCandidate, solution))
				counter++;
		}
		return counter;
	}

	/**
	 * Cuenta cuántas soluciones de la lista dominan al candidato.
	 *
	 * @param stateCandidate estado candidato
	 * @param list lista de soluciones no dominadas
	 * @return el rango de dominancia
	 */
	private int DominanceRank(State stateCandidate, List<State> list) { //calculando el número de soluciones en el conjunto de Pareto que dominan a la solución
		int rank = 0;
		for (int i = 0; i < list.size(); i++) {
			State solution = list.get(i);
			Dominance dominance = new Dominance();
			if(dominance.dominance(solution, stateCandidate)){
				rank++;
			}
		}
		
		return rank;
	}

}
