package evolutionary_algorithms.complement;

import metaheurictics.strategy.Strategy;

import util.RandomUtil;

import java.util.ArrayList;


import problem.definition.State;


public class OnePointCrossover extends Crossover {

	@Override
	public State crossover(State father1, State father2, double PC) {
				
		State newInd = (State) father1.getCopy();
		
	    ArrayList<Object> ind1 = new ArrayList<Object>();
	    ArrayList<Object> ind2 = new ArrayList<Object>();
	    
	    double number = (double) RandomUtil.nextDouble() * (double)(1);	
		if(number <= PC){
			//llenar los valores de cada hijo
			int pos = (int) (RandomUtil.nextDouble() * (Strategy.getStrategy().getProblem().getCodification().getVariableCount() - 1));
			for (int i = 0; i < father1.getCode().size(); i++) {
				if(i <= pos){
					ind1.add(father1.getCode().get(i));
					ind2.add(father2.getCode().get(i));
				}
				else{
					ind1.add(father2.getCode().get(i));
					ind2.add(father1.getCode().get(i));
				}
			}
			
			//generar un numero aleatorio 0 o 1, si es 0 me quedo con ind1 si es 1 con ind2.
			int random = (int)(RandomUtil.nextDouble() * (double)(2));
			if(random == 0)
				newInd.setCode(ind1);
			else newInd.setCode(ind2);
		}
		return newInd;			
	}
	
	
}
