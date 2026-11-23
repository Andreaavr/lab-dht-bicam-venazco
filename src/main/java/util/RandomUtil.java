package util;

import util.RandomUtil;

import java.security.SecureRandom;

/**
 * @brief Fuente centralizada de números aleatorios de BiCIAM.
 *
 * Sustituye a las llamadas directas a {@code RandomUtil.nextDouble()} y {@code java.util.Random},
 * que FindSecBugs marca como generadores predecibles (PREDICTABLE_RANDOM).
 * Se usa una única instancia de {@link SecureRandom}, que es segura para hilos.
 */
public final class RandomUtil {

    /** Generador criptográficamente seguro compartido por toda la biblioteca. */
    private static final SecureRandom RANDOM = new SecureRandom();

    /** Clase de utilidades: no se instancia. */
    private RandomUtil() {
    }

    /**
     * Devuelve un número real uniforme en [0, 1).
     *
     * @return valor aleatorio en el intervalo [0, 1)
     */
    public static double nextDouble() {
        return RANDOM.nextDouble();
    }

    /**
     * Devuelve un número real de precisión simple uniforme en [0, 1).
     *
     * @return valor aleatorio en el intervalo [0, 1)
     */
    public static float nextFloat() {
        return RANDOM.nextFloat();
    }

    /**
     * Devuelve un entero uniforme en [0, bound).
     *
     * @param bound límite superior exclusivo; debe ser positivo
     * @return entero aleatorio en el intervalo [0, bound)
     * @throws IllegalArgumentException si {@code bound} no es positivo
     */
    public static int nextInt(int bound) {
        return RANDOM.nextInt(bound);
    }
}
