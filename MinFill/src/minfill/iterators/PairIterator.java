package minfill.iterators;

import minfill.sets.Set;
import minfill.tuples.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class PairIterator<T extends Comparable<T>> implements Iterator<Pair<T,T>> {
    private List<T> source;
    private int outerIndex = 0, innerIndex = 1;

    public PairIterator(Set<T> source) {
        this.source = new ArrayList<>();
        for (T vertex : source) {
            this.source.add(vertex);
        }
    }

    @Override
    public boolean hasNext() {
        return outerIndex < source.size() && innerIndex < source.size();
    }

    @Override
    public Pair<T,T> next() {
        Pair<T,T> element = new Pair<>(source.get(outerIndex), source.get(innerIndex));
        innerIndex = (innerIndex+1)% source.size();
        if(innerIndex==0)
        {
            outerIndex++;
            innerIndex = outerIndex+1;
        }
        return element;
    }


    public static void main(String[] args){
        PairIterator<Integer> iterator = new PairIterator<Integer>(Set.empty());
        assert(!iterator.hasNext());

        iterator = new PairIterator<>(Set.of(1));
        assert(!iterator.hasNext());

        iterator = new PairIterator<>(Set.of(1,2));
        Pair<Integer,Integer> pair = iterator.next();
        assert (Objects.equals(pair,new Pair<>(1, 2)));
        assert (!iterator.hasNext());

        iterator = new PairIterator<>(Set.of(1,2,3,4));
        pair = iterator.next();
        assert (Objects.equals(pair, new Pair<>(1, 2)));
        pair = iterator.next();
        assert (Objects.equals(pair, new Pair<>(1, 3)));
        pair = iterator.next();
        assert (Objects.equals(pair, new Pair<>(1, 4)));
        pair = iterator.next();
        assert (Objects.equals(pair, new Pair<>(2, 3)));
        pair = iterator.next();
        assert (Objects.equals(pair, new Pair<>(2, 4)));
        pair = iterator.next();
        assert (Objects.equals(pair, new Pair<>(3, 4)));
        assert (!iterator.hasNext());
    }
}
