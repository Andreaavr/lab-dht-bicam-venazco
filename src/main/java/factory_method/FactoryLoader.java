package factory_method;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @brief Cargador reflexivo usado por todas las factorías de BiCIAM.
 *
 * Crea instancias a partir del nombre completo de una clase. Por seguridad solo se
 * permiten clases de los paquetes propios de BiCIAM (lista blanca), de modo que un
 * nombre de clase manipulado no pueda instanciar clases arbitrarias del classpath.
 * Los errores ya no se imprimen con {@code printStackTrace()} (exposición de
 * información) ni se ocultan: se propagan al llamador.
 */
public final class FactoryLoader {

    /** Prefijos de paquete desde los que se permite instanciar clases. */
    private static final List<String> ALLOWED_PACKAGES = List.of(
            "metaheuristics.generators.",
            "local_search.acceptation_type.",
            "local_search.candidate_type.",
            "evolutionary_algorithms.complement.",
            "problem.extension.");

    /** Clase de utilidades: no se instancia. */
    private FactoryLoader() {
    }

    /**
     * Crea una instancia de la clase indicada usando su constructor sin argumentos.
     *
     * @param className nombre completo de la clase (debe pertenecer a un paquete permitido)
     * @return nueva instancia de la clase
     * @throws ClassNotFoundException si la clase no existe o no está en la lista blanca
     * @throws IllegalArgumentException si {@code className} es nulo o vacío
     * @throws SecurityException si el gestor de seguridad impide el acceso
     * @throws InstantiationException si la clase es abstracta o una interfaz
     * @throws IllegalAccessException si el constructor no es accesible
     * @throws InvocationTargetException si el constructor lanza una excepción
     * @throws NoSuchMethodException si la clase no tiene constructor sin argumentos
     */
    public static Object getInstance(String className) throws ClassNotFoundException,
            IllegalArgumentException, SecurityException, InstantiationException,
            IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (className == null || className.isBlank()) {
            throw new IllegalArgumentException("El nombre de la clase no puede estar vacío");
        }
        if (!isAllowed(className)) {
            throw new ClassNotFoundException("Clase no permitida por la factoría: " + className);
        }
        Class<?> c = Class.forName(className);
        return c.getDeclaredConstructor().newInstance();
    }

    /**
     * Indica si una clase pertenece a alguno de los paquetes permitidos.
     *
     * @param className nombre completo de la clase
     * @return {@code true} si la clase puede ser instanciada por la factoría
     */
    static boolean isAllowed(String className) {
        for (String prefix : ALLOWED_PACKAGES) {
            if (className.startsWith(prefix) && className.indexOf('.', prefix.length()) < 0) {
                return true;
            }
        }
        return false;
    }
}
