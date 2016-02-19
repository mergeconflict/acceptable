package acceptable.math;

import org.junit.Test;

import static acceptable.math.Rationals.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// TODO implement quickcheck clone
public class RationalsTest {

    @Test
    public void rationalMustSimplify() {
        long actual = encode(6, 8);
        assertEquals(3, numerator(actual));
        assertEquals(4, denominator(actual));
    }

    @Test
    public void rationalDenominatorMustBeNonnegative() {
        long actual = encode(3, -4);
        assertEquals(-3, numerator(actual));
        assertEquals(4, denominator(actual));
    }

    @Test
    public void rationalPlusMustWork() {
        assertEquals(encode(7, 6), plus(encode(1, 2), encode(2, 3)));
    }

    @Test
    public void rationalMinusMustWork() {
        assertEquals(encode(-1, 6), minus(encode(1, 2), encode(2, 3)));
    }

    @Test
    public void rationalComparatorMustWork() {
        assertTrue(compare(encode(1, 2), encode(2, 3)) < 0);
        assertTrue(compare(encode(1, 2), encode(1, 2)) == 0);
        assertTrue(compare(encode(2, 3), encode(1, 2)) > 0);
    }
}
