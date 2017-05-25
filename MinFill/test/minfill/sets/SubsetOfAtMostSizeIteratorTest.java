package minfill.sets;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class SubsetOfAtMostSizeIteratorTest {
    @Test
    public void testIterator() {
        SubsetOfAtMostSizeIterator<Integer> ite = new SubsetOfAtMostSizeIterator<>(Set.of(8, 7, 6, 5, 4, 3, 2, 1), 5);

        int count = 0;
        while (ite.hasNext()) {
            count++;
            System.out.println(ite.next());
        }

        System.out.println(count);
    }

    @Test
    public void oldTest() {
        SubsetOfAtMostSizeIterator<Integer> ite = new SubsetOfAtMostSizeIterator<>(minfill.sets.Set.of(1, 2, 3, 4, 5), 2);

        // Check for duplicate elements:
        int count = 0;
        java.util.Set<minfill.sets.Set<Integer>> retrieved = new HashSet<>();
        while (ite.hasNext()) {
            count++;
            retrieved.add(ite.next());
        }

        assertEquals(count, retrieved.size());


        // Check for known elements in subsets.
        ite = new SubsetOfAtMostSizeIterator<>(minfill.sets.Set.of(1, 2, 3), 2);

        java.util.Set<minfill.sets.Set<Integer>> expected = new HashSet<>();
        expected.add(minfill.sets.Set.of(1));
        expected.add(minfill.sets.Set.of(2));
        expected.add(minfill.sets.Set.of(3));
        expected.add(minfill.sets.Set.of(1, 2));
        expected.add(minfill.sets.Set.of(1, 3));
        expected.add(minfill.sets.Set.of(2, 3));

        while (ite.hasNext()) {
            assertTrue(expected.remove(ite.next()));
        }
        assertTrue(expected.isEmpty());
    }
}