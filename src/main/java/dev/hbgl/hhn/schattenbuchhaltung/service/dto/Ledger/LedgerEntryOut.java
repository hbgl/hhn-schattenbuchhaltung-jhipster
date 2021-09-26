package dev.hbgl.hhn.schattenbuchhaltung.service.dto.Ledger;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import dev.hbgl.hhn.schattenbuchhaltung.domain.Comment;
import dev.hbgl.hhn.schattenbuchhaltung.domain.LedgerEntry;
import dev.hbgl.hhn.schattenbuchhaltung.domain.LedgerEntryTag;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityManager;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class LedgerEntryOut {

    public Long id;

    public String no;

    public String description;

    public String aNo;

    public Instant bookingDate;

    public BigDecimal income;

    public BigDecimal expenditure;

    public BigDecimal liability;

    public Iterable<CommentOut> comments;

    public Iterable<TagOut> tags;

    public CostCenterOut costCenter1;

    public CostCenterOut costCenter2;

    public CostCenterOut costCenter3;

    public DivisionOut division;

    public CostTypeOut costType;

    public static LedgerEntryOut fromEntity(LedgerEntry entity, Relations relations, EntityManager entityManager) {
        if (entity == null) {
            return null;
        }
        var vm = new LedgerEntryOut();
        vm.id = entity.getId();
        vm.no = entity.getNo();
        vm.description = entity.getDescription();
        vm.aNo = entity.getaNo();
        vm.bookingDate = entity.getBookingDate();
        vm.income = entity.getIncome();
        vm.expenditure = entity.getExpenditure();
        vm.liability = entity.getLiability();
        var util = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();

        Stream<Comment> commentStream = null;
        if (relations != null && relations.comments != null) {
            commentStream = relations.comments.stream();
        } else if (util.isLoaded(entity, "comments")) {
            commentStream = entity.getComments().stream();
        }
        if (commentStream != null) {
            vm.comments = commentStream.map(CommentOut::fromEntity).collect(Collectors.toList());
        }

        Stream<LedgerEntryTag> ledgerEntryTagStream = null;
        if (relations != null && relations.ledgerEntryTags != null) {
            ledgerEntryTagStream = relations.ledgerEntryTags.stream();
        } else if (util.isLoaded(entity, "ledgerEntryTags")) {
            ledgerEntryTagStream = entity.getLedgerEntryTags().stream();
        }
        if (ledgerEntryTagStream != null) {
            vm.tags = ledgerEntryTagStream.map(let -> TagOut.fromEntity(let.getTag())).collect(Collectors.toList());
        }

        vm.costCenter1 = CostCenterOut.fromEntity(entity.getCostCenter1());
        vm.costCenter2 = CostCenterOut.fromEntity(entity.getCostCenter2());
        vm.costCenter3 = CostCenterOut.fromEntity(entity.getCostCenter3());
        vm.division = DivisionOut.fromEntity(entity.getDivision());
        vm.costType = CostTypeOut.fromEntity(entity.getCostType());
        return vm;
    }

    public static class Relations {

        public List<Comment> comments;
        public List<LedgerEntryTag> ledgerEntryTags;
    }
}
