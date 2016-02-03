package acceptable.probability;

public final class IntWeightedValue<A> {
    public final int weight;
    public final A value;

    private IntWeightedValue(int weight, A value) {
        this.weight = weight;
        this.value = value;
    }

    public static <A> IntWeightedValue<A> mk(int weight, A value) {
        return new IntWeightedValue<>(weight, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntWeightedValue<?> intWeightedValue = (IntWeightedValue<?>) o;

        if (weight != intWeightedValue.weight) return false;
        return value.equals(intWeightedValue.value);

    }

    @Override
    public int hashCode() {
        int result = weight;
        result = 31 * result + value.hashCode();
        return result;
    }
}
