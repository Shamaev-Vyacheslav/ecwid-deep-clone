package com.test.clone;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DeepCloneComplexConstructorTest {

    @Test
    public void instantiateWithBusinessLogic() {
        Mountain mountain = new Mountain(1000);
        Mountain mountainClone = DeepClone.of(mountain);
        assertFalse(mountain == mountainClone);
        assertEquals(mountain.altitude, mountainClone.altitude);

        Ravine ravine = new Ravine(-999);
        Ravine ravineClone = DeepClone.of(ravine);
        assertFalse(ravine == ravineClone);
        assertEquals(ravine.altitude, ravineClone.altitude);
    }

    private static class Mountain {
        private Integer altitude;

        private Mountain(Integer altitude) {
            if (altitude <= 0) {
                throw new IllegalArgumentException();
            }

            this.altitude = altitude;
        }
    }

    private static class Ravine {
        private Integer altitude;

        private Ravine(Integer altitude) {
            if (altitude >= 0) {
                throw new IllegalArgumentException();
            }
            this.altitude = altitude;
        }
    }
}
