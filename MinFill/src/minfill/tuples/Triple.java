package minfill.tuples;

import java.util.Objects;

public class Triple<A, B, C> {
    public final A a;
    public final B b;
    public final C c;

    public Triple(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;

        return Objects.equals(a, triple.a) &&
               Objects.equals(b, triple.b) &&
               Objects.equals(c, triple.c);
    }

    @Override
    public int hashCode() {
        return 31 * (31 * a.hashCode() + b.hashCode()) + c.hashCode();
    }

    @Override
    public String toString() {
        return String.format("(%s, %s, %s)", a, b, c);
    }
}
