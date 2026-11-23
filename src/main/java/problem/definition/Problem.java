package problem.definition;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import problem.extension.SolutionMethod;
import problem.extension.TypeSolutionMethod;


import factory_interface.IFFactorySolutionMethod;
import factory_method.FactorySolutionMethod;


/** Definición de un problema de optimización: funciones objetivo, codificación y operador. */
public class Problem {

	/** Sentido de optimización del problema: maximizar o minimizar. */
	public enum ProblemType {
		/** Maximizar la función objetivo. */
		Maximizar,
		/** Minimizar la función objetivo. */
		Minimizar;
	}

	/** Las funciones objetivo. */
	private ArrayList<ObjetiveFunction> function;
	/** El estado. */
	private State state;
	/** El sentido de optimización. */
	private ProblemType typeProblem;
	/** La codificación del problema. */
	private Codification codification;
	/** El operador de vecindad. */
	private Operator operator;
	/** El número de valores posibles de cada variable. */
	private int possibleValue;
	/** El método de solución multiobjetivo. */
	private TypeSolutionMethod typeSolutionMethod;
	/** La factoría de métodos de solución. */
	private IFFactorySolutionMethod factorySolutionMethod;
	
	/** Crea una instancia de Problem. */
	public Problem() {
		super();
	}

	/**
	 * Devuelve las funciones objetivo.
	 *
	 * @return las funciones objetivo
	 */
	public ArrayList<ObjetiveFunction> getFunction() {
		return function;
	}

	/**
	 * Establece las funciones objetivo.
	 *
	 * @param function las funciones objetivo
	 */
	public void setFunction(ArrayList<ObjetiveFunction> function) {
		this.function = function;
	}

	/**
	 * Devuelve el estado.
	 *
	 * @return el estado
	 */
	public State getState() {
		return state;
	}

	/**
	 * Establece el estado.
	 *
	 * @param state estado
	 */
	public void setState(State state) {
		this.state = state;
	}

	/**
	 * Devuelve el sentido de optimización.
	 *
	 * @return el sentido de optimización
	 */
	public ProblemType getTypeProblem() {
		return typeProblem;
	}
	/**
	 * Establece el sentido de optimización.
	 *
	 * @param typeProblem el sentido de optimización
	 */
	public void setTypeProblem(ProblemType typeProblem) {
		this.typeProblem = typeProblem;
	}

	/**
	 * Devuelve la codificación del problema.
	 *
	 * @return la codificación del problema
	 */
	public Codification getCodification() {
		return codification;
	}
	/**
	 * Establece la codificación del problema.
	 *
	 * @param codification la codificación del problema
	 */
	public void setCodification(Codification codification) {
		this.codification = codification;
	}

	/**
	 * Devuelve el operador de vecindad.
	 *
	 * @return el operador de vecindad
	 */
	public Operator getOperator() {
		return operator;
	}

	/**
	 * Establece el operador de vecindad.
	 *
	 * @param operator el operador de vecindad
	 */
	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	/**
	 * Devuelve el número de valores posibles de cada variable.
	 *
	 * @return el número de valores posibles de cada variable
	 */
	public int getPossibleValue() {
		return possibleValue;
	}

	/**
	 * Establece el número de valores posibles de cada variable.
	 *
	 * @param possibleValue el número de valores posibles de cada variable
	 */
	public void setPossibleValue(int possibleValue) {
		this.possibleValue = possibleValue;
	}

	/**
	 * Evalúa un estado con las funciones objetivo del problema y le asigna la evaluación.
	 *
	 * @param state estado
	 * @throws IllegalArgumentException si algún argumento no es válido
	 * @throws SecurityException si el gestor de seguridad impide la reflexión
	 * @throws ClassNotFoundException si la clase pedida a la factoría no existe o no está permitida
	 * @throws InstantiationException si la clase pedida a la factoría no se puede instanciar
	 * @throws IllegalAccessException si el constructor de la clase no es accesible
	 * @throws InvocationTargetException si el constructor de la clase lanza una excepción
	 * @throws NoSuchMethodException si la clase no tiene constructor sin argumentos
	 */
	public void Evaluate(State state) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		double eval = 0;       
		ArrayList<Double> evaluation = new ArrayList<Double>(this.function.size());
		if (typeSolutionMethod == null) {
			eval= function.get(0).Evaluation(state);
			evaluation.add(evaluation.size(), eval);
			state.setEvaluation(evaluation);
		}
		else {
			SolutionMethod method = newSolutionMethod(typeSolutionMethod);
			method.evaluationState(state);
		}
	}
	
	/**
	 * Devuelve el método de solución multiobjetivo.
	 *
	 * @return el método de solución multiobjetivo
	 */
	public TypeSolutionMethod getTypeSolutionMethod() {
		return typeSolutionMethod;
	}
	/**
	 * Establece el método de solución multiobjetivo.
	 *
	 * @param typeSolutionMethod el método de solución multiobjetivo
	 */
	public void setTypeSolutionMethod(TypeSolutionMethod typeSolutionMethod) {
		this.typeSolutionMethod = typeSolutionMethod;
	}
	/**
	 * Devuelve la factoría de métodos de solución.
	 *
	 * @return la factoría de métodos de solución
	 */
	public IFFactorySolutionMethod getFactorySolutionMethod() {
		return factorySolutionMethod;
	}
	/**
	 * Establece la factoría de métodos de solución.
	 *
	 * @param factorySolutionMethod la factoría de métodos de solución
	 */
	public void setFactorySolutionMethod(
			IFFactorySolutionMethod factorySolutionMethod) {
		this.factorySolutionMethod = factorySolutionMethod;
	}
	
	/**
	 * Crea el método de solución multiobjetivo indicado.
	 *
	 * @param typeSolutionMethod el método de solución multiobjetivo
	 * @return el método creado
	 * @throws IllegalArgumentException si algún argumento no es válido
	 * @throws SecurityException si el gestor de seguridad impide la reflexión
	 * @throws ClassNotFoundException si la clase pedida a la factoría no existe o no está permitida
	 * @throws InstantiationException si la clase pedida a la factoría no se puede instanciar
	 * @throws IllegalAccessException si el constructor de la clase no es accesible
	 * @throws InvocationTargetException si el constructor de la clase lanza una excepción
	 * @throws NoSuchMethodException si la clase no tiene constructor sin argumentos
	 */
	public SolutionMethod newSolutionMethod(TypeSolutionMethod typeSolutionMethod) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		factorySolutionMethod = new FactorySolutionMethod();
		SolutionMethod solutionMethod = factorySolutionMethod.createdSolutionMethod(typeSolutionMethod);
		return solutionMethod;
	}
}

	
	

