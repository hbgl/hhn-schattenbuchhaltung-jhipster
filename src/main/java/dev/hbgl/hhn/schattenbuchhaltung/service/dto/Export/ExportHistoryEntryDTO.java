package dev.hbgl.hhn.schattenbuchhaltung.service.dto.Export;

import dev.hbgl.hhn.schattenbuchhaltung.domain.HistoryEntry;
import dev.hbgl.hhn.schattenbuchhaltung.domain.HistoryEntryRecType;
import dev.hbgl.hhn.schattenbuchhaltung.domain.User;
import dev.hbgl.hhn.schattenbuchhaltung.domain.enumeration.HistoryAction;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;

public class ExportHistoryEntryDTO {

    @NotNull
    public UUID uuid;

    @NotNull
    public Instant instant;

    @NotNull
    public HistoryAction action;

    @NotNull
    public HistoryEntryRecType recType;

    @NotNull
    public String author;

    @NotNull
    public String recId1;

    public String recId2;

    @NotNull
    public List<ExportHistoryEntryFieldDTO> fields;

    public static ExportHistoryEntryDTO fromEntity(HistoryEntry entity) {
        var dto = new ExportHistoryEntryDTO();
        dto.uuid = entity.getUuid();
        dto.instant = entity.getInstant();
        dto.action = entity.getAction();
        dto.recType = entity.getRecType();
        dto.author = entity.getAuthor().getId();
        dto.recId1 = entity.getRecId1();
        dto.recId2 = entity.getRecId2();
        dto.fields = entity.getFields().stream().map(f -> ExportHistoryEntryFieldDTO.fromEntity(f)).toList();
        return dto;
    }

    public HistoryEntry toEntity(EntityManager em) {
        var he = new HistoryEntry();
        he.setAction(this.action);
        he.setAuthor(em.getReference(User.class, this.author));
        he.setInstant(this.instant);
        he.setRecId1(this.recId1);
        he.setRecId2(this.recId2);
        he.setRecType(this.recType);
        he.setUuid(this.uuid);
        he.setFields(this.fields.stream().map(f -> f.toEntity(he)).collect(Collectors.toSet()));
        return he;
    }
}
