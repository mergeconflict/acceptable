package acceptable.data;

import org.junit.Test;

import static org.junit.Assert.*;

public class ImmutableStackTest {

    @Test
    public void matchEmptyMustInvokeIfEmpty() {
        int actual = ImmutableStack.empty().match(
                () -> 42,
                (head, tail) -> 0);
        assertEquals(42, actual);
    }

    @Test
    public void matchNonEmptyMustInvokeIfNonEmpty() {
        int actual = ImmutableStack.of(42).match(
                () -> 0,
                (head, tail) -> head);
        assertEquals(42, actual);
    }
}
