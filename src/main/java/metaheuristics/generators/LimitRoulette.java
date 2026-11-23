package metaheuristics.generators;

/** Intervalo de la ruleta asociado a un generador en el portafolio. */
public class LimitRoulette {

	/** El límite inferior. */
	private float limitLow;
	/** El límite superior. */
	private float limitHigh;
	/** El generador. */
	private Generator generator;
	
	/**
	 * Devuelve el generador.
	 *
	 * @return el generador
	 */
	public Generator getGenerator() {
		return generator;
	}
	/**
	 * Establece el generador.
	 *
	 * @param generator el generador
	 */
	public void setGenerator(Generator generator) {
		this.generator = generator;
	}
	/**
	 * Devuelve el límite superior.
	 *
	 * @return el límite superior
	 */
	public float getLimitHigh() {
		return limitHigh;
	}
	/**
	 * Establece el límite superior.
	 *
	 * @param limitHigh el límite superior
	 */
	public void setLimitHigh(float limitHigh) {
		this.limitHigh = limitHigh;
	}
	/**
	 * Devuelve el límite inferior.
	 *
	 * @return el límite inferior
	 */
	public float getLimitLow() {
		return limitLow;
	}
	/**
	 * Establece el límite inferior.
	 *
	 * @param limitLow el límite inferior
	 */
	public void setLimitLow(float limitLow) {
		this.limitLow = limitLow;
	}
	
	
}
