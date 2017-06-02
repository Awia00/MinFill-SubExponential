package minfill.data;

import minfill.sets.EmptySet;
import minfill.sets.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmptySetTest {
    private final Set<Object> instance = EmptySet.instance();

    @Test
    void isEmpty() {
        assertTrue(instance.isEmpty());
    }

    @Test
    void isProperSubsetOf() {
        assertFalse(instance.isProperSubsetOf(instance));

        assertTrue(instance.isProperSubsetOf(Set.of(1)));
        assertTrue(instance.isProperSubsetOf(Set.of(1, 2, 3, 4)));
    }

    @Test
    void isSubsetOf() {
        assertTrue(instance.isSubsetOf(instance));
        assertTrue(instance.isSubsetOf(Set.of(1)));
        assertTrue(instance.isSubsetOf(Set.of(1, 2, 3, 4)));
    }

    @Test
    void contains() {
        assertFalse(instance.contains(1));
    }

    @Test
    void size() {
        assertEquals(0, instance.size());
    }

    @Test
    void add() {
        Set<Object> added = instance.add(1);

        assertEquals(1, added.size());
        assertTrue(!added.isEmpty());
        assertNotSame(instance, added);
    }

    @Test
    void remove() {
        assertSame(instance, instance.remove(1));
    }

    @Test
    void union() {
        Set<Object> other = Set.of(1);

        assertSame(other, instance.union(other));
    }

    @Test
    void intersect() {
        assertSame(instance, instance.intersect(Set.of(1)));
    }

    @Test
    void minus() {
        assertSame(instance, instance.minus(Set.of(1)));

        Set<Object> other = Set.of(1);
        assertSame(other, other.minus(instance));
    }
}