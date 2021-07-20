package dev.hbgl.hhn.schattenbuchhaltung.repository;

import dev.hbgl.hhn.schattenbuchhaltung.domain.LedgerEntry;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the LedgerEntry entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {
    @Query("SELECT le FROM LedgerEntry le WHERE le.no IN ?1")
    List<LedgerEntry> findByNos(Iterable<String> nos);
}
