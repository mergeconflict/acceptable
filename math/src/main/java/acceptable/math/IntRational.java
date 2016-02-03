package acceptable.math;

public class IntRational implements Comparable<IntRational> {
    public final int numerator;
    public final int denominator;

    public static final IntRational
            ZERO = new IntRational(0, 1),
            ONE = new IntRational(1, 1),
            NEGATIVE_ONE = new IntRational(-1, 1);

    private IntRational(int numerator, int denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public static IntRational mk(int numerator, int denominator) {
        if (denominator < 0) {
            numerator = -numerator;
            denominator = -denominator;
        }
        int gcd = Ints.gcd(numerator, denominator);
        return new IntRational(numerator / gcd, denominator / gcd);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntRational that = (IntRational) o;

        if (numerator != that.numerator) return false;
        return denominator == that.denominator;

    }

    @Override
    public int hashCode() {
        int result = numerator;
        result = 31 * result + denominator;
        return result;
    }

    @Override
    public String toString() {
        return numerator + "/" + denominator;
    }

    @Override
    public int compareTo(IntRational that) {
        return minus(that).signum();
    }

    public int signum() {
        return Integer.signum(numerator);
    }

    public IntRational negate() {
        return new IntRational(-numerator, denominator);
    }

    public IntRational plus(IntRational that) {
        int denominator =
                Ints.lcm(this.denominator, that.denominator);
        int numerator =
                this.numerator * (denominator / this.denominator) +
                that.numerator * (denominator / that.denominator);
        return mk(numerator, denominator);
    }

    public IntRational minus(IntRational that) {
        return plus(that.negate());
    }
}
