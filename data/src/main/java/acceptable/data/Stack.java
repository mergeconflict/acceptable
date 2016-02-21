package acceptable.data;

import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * An immutable stack.
 * @param <A> the type of element contained in the stack.
 */
public final class Stack<A> {
    private final A head;
    private final Stack<A> tail;

    private static final Stack<Object> nil =
            new Stack<>(null, null);

    private Stack(A head, Stack<A> tail) {
        this.head = head;
        this.tail = tail;
    }

    /**
     * Return an empty stack that can contain elements of type <code>A</code>
     * @param <A> the type of elements that the returned stack can contain.
     * @return an empty stack.
     */
    @SuppressWarnings("unchecked")
    public static <A> Stack<A> empty() {
        return (Stack<A>) nil;
    }

    /**
     * Return a stack containing the given elements in order from bottom to top.
     * @param elements the elements that the stack will contain, in order from
     *                 bottom to top.
     * @param <A> the type of elements that the returned stack can contain.
     * @return a stack containing the given elements.
     */
    @SafeVarargs
    public static <A> Stack<A> of(A... elements) {
        Stack<A> stack = empty();
        for (A element : elements) {
            stack = stack.push(element);
        }
        return stack;
    }

    /**
     * Return a new stack consisting of this one with the new element on top.
     * @param element the new top element for the new stack.
     * @return a new stack consisting of this one with the new element on top.
     */
    public Stack<A> push(A element) {
        return new Stack<>(element, this);
    }

    /**
     * Return true if this stack is empty, false if not.
     * @return true if this stack is empty, false if not.
     */
    public boolean isEmpty() {
        return this == nil;
    }

    /**
     * Return the element at the top of this stack. If this stack is empty, the
     * behavior of this method is undefined.
     * @return the element at the top of this stack.
     * @see #isEmpty()
     * @see #match(Supplier, BiFunction)
     */
    public A top() {
        return head;
    }

    /**
     * Return the remainder of this stack below the element at the top. If this
     * stack is empty, the behavior of this method is undefined.
     * @return the remainder of this stack below the element at the top.
     * @see #isEmpty()
     * @see #match(Supplier, BiFunction)
     */
    public Stack<A> pop() {
        return tail;
    }

    /**
     * Pattern match exhaustively over this stack.
     * @param ifEmpty supplier to invoke if this stack is empty.
     * @param ifNonEmpty function to invoke on the top element and the
     *                   remainder of the stack, if this stack is non-empty.
     * @param <B> result type of the given supplier and function.
     * @return the result of the supplier or function.
     */
    public <B> B match(
            Supplier<B> ifEmpty,
            BiFunction<A, Stack<A>, B> ifNonEmpty) {
        return this == nil
                ? ifEmpty.get()
                : ifNonEmpty.apply(head, tail);
    }
}
