package acceptable.math;

import java.util.Comparator;

public final class Rationals {

    private Rationals() {}

    public static final long
            ZERO = unsafeEncode(0, 1),
            ONE = unsafeEncode(1, 1),
            NEGATIVE_ONE = unsafeEncode(-1, 1);

    /**
     * Encode a rational number consisting of an int numerator and denominator
     * as a long. Caller must ensure the denominator is nonnegative.
     * @param numerator int numerator, may be negative.
     * @param denominator int denominator, must be nonnegative.
     * @return an encoding of the given rational number as a long.
     */
    private static long unsafeEncode(int numerator, int denominator) {
        return (long) numerator << 32 | denominator;
    }

    /**
     * Construct a rational number, encoded as a long, from an int numerator
     * and denominator. The number will be simplified such that the encoded
     * denominator is nonnegative, and the gcd of the encoded numerator and
     * denominator is 1.
     * @return a simplified encoding of the given rational number as a long.
     */
    public static long encode(int numerator, int denominator) {
        if (denominator < 0) {
            numerator = -numerator;
            denominator = -denominator;
        }
        int gcd = Ints.gcd(numerator, denominator);
        return unsafeEncode(numerator / gcd, denominator / gcd);
    }

    /**
     * Decode the numerator of a rational number encoded as a long.
     * @param rational a rational number with int numerator and denominator
     *                 encoded as a long.
     * @return the int numerator of the rational number, may be negative.
     */
    public static int numerator(long rational) {
        return (int) (rational >> 32);
    }

    /**
     * Decode the denominator of a rational number encoded as a long.
     * @param rational a rational number with int numerator and denominator
     *                 encoded as a long.
     * @return the int denominator of the rational number, must be nonnegative.
     */
    public static int denominator(long rational) {
        return (int) rational;
    }

    /**
     * Construct a string representation of a rational number encoded as a long.
     * @param rational a rational number with int numerator and denominator
     *                 encoded as a long.
     * @return a string representation of the rational number.
     */
    public static String toString(long rational) {
        return numerator(rational) + "/" + denominator(rational);
    }

    /**
     * Negates a rational number encoded as a long.
     * @param rational a rational number with int numerator and denominator
     *                 encoded as a long.
     * @return -rational
     */
    public static long negate(long rational) {
        return unsafeEncode(-numerator(rational), denominator(rational));
    }

    /**
     * Add two rational numbers encoded as longs.
     * @param lhs left-hand side
     * @param rhs right-hand side
     * @return lhs + rhs
     */
    public static long plus(long lhs, long rhs) {
        int
                lhsDenominator = denominator(lhs),
                rhsDenominator = denominator(rhs),
                denominator = Ints.lcm(lhsDenominator, rhsDenominator),
                numerator =
                        numerator(lhs) * (denominator / lhsDenominator) +
                        numerator(rhs) * (denominator / rhsDenominator);
        return encode(numerator, denominator);
    }

    /**
     * Subtract two rational numbers encoded as longs.
     * @param lhs left-hand side
     * @param rhs right-hand side
     * @return lhs - rhs
     */
    public static long minus(long lhs, long rhs) {
        return plus(lhs, negate(rhs));
    }

    /**
     * Compare two rational numbers encoded as longs.
     * @param lhs left-hand side
     * @param rhs right-hand side
     * @return a negative number if lhs < rhs, 0 if lhs == rhs, or a positive
     * number if lhs > rhs.
     */
    public static int compare(long lhs, long rhs) {
        return numerator(minus(lhs, rhs));
    }
}
