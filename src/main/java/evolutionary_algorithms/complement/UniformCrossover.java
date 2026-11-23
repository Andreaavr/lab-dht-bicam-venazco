package evolutionary_algorithms.complement;

import util.RandomUtil;


import problem.definition.State;

/** Cruce uniforme: cada gen del hijo procede de uno de los dos padres según una máscara aleatoria. */
public class UniformCrossover extends Crossover {
	
	
	/**
	 * Genera una máscara binaria aleatoria.
	 *
	 * @param length longitud de la máscara
	 * @return la máscara de ceros y unos
	 */
	public int[] mascara(int length){
		int[] mascara = new int[length];
		for (int i = 0; i < mascara.length; i++) {
			int value = RandomUtil.nextInt(2);
			mascara[i] = value;
		}
		return mascara;
	}	
    
	/**
	 * Cruza dos padres y obtiene un hijo.
	 *
	 * @param father1 primer padre
	 * @param father2 segundo padre
	 * @param PC probabilidad de cruce
	 * @return el hijo generado
	 */
	@Override
	public State crossover(State father1, State father2, double PC) {
		Object value;
		State state = (State) father1.getCopy();
		int[] mascara = mascara(father1.getCode().size());
   		for (int k = 0; k < mascara.length; k++) {
   			if(mascara[k] == 1){
   				value = father1.getCode().get(k);
   				state.getCode().set(k, value);
   			}
   			else{
   				if(mascara[k] == 0){
   					value = father2.getCode().get(k);  
   					state.getCode().set(k, value);
   				}
   			}
		}
		return state;
	}
}
