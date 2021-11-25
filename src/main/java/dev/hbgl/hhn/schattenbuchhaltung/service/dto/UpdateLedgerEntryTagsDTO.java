package dev.hbgl.hhn.schattenbuchhaltung.service.dto;

import java.io.Serializable;
import javax.validation.constraints.NotNull;

public class UpdateLedgerEntryTagsDTO implements Serializable {

    @NotNull
    public String[] assignTags;

    @NotNull
    public String[] unassignTags;
}
