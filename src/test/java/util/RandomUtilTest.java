package util;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("RandomUtil")
class RandomUtilTest {

    @Test
    @DisplayName("Los valores generados están dentro de su rango")
    void ranges() {
        for (int i = 0; i < 1000; i++) {
            double d = RandomUtil.nextDouble();
            float f = RandomUtil.nextFloat();
            int n = RandomUtil.nextInt(3);
            assertTrue(d >= 0 && d < 1);
            assertTrue(f >= 0 && f < 1);
            assertTrue(n >= 0 && n < 3);
        }
    }

    @Test
    @DisplayName("nextInt rechaza límites no positivos")
    void invalidBound() {
        assertThrows(IllegalArgumentException.class, () -> RandomUtil.nextInt(0));
    }
}
