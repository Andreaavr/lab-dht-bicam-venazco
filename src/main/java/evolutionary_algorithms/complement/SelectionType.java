package evolutionary_algorithms.complement;

/** Tipos de selección de padres disponibles. */
public enum SelectionType {
	/** Selección por ruleta: cada individuo se elige con probabilidad proporcional a su evaluación. */
	RouletteSelection,
	/** Selección por truncamiento: elige los k mejores individuos de la población. */
	TruncationSelection;
}
