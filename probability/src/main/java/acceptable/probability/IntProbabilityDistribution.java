package acceptable.probability;

import acceptable.data.Stack;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static acceptable.math.Rationals.*;

/**
 * Discrete probability distribution with integer weights. Given an indexed
 * list of weights <em>w<sub>0</sub></em>, <em>w<sub>1</sub></em>, ...
 * <em>w<sub>size - 1</sub></em> totaling <em>Σw</em>, and an
 * {@code IntProbabilityDistribution d} constructed with those weights, then
 * calls to {@code d.}{@link #next(Random)} will return index <em>i</em> with
 * probability <em>w<sub>i</sub> / Σw</em>. For example:
 * <pre>{@code
 * IntProbabilityDistribution d = new IntProbabilityDistribution(2, 3, 5);
 * int sample = d.next();}</pre>
 * Here, {@code sample} will be 0 with 20% probability, 1 with 30% probability,
 * and 2 with 50% probability.
 */
public final class IntProbabilityDistribution {

    /*
     * "Alias method" implementation based on Vose, 1991: "A linear algorithm
     * for generating random numbers with a given distribution." Rational
     * numbers are used as probabilities here instead of floating point to avoid
     * rounding errors.
     */

    private final int size;
    private final int[] aliases;
    private final long[] probabilities;

    /**
     * Construct a discrete probability distribution with integer weights
     * <em>w<sub>0</sub></em>, <em>w<sub>1</sub></em>, ...
     * <em>w<sub>size - 1</sub></em> totaling <em>Σw</em>.
     * @param weights indexed list of integer weights.
     */
    @SuppressWarnings("unchecked")
    public IntProbabilityDistribution(int... weights) {
        size = weights.length;
        aliases = new int[size];
        probabilities = new long[size];

        // compute the total weight
        int totalWeight = 0;
        for (int weight : weights) {
            totalWeight += weight;
        }

        // populate the "small" and "large" stacks.
        Stack<Integer> smalls = Stack.empty(), larges = Stack.empty();
        for (int i = 0; i < size; ++i) {
            // normalize each int weight, such that the average weight is 1.
            long p = encode(weights[i] * size, totalWeight);
            probabilities[i] = p;

            // p < 1 is "small", p ≥ 1 is "large."
            if (numerator(p) < denominator(p)) {
                smalls = smalls.push(i);
            } else {
                larges = larges.push(i);
            }
        }

        while (!smalls.isEmpty()) {
            // pop a small and large element from the top of each stack.
            int small = smalls.top();
            smalls = smalls.pop();
            int large = larges.top();
            larges = larges.pop();

            // p = p_large - (1 - p_small).
            long p = plus(plus(probabilities[large], probabilities[small]), NEGATIVE_ONE);

            // as above, p < 1 is "small", p ≥ 1 is "large."
            if (numerator(p) < denominator(p)) {
                smalls = smalls.push(large);
            } else {
                larges = larges.push(large);
            }

            // set the alias for the small element, and adjust the probability
            // of the large element.
            aliases[small] = large;
            probabilities[large] = p;
        }
    }

    /**
     * Return index <em>i</em> with probability <em>w<sub>i</sub> / Σw</em>,
     * using a given random number generator.
     * @param random Java standard random number generator.
     * @return a random index.
     */
    public int next(Random random) {
        int bin = random.nextInt(size);
        long p = probabilities[bin];
        return random.nextInt(denominator(p)) < numerator(p) ? bin : aliases[bin];
    }

    /**
     * Return index <em>i</em> with probability <em>w<sub>i</sub> / Σw</em>,
     * using the current thread's random number generator.
     * @return a random index.
     * @see #next(Random)
     * @see ThreadLocalRandom
     */
    public int next() {
        return next(ThreadLocalRandom.current());
    }
}
