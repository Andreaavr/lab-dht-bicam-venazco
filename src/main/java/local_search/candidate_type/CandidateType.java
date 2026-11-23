package local_search.candidate_type;

/** Tipos de estrategia para elegir un candidato de la vecindad. */
public enum CandidateType{
	/** Elige el vecino de menor evaluación. */
	SmallerCandidate,
	/** Elige el vecino de mayor evaluación. */
	GreaterCandidate,
	/** Elige un vecino al azar. */
	RandomCandidate,
	/** Elige un vecino no dominado recorriendo la vecindad. */
	NotDominatedCandidate;
}
