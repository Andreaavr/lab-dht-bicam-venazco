package problem.definition;

import problem.definition.Problem.ProblemType;

/** Función objetivo abstracta de un problema. */
public abstract class ObjetiveFunction {
	
	/** El sentido de optimización. */
	private ProblemType typeProblem;
	/** El peso. */
	private float weight;
	
	/**
	 * Devuelve el peso.
	 *
	 * @return el peso
	 */
	public float getWeight() {
		return weight;
	}

	/**
	 * Establece el peso.
	 *
	 * @param weight el peso
	 */
	public void setWeight(float weight) {
		this.weight = weight;
	}

	/**
	 * Devuelve el sentido de optimización.
	 *
	 * @return el sentido de optimización
	 */
	public ProblemType getTypeProblem() {
		return typeProblem;
	}

	/**
	 * Establece el sentido de optimización.
	 *
	 * @param typeProblem el sentido de optimización
	 */
	public void setTypeProblem(ProblemType typeProblem) {
		this.typeProblem = typeProblem;
	}

	/**
	 * Evalúa un estado.
	 *
	 * @param state estado
	 * @return el valor de la función objetivo
	 */
	public abstract Double Evaluation(State state);
}
