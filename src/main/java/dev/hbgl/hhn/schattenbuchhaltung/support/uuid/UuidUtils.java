package dev.hbgl.hhn.schattenbuchhaltung.support.uuid;

import java.util.UUID;

public class UuidUtils {

    public static int compare128(UUID a, UUID b) {
        var msb = Long.compare(a.getMostSignificantBits(), b.getMostSignificantBits());
        if (msb != 0) {
            return msb;
        }
        return Long.compare(a.getLeastSignificantBits(), b.getLeastSignificantBits());
    }
}
