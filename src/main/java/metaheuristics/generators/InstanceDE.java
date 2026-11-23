package metaheuristics.generators;

import java.util.logging.Level;
import java.util.logging.Logger;

import factory_method.FactoryGenerator;


public class InstanceDE implements Runnable {

	/** Registro de errores de la clase. */
	private static final Logger LOGGER = Logger.getLogger(InstanceDE.class.getName());

	private boolean terminate = false;
	
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

	public boolean isTerminate() {
		return terminate;
	}

	public void setTerminate(boolean terminate) {
		this.terminate = terminate;
	}
}
