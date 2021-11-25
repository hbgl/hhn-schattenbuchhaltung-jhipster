package dev.hbgl.hhn.schattenbuchhaltung.service.dto.Export;

public enum ExportVersion {
    VERSION_1(1);

    public final int value;

    private ExportVersion(int value) {
        this.value = value;
    }
}
