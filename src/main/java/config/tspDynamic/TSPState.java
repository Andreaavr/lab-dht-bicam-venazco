package config.tspDynamic;

/** Ciudad de una ruta en el problema del viajante (TSP) dinámico: identificador y valor de ordenación. */
public class TSPState {
	
	/** El valor. */
	private int value;
	/** El identificador de la ciudad. */
	private int idCity;
	
	/**
	 * Devuelve el valor.
	 *
	 * @return el valor
	 */
	public int getValue() {
		return value;
	}
	/**
	 * Establece el valor.
	 *
	 * @param value el valor
	 */
	public void setValue(int value) {
		this.value = value;
	}
	
	/**
	 * Devuelve el identificador de la ciudad.
	 *
	 * @return el identificador de la ciudad
	 */
	public int getIdCity() {
		return idCity;
	}
	/**
	 * Establece el identificador de la ciudad.
	 *
	 * @param idCity el identificador de la ciudad
	 */
	public void setIdCity(int idCity) {
		this.idCity = idCity;
	}

}
