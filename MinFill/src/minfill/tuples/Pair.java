package minfill.tuples;

import java.util.Objects;

public class Pair<A, B> {
    public final A a;
    public final B b;

    public Pair(A a, B b) {
        if (a == null || b == null) throw new IllegalArgumentException();
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair<?, ?> pair = (Pair<?, ?>) o;

        return Objects.equals(a, pair.a) && Objects.equals(b, pair.b);
    }

    @Override
    public int hashCode() {
        return 31 * a.hashCode() + b.hashCode();
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", a, b);
    }
}
