package dev.hbgl.hhn.schattenbuchhaltung.repository;

import dev.hbgl.hhn.schattenbuchhaltung.domain.LedgerEntry;
import java.util.List;
import java.util.Optional;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the LedgerEntry entity.
 */
@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {
    @Query("SELECT le FROM LedgerEntry le WHERE le.no IN ?1")
    List<LedgerEntry> findByNos(Iterable<String> nos);

    Optional<LedgerEntry> findByNo(String no);

    @Query(
        value = "select distinct ledgerEntry from LedgerEntry ledgerEntry left join fetch ledgerEntry.tags",
        countQuery = "select count(distinct ledgerEntry) from LedgerEntry ledgerEntry"
    )
    Page<LedgerEntry> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct ledgerEntry from LedgerEntry ledgerEntry left join fetch ledgerEntry.tags")
    List<LedgerEntry> findAllWithEagerRelationships();

    @Query("select ledgerEntry from LedgerEntry ledgerEntry left join fetch ledgerEntry.tags where ledgerEntry.id =:id")
    Optional<LedgerEntry> findOneWithEagerRelationships(@Param("id") Long id);
}
