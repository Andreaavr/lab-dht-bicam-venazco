package problem.definition;

import java.util.List;

/** Operador abstracto que genera vecinos y estados aleatorios de un problema. */
public abstract class Operator {
	
		/**
		 * Genera vecinos de un estado.
		 *
		 * @param stateCurrent estado actual
		 * @param operatornumber número de vecinos o estados a generar
		 * @return la lista de vecinos
		 */
		public abstract List<State> generatedNewState(State stateCurrent, Integer operatornumber);
		/**
		 * Genera estados aleatorios del problema.
		 *
		 * @param operatornumber número de vecinos o estados a generar
		 * @return la lista de estados generados
		 */
		public abstract List<State> generateRandomState (Integer operatornumber);

	}

