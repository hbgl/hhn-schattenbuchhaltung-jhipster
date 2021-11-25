package dev.hbgl.hhn.schattenbuchhaltung.support.tuples;

import java.util.Objects;

public class Pair<T1, T2> {

    private T1 value1;
    private T2 value2;

    private Pair() {}

    public static <T1, T2> Pair<T1, T2> create(T1 value1, T2 value2) {
        var pair = new Pair<T1, T2>();
        pair.value1 = value1;
        pair.value2 = value2;
        return pair;
    }

    public T1 get1() {
        return value1;
    }

    public T2 get2() {
        return value2;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pair<?, ?> other = (Pair<?, ?>) obj;
        return Objects.equals(value1, other.value1) && Objects.equals(value2, other.value2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value1, value2);
    }
}
