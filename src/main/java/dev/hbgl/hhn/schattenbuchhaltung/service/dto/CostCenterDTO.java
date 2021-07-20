package dev.hbgl.hhn.schattenbuchhaltung.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import javax.validation.constraints.*;

public class CostCenterDTO implements Serializable {

    @NotNull
    public String no;

    @NotNull
    public String name;

    @JsonIgnore
    public int rank;

    @JsonIgnore
    public String parentNo;

    @JsonIgnore
    public int index;
}
