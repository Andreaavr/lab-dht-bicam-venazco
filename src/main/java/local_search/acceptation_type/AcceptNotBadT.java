package local_search.acceptation_type;

import util.RandomUtil;

import java.lang.reflect.InvocationTargetException;

import metaheurictics.strategy.Strategy;
import metaheuristics.generators.SimulatedAnnealing;
import problem.definition.Problem;
import problem.definition.State;
import problem.definition.Problem.ProblemType;

/**
 * Criterio de aceptación de Metrópolis (recocido simulado): acepta empeoramientos con probabilidad exp(-delta/T).
 */
public class AcceptNotBadT extends AcceptableCandidate{

	/**
	 * Decide si el candidato sustituye a la solución actual.
	 *
	 * @param stateCurrent estado actual
	 * @param stateCandidate estado candidato
	 * @return {@code true} si el candidato es aceptado
	 * @throws IllegalArgumentException si algún argumento no es válido
	 * @throws SecurityException si el gestor de seguridad impide la reflexión
	 * @throws ClassNotFoundException si la clase pedida a la factoría no existe o no está permitida
	 * @throws InstantiationException si la clase pedida a la factoría no se puede instanciar
	 * @throws IllegalAccessException si el constructor de la clase no es accesible
	 * @throws InvocationTargetException si el constructor de la clase lanza una excepción
	 * @throws NoSuchMethodException si la clase no tiene constructor sin argumentos
	 */
	@Override
	public Boolean acceptCandidate(State stateCurrent, State stateCandidate) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Boolean accept = false;
		Problem problem = Strategy.getStrategy().getProblem();
		if (problem.getTypeProblem().equals(ProblemType.Maximizar)) {
			double result = (stateCandidate.getEvaluation().get(0) - stateCurrent.getEvaluation().get(0)) / SimulatedAnnealing.tinitial;
			double probaleatory = RandomUtil.nextDouble();
			double exp = Math.exp(result);
			if ((stateCandidate.getEvaluation().get(0) >= stateCurrent.getEvaluation().get(0))
					|| (probaleatory < exp))
 				accept = true;
			else
				accept = false;
		} else {
			double result_min = (stateCurrent.getEvaluation().get(0) - stateCandidate.getEvaluation().get(0)) / SimulatedAnnealing.tinitial;
			if ((stateCandidate.getEvaluation().get(0) <= stateCurrent.getEvaluation().get(0))
					|| (RandomUtil.nextDouble() < Math.exp(result_min)))
				accept = true;
			else
				accept = false;
		}
		return accept;
	}
}
