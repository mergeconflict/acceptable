package acceptable.probability;

import acceptable.data.ImmutableStack;
import acceptable.math.Rationals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static acceptable.math.Rationals.*;

// TODO cite vose
public final class IntProbabilityDistribution<A> {
    private final int size;
    private final A[] values;
    private final int[] aliases;
    private final long[] probabilities;

    @SuppressWarnings("unchecked")
    // TODO what happens if there's a weight of 0 in here? is that legal?
    public IntProbabilityDistribution(IntWeightedValue<A>... weightedValues) {
        size = weightedValues.length;

        values = (A[]) new Object[size];
        aliases = new int[size];
        probabilities = new long[size];

        int totalWeight = 0;
        for (int i = 0; i < size; ++i) {
            totalWeight += weightedValues[i].weight;
            values[i] = weightedValues[i].value;
        }

        ImmutableStack<Integer>
                small = ImmutableStack.empty(),
                large = ImmutableStack.empty();

        for (int i = 0; i < size; ++i) {
            long p = encode(weightedValues[i].weight * size, totalWeight);
            probabilities[i] = p;
            if (numerator(p) < denominator(p)) {
                small = small.push(i);
            } else {
                large = large.push(i);
            }
        }

        while (!small.isEmpty()) {
            int l = small.top();
            small = small.pop();
            int g = large.top();
            large = large.pop();
            long p = plus(plus(probabilities[g], probabilities[l]), NEGATIVE_ONE);
            if (numerator(p) < denominator(p)) {
                small = small.push(g);
            } else {
                large = large.push(g);
            }
            aliases[l] = g;
            probabilities[g] = p;
        }

        while (!large.isEmpty()) {
            int g = large.top();
            large = large.pop();
            // TODO prove this entire loop is unnecessary???
            // probabilities[g] = IntRational.ONE;
        }
    }

    public A next(Random random) {
        int bin = random.nextInt(size);
        long p = probabilities[bin];
        return values[random.nextInt(denominator(p)) < numerator(p)
                ? bin
                : aliases[bin]];
    }

    public A next() {
        return next(ThreadLocalRandom.current());
    }

    public static void main(String[] args) {
        IntProbabilityDistribution<Character> haha =
                new IntProbabilityDistribution<>(
                        IntWeightedValue.mk(2, 'a'),
                        IntWeightedValue.mk(3, 'b'),
                        IntWeightedValue.mk(5, 'c'));
        for (long probability : haha.probabilities) {
            System.out.println(Rationals.toString(probability));
        }
        Map<Character, Integer> lol = new HashMap<>();
        lol.put('a', 0);
        lol.put('b', 0);
        lol.put('c', 0);
        Random r = ThreadLocalRandom.current();
        for (int i = 0; i < 100000000; ++i) {
            char c = haha.next(r);
            lol.put(c, lol.get(c) + 1);
        }
        System.out.println(lol);
    }
}
