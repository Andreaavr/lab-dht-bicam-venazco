package evolutionary_algorithms.complement;

/** Intervalo [min, max] asociado a una probabilidad, usado en el muestreo por ruleta. */
public class Range {
   /** La probabilidad asociada al intervalo. */
   private Probability data;
   /** El máximo. */
   private float max;
   /** El mínimo. */
   private float min;
   
   /**
    * Devuelve la probabilidad asociada al intervalo.
    *
    * @return la probabilidad asociada al intervalo
    */
   public Probability getData() {
	  return data;
   }
   /**
    * Establece la probabilidad asociada al intervalo.
    *
    * @param data la probabilidad asociada al intervalo
    */
   public void setData(Probability data) {
	  this.data = data;
   }
   /**
    * Devuelve el máximo.
    *
    * @return el máximo
    */
   public float getMax() {
	  return max;
   }
   /**
    * Establece el máximo.
    *
    * @param max el máximo
    */
   public void setMax(float max) {
	  this.max = max;
   }
   /**
    * Devuelve el mínimo.
    *
    * @return el mínimo
    */
   public float getMin() {
	  return min;
   }
   /**
    * Establece el mínimo.
    *
    * @param min el mínimo
    */
   public void setMin(float min) {
	  this.min = min;
   }
}
