package problem.definition;



import java.util.ArrayList;

import metaheuristics.generators.GeneratorType;

/** Estado (solución) de un problema: código, evaluación y generador que lo produjo. */
public class State implements Cloneable {
	
	/** El tipo de generador. */
	protected GeneratorType typeGenerator;
	/** La evaluación del estado. */
	protected ArrayList<Double> evaluation;
	/** La iteración en la que se generó el estado. */
	protected int number;
	/** El código (valores de las variables) del estado. */
	protected ArrayList<Object> code;
	
	/**
	 * Crea una instancia de State con los valores indicados.
	 *
	 * @param ps estado a copiar
	 */
	public State(State ps) {
		typeGenerator = ps.getTypeGenerator();
		evaluation = ps.getEvaluation();
		number = ps.getNumber();
		code = new ArrayList<Object>(ps.getCode());
	}
	
	/**
	 * Crea una instancia de State con los valores indicados.
	 *
	 * @param code el código (valores de las variables) del estado
	 */
	public State(ArrayList<Object> code) {
		super();
		this.code = code;
	}
	
	/** Crea una instancia de State. */
	public State() {
		code=new ArrayList<Object>();
	}	
	
	/**
	 * Devuelve el código (valores de las variables) del estado.
	 *
	 * @return el código (valores de las variables) del estado
	 */
	public ArrayList<Object> getCode() {
		return code;
	}

	/**
	 * Establece el código (valores de las variables) del estado.
	 *
	 * @param listCode código del estado
	 */
	public void setCode(ArrayList<Object> listCode) {
		this.code = listCode;
	}

	/**
	 * Devuelve el tipo de generador.
	 *
	 * @return el tipo de generador
	 */
	public GeneratorType getTypeGenerator() {
		return typeGenerator;
	}
	/**
	 * Establece el tipo de generador.
	 *
	 * @param typeGenerator el tipo de generador
	 */
	public void setTypeGenerator(GeneratorType typeGenerator) {
		this.typeGenerator = typeGenerator;
	}

	
	/**
	 * Devuelve la evaluación del estado.
	 *
	 * @return la evaluación del estado
	 */
	public ArrayList<Double> getEvaluation() {
		return evaluation;
	}

	/**
	 * Establece la evaluación del estado.
	 *
	 * @param evaluation la evaluación del estado
	 */
	public void setEvaluation(ArrayList<Double> evaluation) {
		this.evaluation = evaluation;
	}

	/**
	 * Devuelve la iteración en la que se generó el estado.
	 *
	 * @return la iteración en la que se generó el estado
	 */
	public int getNumber() {
		return number;
	}
	/**
	 * Establece la iteración en la que se generó el estado.
	 *
	 * @param number la iteración en la que se generó el estado
	 */
	public void setNumber(int number) {
		this.number = number;
	}
	/**
	 * Crea una copia del objeto.
	 *
	 * @return la copia
	 */
	@Override
	public State clone(){
		try {
			State copy = (State) super.clone();
			copy.code = new ArrayList<Object>(this.code);
			if (this.evaluation != null) {
				copy.evaluation = new ArrayList<Double>(this.evaluation);
			}
			return copy;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError("State implementa Cloneable", e);
		}
	}
	
	/**
	 * Crea una copia del estado con su propia lista de código.
	 *
	 * @return la copia del estado
	 */
	public Object getCopy(){
		return new State(new ArrayList<Object>(this.getCode()));
	}
	
	/**
	 * Compara el código de este estado con el de otro.
	 *
	 * @param state estado
	 * @return {@code true} si ambos códigos son iguales
	 */
	public boolean Comparator(State state){

		boolean result=false;
		if(state.getCode().equals(getCode())){
			result=true;
		}
		return result;
	}
	/**
	 * Calcula la distancia de Hamming entre este estado y otro.
	 *
	 * @param state estado
	 * @return número de variables con distinto valor
	 */
	public double Distance(State state){
		double distancia = 0;
		for (int i = 0; i < state.getCode().size(); i++) {
			if (!(state.getCode().get(i).equals(this.getCode().get(i)))) {
				distancia++;
			}
		}
	return distancia;
	}
}
