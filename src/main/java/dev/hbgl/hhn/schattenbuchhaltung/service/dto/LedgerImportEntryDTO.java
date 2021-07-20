package dev.hbgl.hhn.schattenbuchhaltung.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.*;

public class LedgerImportEntryDTO implements Serializable {

    @NotNull
    public String year;

    @NotNull
    public CostCenterDTO costCenter1;

    @NotNull
    public CostCenterDTO costCenter2;

    @NotNull
    public CostCenterDTO costCenter3;

    @NotNull
    public String no;

    @NotNull
    public String description;

    public String aNo;

    @NotNull
    public String bookingDate;

    @NotNull
    public BigDecimal income;

    @NotNull
    public BigDecimal expenditure;

    @NotNull
    public BigDecimal liability;

    public String divisionNo;

    public String divisionName;

    public String costTypeNo;

    public String costTypeName;
}
