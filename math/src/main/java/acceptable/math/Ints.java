package acceptable.math;

import static java.lang.Math.abs;

public final class Ints {
    private Ints() {}

    public static int gcd(int a, int b) {
        while (b != 0) {
            int tmp = b;
            b = a % b;
            a = tmp;
        }
        return abs(a);
    }

    public static int lcm(int a, int b) {
        return abs(a * (b / gcd(a, b)));
    }
}
