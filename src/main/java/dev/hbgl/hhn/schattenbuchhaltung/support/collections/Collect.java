package dev.hbgl.hhn.schattenbuchhaltung.support.collections;

import java.util.ArrayList;
import java.util.function.Function;

public class Collect {

    public static <T, R> ArrayList<R> pluckToList(Iterable<T> iterable, Function<? super T, ? extends R> map) {
        var mapped = new ArrayList<R>();
        for (var item : iterable) {
            mapped.add(map.apply(item));
        }
        return mapped;
    }
}
