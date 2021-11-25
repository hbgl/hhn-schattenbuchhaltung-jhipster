package dev.hbgl.hhn.schattenbuchhaltung.service.dto.Export;

import dev.hbgl.hhn.schattenbuchhaltung.domain.HistoryEntry;
import dev.hbgl.hhn.schattenbuchhaltung.domain.HistoryEntryField;

public class ExportHistoryEntryFieldDTO {

    public String name;

    public String oldValue;

    public String newValue;

    public static ExportHistoryEntryFieldDTO fromEntity(HistoryEntryField historyEntryField) {
        var dto = new ExportHistoryEntryFieldDTO();
        dto.name = historyEntryField.getName();
        dto.oldValue = historyEntryField.getOldValue();
        dto.newValue = historyEntryField.getNewValue();
        return dto;
    }

    public HistoryEntryField toEntity(HistoryEntry he) {
        var hef = new HistoryEntryField();
        hef.setEntry(he);
        hef.setName(this.name);
        hef.setOldValue(this.oldValue);
        hef.setNewValue(this.newValue);
        return hef;
    }
}
