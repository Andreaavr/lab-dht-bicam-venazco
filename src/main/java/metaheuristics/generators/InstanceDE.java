package metaheuristics.generators;

import java.util.logging.Level;
import java.util.logging.Logger;

import factory_method.FactoryGenerator;


/** Tarea que crea en segundo plano una instancia del EDA para el portafolio de generadores. */
public class InstanceDE implements Runnable {

	/** Registro de errores de la clase. */
	private static final Logger LOGGER = Logger.getLogger(InstanceDE.class.getName());

	/** El indicador de tarea terminada. */
	private volatile boolean terminate = false;
	
	/** Ejecuta la tarea. */
	public void run() {
		FactoryGenerator ifFactoryGenerator = new FactoryGenerator();
		Generator generatorDE = null;
		try {
			generatorDE = ifFactoryGenerator.createGenerator(GeneratorType.DistributionEstimationAlgorithm);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error al crear el generador", e);
		}
		boolean find = false;
		int i = 0;
		while (!find) {
			if(MultiGenerator.getListGenerators()[i].getType().equals(GeneratorType.DistributionEstimationAlgorithm)){
				MultiGenerator.getListGenerators()[i] = generatorDE;
				find = true;
			}
			else i++;	
		}
		terminate = true;
	}

	/**
	 * Indica si la tarea ha terminado.
	 *
	 * @return {@code true} si ha terminado
	 */
	public boolean isTerminate() {
		return terminate;
	}

	/**
	 * Establece el indicador de tarea terminada.
	 *
	 * @param terminate el indicador de tarea terminada
	 */
	public void setTerminate(boolean terminate) {
		this.terminate = terminate;
	}
}
