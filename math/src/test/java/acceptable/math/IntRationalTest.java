package acceptable.math;

import org.junit.Test;

import static acceptable.math.IntRational.*;
import static org.junit.Assert.*;

// TODO implement quickcheck clone
public class IntRationalTest {

    @Test
    public void rationalMustSimplify() {
        IntRational actual = mk(6, 8);
        assertEquals(3, actual.numerator);
        assertEquals(4, actual.denominator);
    }

    @Test
    public void rationalDenominatorMustBeNonnegative() {
        IntRational actual = mk(3, -4);
        assertEquals(-3, actual.numerator);
        assertEquals(4, actual.denominator);
    }

    @Test
    public void rationalSignumMustEqualNumeratorSignum() {
        assertEquals(-1, mk(-1, 1).signum());
        assertEquals(0, mk(0, 1).signum());
        assertEquals(1, mk(1, 1).signum());
    }

    @Test
    public void rationalPlusMustWork() {
        assertEquals(mk(7, 6), mk(1, 2).plus(mk(2, 3)));
    }

    @Test
    public void rationalMinusMustWork() {
        assertEquals(mk(-1, 6), mk(1, 2).minus(mk(2, 3)));
    }

    @Test
    public void rationalCompareToMustWork() {
        assertEquals(-1, mk(1, 2).compareTo(mk(2, 3)));
        assertEquals(0, mk(1, 2).compareTo(mk(1, 2)));
        assertEquals(1, mk(2, 3).compareTo(mk(1, 2)));
    }
}
