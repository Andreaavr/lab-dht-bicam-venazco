package metaheuristics.generators;

import java.util.logging.Level;
import java.util.logging.Logger;

import factory_method.FactoryGenerator;


/** Tarea que crea en segundo plano una instancia del algoritmo genético para el portafolio. */
public class InstanceGA implements Runnable {

	/** Registro de errores de la clase. */
	private static final Logger LOGGER = Logger.getLogger(InstanceGA.class.getName());

	/** El indicador de tarea terminada. */
	private volatile boolean terminate = false;
	
	/** Ejecuta la tarea. */
	public void run() {
		FactoryGenerator ifFactoryGenerator = new FactoryGenerator();
		Generator generatorGA = null;
		try {
			generatorGA = ifFactoryGenerator.createGenerator(GeneratorType.GeneticAlgorithm);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error al crear el generador", e);
		}
		boolean find = false;
		int i = 0;
		while (!find) {
			if(MultiGenerator.getListGenerators()[i].getType().equals(GeneratorType.GeneticAlgorithm)){
				MultiGenerator.getListGenerators()[i] = generatorGA;
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
