package dev.hbgl.hhn.schattenbuchhaltung.repository;

import dev.hbgl.hhn.schattenbuchhaltung.domain.LedgerEntry;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the LedgerEntry entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {}
