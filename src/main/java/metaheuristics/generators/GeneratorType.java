package metaheuristics.generators;

/** Tipos de generador (metaheurística) disponibles. */
public enum GeneratorType {
	/** Ascensión de colinas: se mueve al mejor vecino si no empeora la solución actual. */
	HillClimbing,
	/** Búsqueda tabú: evita volver a estados visitados recientemente. */
	TabuSearch,
	/** Recocido simulado con enfriamiento geométrico. */
	SimulatedAnnealing,
	/** Búsqueda aleatoria: genera estados aleatorios y conserva el mejor. */
	RandomSearch,
	/** Búsqueda por umbral límite: acepta vecinos que empeoran menos que un umbral. */
	LimitThreshold,
	/** Ascensión de colinas con reinicio aleatorio periódico. */
	HillClimbingRestart,
	/** Algoritmo genético: selección, cruce, mutación y reemplazo. */
	GeneticAlgorithm,
	/** Estrategias evolutivas: selección, mutación y reemplazo sin cruce. */
	EvolutionStrategies,
	/** Algoritmo de estimación de distribuciones (EDA). */
	DistributionEstimationAlgorithm,
	/** Optimización por enjambre de partículas (PSO). */
	ParticleSwarmOptimization,
	/** Portafolio de generadores: elige en cada iteración el generador a aplicar mediante una ruleta de pesos. */
	MultiGenerator,
	/** Búsqueda tabú multiobjetivo. */
	MultiobjectiveTabuSearch,
	/** Ascensión de colinas estocástica multiobjetivo. */
	MultiobjectiveStochasticHillClimbing,
	/** Recocido simulado multiobjetivo multicaso (MC-MOSA). */
	MultiCaseSimulatedAnnealing,
	/** Ascensión de colinas multiobjetivo con reinicio. */
	MultiobjectiveHillClimbingRestart,
	/** MultiobjectiveHillClimbingDistance; */
	MultiobjectiveHillClimbingDistance;;
}
