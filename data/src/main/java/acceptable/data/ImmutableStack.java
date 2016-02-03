package acceptable.data;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class ImmutableStack<A> {
    private final A head;
    private final ImmutableStack<A> tail;

    private static final ImmutableStack<Object> nil =
            new ImmutableStack<>(null, null);

    private ImmutableStack(A head, ImmutableStack<A> tail) {
        this.head = head;
        this.tail = tail;
    }

    @SuppressWarnings("unchecked")
    public static <A> ImmutableStack<A> empty() {
        return (ImmutableStack<A>) nil;
    }

    public static <A> ImmutableStack<A> of(A... values) {
        ImmutableStack<A> stack = empty();
        for (A value : values) {
            stack = stack.push(value);
        }
        return stack;
    }

    /* TODO if it's important to support pushing a supertype of A, then
     * create `public static <A, B extends A> ImmutableStack<A> push(...)`
     * where tail is declared as ImmutableStack<? extends A> */
    public ImmutableStack<A> push(A value) {
        return new ImmutableStack<>(value, this);
    }

    public boolean isEmpty() {
        return this == nil;
    }

    // TODO document "undefined behavior" or null when calling top on empty stack,
    // noting that null is safe as a value
    public A top() {
        return head;
    }

    // TODO document "undefined behavior" or null when calling pop on empty stack
    public ImmutableStack<A> pop() {
        return tail;
    }

    public <B> B match(
            Supplier<B> ifEmpty,
            BiFunction<A, ImmutableStack<A>, B> ifNonEmpty) {
        return this == nil
                ? ifEmpty.get()
                : ifNonEmpty.apply(head, tail);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ImmutableStack<?> lhs = this, rhs = (ImmutableStack<?>) o;
        while (lhs.tail != null && rhs.tail != null) {
            if (lhs == rhs) {
                return true;
            }
            if (lhs.head == null
                    ? rhs.head != null
                    : !lhs.head.equals(rhs.head)) {
                return false;
            }
            lhs = lhs.tail;
            rhs = rhs.tail;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (ImmutableStack<A> stack = this; stack != nil; stack = stack.tail) {
            // NOTE: don't use 0 if head == null. we want different numbers of
            // null elements to result in different hashCodes. write a test
            // to assert this.
            result = 31 * result + (head == null ? 17 : head.hashCode());
        }
        return result;
    }
}
