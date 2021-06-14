package dev.hbgl.hhn.schattenbuchhaltung.repository;

import dev.hbgl.hhn.schattenbuchhaltung.domain.HistoryEntry;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the HistoryEntry entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HistoryEntryRepository extends JpaRepository<HistoryEntry, Long> {}
