package factory_method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import evolutionary_algorithms.complement.CrossoverType;
import evolutionary_algorithms.complement.DistributionType;
import evolutionary_algorithms.complement.MutationType;
import evolutionary_algorithms.complement.ProbabilisticSampling;
import evolutionary_algorithms.complement.ReplaceType;
import evolutionary_algorithms.complement.SamplingType;
import evolutionary_algorithms.complement.SelectionType;
import evolutionary_algorithms.complement.Univariate;
import local_search.acceptation_type.AcceptType;
import local_search.candidate_type.CandidateType;
import metaheuristics.generators.GeneratorType;
import metaheuristics.generators.HillClimbing;
import problem.extension.TypeSolutionMethod;
import support.BiciamTestSupport;

@DisplayName("Factorías")
class FactoriesTest {

    @BeforeEach
    void setUp() {
        BiciamTestSupport.setUpOneMax(BiciamTestSupport.BITS);
    }

    @Test
    @DisplayName("FactoryLoader instancia clases de los paquetes permitidos")
    void loaderCreatesAllowedClass() throws Exception {
        Object instance = FactoryLoader.getInstance("evolutionary_algorithms.complement.Univariate");
        assertTrue(instance instanceof Univariate);
    }

    @Test
    @DisplayName("FactoryLoader rechaza clases fuera de la lista blanca")
    void loaderRejectsForeignClass() {
        assertThrows(ClassNotFoundException.class, () -> FactoryLoader.getInstance("java.lang.ProcessBuilder"));
        assertThrows(ClassNotFoundException.class, () -> FactoryLoader.getInstance("metaheuristics.generators.sub.Evil"));
    }

    @Test
    @DisplayName("FactoryLoader valida el nombre de la clase")
    void loaderValidatesName() {
        assertThrows(IllegalArgumentException.class, () -> FactoryLoader.getInstance(null));
        assertThrows(IllegalArgumentException.class, () -> FactoryLoader.getInstance("  "));
        assertThrows(ClassNotFoundException.class, () -> FactoryLoader.getInstance("metaheuristics.generators.NoExiste"));
    }

    @Test
    @DisplayName("FactoryLoader propaga el error de las clases sin constructor público sin argumentos")
    void loaderPropagatesInstantiationErrors() {
        assertThrows(InstantiationException.class,
                () -> FactoryLoader.getInstance("evolutionary_algorithms.complement.Crossover"));
    }

    @Test
    @DisplayName("isAllowed solo admite clases directamente en los paquetes permitidos")
    void isAllowed() {
        assertTrue(FactoryLoader.isAllowed("local_search.acceptation_type.AcceptBest"));
        assertFalse(FactoryLoader.isAllowed("local_search.acceptation_type.x.AcceptBest"));
        assertFalse(FactoryLoader.isAllowed("java.lang.Runtime"));
    }

    @Test
    @DisplayName("Cada factoría crea la clase correspondiente a cada valor de su enumerado")
    void everyFactoryCreatesEveryType() throws Exception {
        FactoryAcceptCandidate accept = new FactoryAcceptCandidate();
        for (AcceptType type : AcceptType.values()) {
            assertEquals(type.name(), accept.createAcceptCandidate(type).getClass().getSimpleName());
        }
        FactoryCandidate candidate = new FactoryCandidate();
        for (CandidateType type : CandidateType.values()) {
            assertEquals(type.name(), candidate.createSearchCandidate(type).getClass().getSimpleName());
        }
        FactoryCrossover crossover = new FactoryCrossover();
        for (CrossoverType type : CrossoverType.values()) {
            assertEquals(type.name(), crossover.createCrossover(type).getClass().getSimpleName());
        }
        FactoryMutation mutation = new FactoryMutation();
        for (MutationType type : MutationType.values()) {
            assertEquals(type.name(), mutation.createMutation(type).getClass().getSimpleName());
        }
        FactoryReplace replace = new FactoryReplace();
        for (ReplaceType type : ReplaceType.values()) {
            assertEquals(type.name(), replace.createReplace(type).getClass().getSimpleName());
        }
        FactoryFatherSelection selection = new FactoryFatherSelection();
        for (SelectionType type : SelectionType.values()) {
            assertEquals(type.name(), selection.createSelectFather(type).getClass().getSimpleName());
        }
        FactorySolutionMethod solution = new FactorySolutionMethod();
        for (TypeSolutionMethod type : TypeSolutionMethod.values()) {
            assertEquals(type.name(), solution.createdSolutionMethod(type).getClass().getSimpleName());
        }
        assertTrue(new FactoryDistribution().createDistribution(DistributionType.Univariate) instanceof Univariate);
        assertTrue(new FactorySampling().createSampling(SamplingType.ProbabilisticSampling) instanceof ProbabilisticSampling);
    }

    @Test
    @DisplayName("FactoryGenerator crea generadores de búsqueda local")
    void generatorFactory() throws Exception {
        assertTrue(new FactoryGenerator().createGenerator(GeneratorType.HillClimbing) instanceof HillClimbing);
        assertEquals(GeneratorType.TabuSearch,
                new FactoryGenerator().createGenerator(GeneratorType.TabuSearch).getType());
    }
}
