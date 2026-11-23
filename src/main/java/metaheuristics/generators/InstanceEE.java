package metaheuristics.generators;

import java.util.logging.Level;

import java.util.logging.Logger;

import factory_method.FactoryGenerator;

public class InstanceEE implements Runnable {

	/** Registro de errores de la clase. */
	private static final Logger LOGGER = Logger.getLogger(InstanceEE.class.getName());

	private boolean terminate = false;
	
	public void run() {
		FactoryGenerator ifFactoryGenerator = new FactoryGenerator();
		Generator generatorEE = null;
		try {
			generatorEE = ifFactoryGenerator.createGenerator(GeneratorType.EvolutionStrategies);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.log(Level.SEVERE, "Error al crear el generador", e);
		}
		boolean find = false;
		int i = 0;
		while (!find) {
			if(MultiGenerator.getListGenerators()[i].getType().equals(GeneratorType.EvolutionStrategies)){
				MultiGenerator.getListGenerators()[i] = generatorEE;
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
