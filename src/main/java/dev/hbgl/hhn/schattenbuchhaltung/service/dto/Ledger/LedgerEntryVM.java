package dev.hbgl.hhn.schattenbuchhaltung.service.dto.Ledger;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import dev.hbgl.hhn.schattenbuchhaltung.domain.LedgerEntry;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class LedgerEntryVM {

    public Long id;

    public String no;

    public String description;

    public String aNo;

    public Instant bookingDate;

    public BigDecimal income;

    public BigDecimal expenditure;

    public BigDecimal liability;

    public Iterable<CommentVM> comments;

    public Iterable<TagVM> tags;

    public CostCenterVM costCenter1;

    public CostCenterVM costCenter2;

    public CostCenterVM costCenter3;

    public DivisionVM division;

    public CostTypeVM costType;

    public static LedgerEntryVM fromEntity(LedgerEntry entity, EntityManager entityManager) {
        if (entity == null) {
            return null;
        }
        var vm = new LedgerEntryVM();
        vm.id = entity.getId();
        vm.no = entity.getNo();
        vm.description = entity.getDescription();
        vm.aNo = entity.getaNo();
        vm.bookingDate = entity.getBookingDate();
        vm.income = entity.getIncome();
        vm.expenditure = entity.getExpenditure();
        vm.liability = entity.getLiability();
        var util = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
        if (util.isLoaded(entity, "comments")) {
            vm.comments = entity.getComments().stream().map(CommentVM::fromEntity).collect(Collectors.toList());
        }
        if (util.isLoaded(entity, "tags")) {
            vm.tags = entity.getTags().stream().map(TagVM::fromEntity).collect(Collectors.toList());
        }
        vm.costCenter1 = CostCenterVM.fromEntity(entity.getCostCenter1());
        vm.costCenter2 = CostCenterVM.fromEntity(entity.getCostCenter2());
        vm.costCenter3 = CostCenterVM.fromEntity(entity.getCostCenter3());
        vm.division = DivisionVM.fromEntity(entity.getDivision());
        vm.costType = CostTypeVM.fromEntity(entity.getCostType());
        return vm;
    }
}
