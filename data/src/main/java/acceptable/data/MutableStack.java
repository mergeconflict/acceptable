package acceptable.data;

import java.util.function.Function;
import java.util.function.Supplier;

public class MutableStack<A> {
    private ImmutableStack<A> stack;

    private MutableStack(ImmutableStack<A> stack) {
        this.stack = stack;
    }

    public static <A> MutableStack<A> empty() {
        return new MutableStack<>(ImmutableStack.empty());
    }

    public static <A> MutableStack<A> of(A... values) {
        return new MutableStack<>(ImmutableStack.of(values));
    }

    public void push(A value) {
        stack = stack.push(value);
    }

    public A top() {
        return stack.top();
    }

    public void pop() {
        stack = stack.pop();
    }

    // is this stupid?
    public <B> B match(
            Supplier<B> ifEmpty,
            Function<A, B> ifNonEmpty) {
        return stack.match(
                ifEmpty,
                (head, tail) -> {
                    stack = tail;
                    return ifNonEmpty.apply(head);
                }
        );
    }
}
