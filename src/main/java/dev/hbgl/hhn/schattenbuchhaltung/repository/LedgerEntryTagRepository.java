package dev.hbgl.hhn.schattenbuchhaltung.repository;

import dev.hbgl.hhn.schattenbuchhaltung.domain.LedgerEntryTag;
import dev.hbgl.hhn.schattenbuchhaltung.domain.Tag;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the LedgerEntryTag entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LedgerEntryTagRepository extends JpaRepository<LedgerEntryTag, Long> {
    int deleteByTagId(Long tagId);

    @Modifying
    @Query("DELETE FROM LedgerEntryTag let WHERE let.tag IN ?1")
    int deleteAllByTags(Iterable<Tag> tags);

    @Query("SELECT let FROM LedgerEntryTag let WHERE let.ledgerEntry.id = ?1 AND let.tag.id IN ?2")
    List<LedgerEntryTag> findAllByLedgerAndTagId(Long ledgerEntryId, Iterable<Long> tagIds);
}
