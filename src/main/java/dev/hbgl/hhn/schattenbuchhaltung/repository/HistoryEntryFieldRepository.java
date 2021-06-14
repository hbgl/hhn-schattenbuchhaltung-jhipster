package dev.hbgl.hhn.schattenbuchhaltung.repository;

import dev.hbgl.hhn.schattenbuchhaltung.domain.HistoryEntryField;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the HistoryEntryField entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HistoryEntryFieldRepository extends JpaRepository<HistoryEntryField, Long> {}
