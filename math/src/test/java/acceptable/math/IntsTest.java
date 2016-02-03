package acceptable.math;

import org.junit.Test;

import static acceptable.math.Ints.*;
import static org.junit.Assert.*;

// TODO implement quickcheck clone
public class IntsTest {

    @Test
    public void gcdIsCommutative() {
        assertEquals(gcd(3, 5), gcd(5, 3));
        assertEquals(gcd(3, -5), gcd(-5, 3));
        assertEquals(gcd(-3, -5), gcd(-5, -3));
    }

    @Test
    public void gcdIsAssociative() {
        assertEquals(gcd(3, gcd(5, 15)), gcd(gcd(3, 5), 15));
    }

    @Test
    public void gcdIdentityIsZero() {
        assertEquals(5, gcd(0, 5));
        assertEquals(5, gcd(5, 0));
        assertEquals(0, gcd(0, 0));
    }

    @Test
    public void gcdIsNonnegative() {
        assertEquals(2, gcd(2, -4));
        assertEquals(2, gcd(-2, 4));
        assertEquals(2, gcd(-2, -4));
    }
}
