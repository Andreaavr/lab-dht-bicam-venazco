package evolutionary_algorithms.complement;

/** Probabilidad de que una variable tome un valor determinado. */
public class Probability {
    /** La variable. */
    private Object key;
    /** El valor. */
    private Object value;
	/** La probabilidad. */
	private float probability;
	
	
	/**
	 * Devuelve la probabilidad.
	 *
	 * @return la probabilidad
	 */
	public float getProbability() {
		return probability;
	}
	/**
	 * Establece la probabilidad.
	 *
	 * @param probability la probabilidad
	 */
	public void setProbability(float probability) {
		this.probability = probability;
	}
	/**
	 * Devuelve la variable.
	 *
	 * @return la variable
	 */
	public Object getKey() {
		return key;
	}
	/**
	 * Establece la variable.
	 *
	 * @param key índice de la variable
	 */
	public void setKey(Object key) {
		this.key = key;
	}
	/**
	 * Devuelve el valor.
	 *
	 * @return el valor
	 */
	public Object getValue() {
		return value;
	}
	/**
	 * Establece el valor.
	 *
	 * @param value el valor
	 */
	public void setValue(Object value) {
		this.value = value;
	}
}
