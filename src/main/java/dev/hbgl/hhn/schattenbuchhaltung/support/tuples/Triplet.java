package dev.hbgl.hhn.schattenbuchhaltung.support.tuples;

import java.util.Objects;

public class Triplet<T1, T2, T3> {

    private T1 value1;
    private T2 value2;
    private T3 value3;

    private Triplet() {}

    public static <T1, T2, T3> Triplet<T1, T2, T3> create(T1 value1, T2 value2, T3 value3) {
        var instance = new Triplet<T1, T2, T3>();
        instance.value1 = value1;
        instance.value2 = value2;
        instance.value3 = value3;
        return instance;
    }

    public T1 get1() {
        return value1;
    }

    public T2 get2() {
        return value2;
    }

    public T3 get3() {
        return value3;
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
        final Triplet<?, ?, ?> other = (Triplet<?, ?, ?>) obj;
        return Objects.equals(value1, other.value1) && Objects.equals(value2, other.value2) && Objects.equals(value2, other.value3);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value1, value2, value3);
    }
}
