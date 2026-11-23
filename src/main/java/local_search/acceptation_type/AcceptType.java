package local_search.acceptation_type;

/** Tipos de criterio de aceptación disponibles. */
public enum AcceptType
{
	/** Criterio de aceptación que solo acepta candidatos que no empeoran la solución actual. */
	AcceptBest,
	/** Criterio de aceptación que acepta cualquier candidato. */
	AcceptAnyone,
	/** Criterio de aceptación de Metrópolis (recocido simulado): acepta empeoramientos con probabilidad exp(-delta/T). */
	AcceptNotBadT,
	/** Criterio de aceptación por umbral: acepta empeoramientos menores que un umbral. */
	AcceptNotBadU,
	/** Criterio de aceptación multiobjetivo: acepta candidatos no dominados y actualiza el frente de Pareto. */
	AcceptNotDominated,
	/** Criterio de aceptación de la búsqueda tabú multiobjetivo: acepta siempre y actualiza el frente de Pareto. */
	AcceptNotDominatedTabu,
	/** Criterio de aceptación que acepta candidatos no peores que la solución actual. */
	AcceptNotBad,
	/** Criterio de aceptación multicaso del recocido simulado multiobjetivo (MC-MOSA). */
	AcceptMulticase;
}
