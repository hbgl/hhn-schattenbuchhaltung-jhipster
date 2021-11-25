package dev.hbgl.hhn.schattenbuchhaltung.support.history;

import dev.hbgl.hhn.schattenbuchhaltung.domain.HistoryEntry;
import dev.hbgl.hhn.schattenbuchhaltung.domain.enumeration.HistoryAction;
import dev.hbgl.hhn.schattenbuchhaltung.support.uuid.UuidUtils;
import java.util.List;

public class HistoryUtils {

    public static HistoryEntry newest(HistoryEntry a, HistoryEntry b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        var cmp = a.getInstant().compareTo(b.getInstant());
        if (cmp == 0) {
            // Arbitrary tiebreaker when instants are equal.
            cmp = UuidUtils.compare128(a.getUuid(), b.getUuid());
        }
        if (cmp > 0) {
            return a;
        } else {
            return b;
        }
    }

    public static int findSyncStartIndex(List<HistoryEntry> entries) {
        var start = 0;
        for (; start < entries.size(); start++) {
            var action = entries.get(start).getAction();
            if (action == HistoryAction.CREATE || action == HistoryAction.DELETE) {
                break;
            }
        }
        if (start >= entries.size()) {
            start--;
        }
        return start;
    }
}
