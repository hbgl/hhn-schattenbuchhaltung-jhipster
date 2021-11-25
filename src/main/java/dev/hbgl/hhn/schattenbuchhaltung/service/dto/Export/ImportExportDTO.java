package dev.hbgl.hhn.schattenbuchhaltung.service.dto.Export;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.validation.constraints.NotNull;

public class ImportExportDTO {

    public int version;

    @NotNull
    public String userId;

    @NotNull
    public String userName;

    @NotNull
    public String userEmail;

    @NotNull
    public Instant instant;

    @NotNull
    public List<ExportHistoryEntryDTO> historyEntries;
}
