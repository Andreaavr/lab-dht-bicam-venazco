package metaheuristics.generators;

import java.util.logging.Level;
import java.util.logging.Logger;

import factory_method.FactoryGenerator;


public class InstanceGA implements Runnable {

	/** Registro de errores de la clase. */
	private static final Logger LOGGER = Logger.getLogger(InstanceGA.class.getName());

	private boolean terminate = false;
	
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
		while (find == false) {
			if(MultiGenerator.getListGenerators()[i].getType().equals(GeneratorType.GeneticAlgorithm)){
				MultiGenerator.getListGenerators()[i] = generatorGA;
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
