package acceptable.probability;

// TODO eliminate this dependency
import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertFalse;

// TODO implement quickcheck clone
public class IntProbabilityDistributionTest {

    private final ChiSquareTest chiSquareTest = new ChiSquareTest();
    private final Random random = new Random(0);

    @Test
    public void chiSquareTestMustNeverRejectNullHypothesis() {
        for (int trial = 0; trial < 100; ++trial) {
            int size = random.nextInt(18) + 2;
            int weights[] = new int[size];
            int totalWeight = 0;
            for (int i = 0; i < size; ++i) {
                int weight = random.nextInt(9) + 1;
                weights[i] = weight;
                totalWeight += weight;
            }
            double[] expected = new double[size];
            for (int i = 0; i < size; ++i) {
                expected[i] = (double) weights[i] / totalWeight;
            }

            IntProbabilityDistribution d = new IntProbabilityDistribution(weights);
            long[] observed = new long[size];
            for (int sample = 0; sample < 1000000; ++sample) {
                int index = d.next(random);
                ++observed[index];
            }

            assertFalse(chiSquareTest.chiSquareTest(expected, observed, 0.01));
        }
    }
}
