package acceptable.stopwatch;

/**
 * <p>NanotimeStopwatch is an approximate timer in constant space, based loosely
 * on Varghese &amp; Lauck, "Hashed and Hierarchical Timing Wheels: Efficient
 * Data Structures for Implementing a Timer Facility". A stopwatch has a fixed
 * capacity of active, nanosecond-precision timers. Timers are identified by
 * index, and the user is responsible for externally mapping timer indices to
 * whatever is being timed (such as requests). Each timer is
 * {@link #start(long) started} with a duration, and if it isn't explicitly
 * {@link #stop(int) stopped}, then at some {@link #tick(int[]) tick} after that
 * duration elapses the timer will expire.</p>
 *
 * <p>A stopwatch has a "resolution," which should be set to approximately how
 * frequently the user intends to call {@link #tick(int[]) tick}. The resolution
 * affects how timers are hashed internally, and roughly determines how many
 * active timers will be checked for expiration on each tick.</p>
 *
 * <p><strong>Note that this implementation is not synchronized.</strong></p>
 */
public final class NanotimeStopwatch {

    /**
     * The frequency, in nanoseconds, at which {@link #tick(int[]) tick} is
     * called.
     */
    public final long resolution;

    /**
     * The maximum number of concurrently active timers.
     */
    public final int capacity;

    private final boolean[] actives;
    private final long[] startTimes;
    private final long[] stopTimes;

    private int cursor = nanotimeToIndex(System.nanoTime());
    private int activeCount = 0;

    /**
     * Exception thrown when a user attempts to {@link #start(long) start} a
     * new timer in a {@link NanotimeStopwatch} that has reached its
     * {@link #capacity capacity}.
     */
    public static final class Overflow extends RuntimeException {}

    /**
     * Exception thrown when a user attempts to {@link #stop(int) stop} an
     * inactive timer in a {@link NanotimeStopwatch}.
     */
    public static final class Inactive extends RuntimeException {}

    /**
     * Construct a NanotimeStopwatch with a given resolution and capacity. The
     * user is responsible for calling {@link #tick(int[]) tick} at
     * approximately the rate specified by the resolution.
     * @param resolution The frequency, in nanoseconds, at which
     *                   {@link #tick(int[]) tick} is called.
     * @param capacity The maximum number of concurrently active timers.
     */
    public NanotimeStopwatch(long resolution, int capacity) {
        this.resolution = resolution;
        this.capacity = capacity;
        startTimes = new long[capacity];
        stopTimes = new long[capacity];
        actives = new boolean[capacity];
    }

    /**
     * Start a new timer with a given duration. If the timer isn't explicitly
     * {@link #stop(int) stopped}, then at some {@link #tick(int[]) tick} after
     * the duration elapses the timer will expire.
     * @param duration The duration, in nanoseconds, of the timer.
     * @return The index of the timer; the user is responsible for externally
     * mapping timer indices to whatever is being timed (such as requests).
     * @throws Overflow Thrown if the number of active timers is at
     * {@link #capacity}.
     */
    public int start(long duration) throws Overflow {
        // prevent the infinite loop that would occur below if full
        if (activeCount == capacity)
            throw new Overflow();

        // record the start and stop times, and find the next available index
        // (using linear probing) that doesn't reference an active timer
        long startTime = System.nanoTime(), stopTime = startTime + duration;
        int index = nanotimeToIndex(stopTime);
        while (actives[index]) {
            index = (index + 1) % capacity;
        }

        // activate the timer and return its index
        actives[index] = true;
        startTimes[index] = startTime;
        stopTimes[index] = stopTime;
        ++activeCount;
        return index;
    }

    /**
     * Stop an active timer, and return the time elapsed since it was
     * {@link #start(long) started}. Note that this elapsed time may exceed the
     * duration specified, because {@link #tick(int[]) tick} might not have been
     * called, because of a hash collision, or both. If these times chronically
     * or significantly exceed the expected duration, that probably suggests a
     * need to adjust the resolution and tick frequency.
     * @param index The index of the timer.
     * @return The time, in nanoseconds, elapsed since the timer was started.
     * @throws Inactive Thrown if the index references an inactive timer.
     */
    public long stop(int index) throws Inactive {
        // prevent screwing up activeCount below if the timer isn't active
        if (!actives[index])
            throw new Inactive();

        // deactivate the timer and return the elapsed time since start
        actives[index] = false;
        --activeCount;
        return System.nanoTime() - startTimes[index];
    }

    /**
     * Deactivate and return all timers that have expired since the previous
     * tick. This is a "C-style" method: to avoid allocating a new data
     * structure to contain these timers at each tick, this implementation
     * requires the user to allocate an appropriately sized buffer, and returns
     * a length. All expired timer indices will be copied to elements
     * <code>[0, length)</code> in the buffer. To be completely safe, the buffer
     * should be sized equal to the {@link #capacity} of this stopwatch.
     * @param expired Buffer into which all expired timers will be copied.
     * @return The number of expired timers.
     */
    public int tick(int[] expired) {
        long now = System.nanoTime();
        int length = 0;
        for (int endCursor = (nanotimeToIndex(now) + 1) % capacity;
             cursor != endCursor;
             cursor = (cursor + 1) % capacity) {
            if (stopTimes[cursor] - now < 0) {
                actives[cursor] = false;
                --activeCount;
                expired[length++] = cursor;
            }
        }
        return length;
    }

    private int nanotimeToIndex(long nanotime) {
        return (int) (nanotime / resolution % capacity);
    }
}
