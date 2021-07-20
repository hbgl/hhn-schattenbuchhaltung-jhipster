package dev.hbgl.hhn.schattenbuchhaltung.repository;

import dev.hbgl.hhn.schattenbuchhaltung.domain.CostType;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the CostType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CostTypeRepository extends JpaRepository<CostType, Long> {
    @Query("SELECT ct FROM CostType ct WHERE ct.no IN ?1")
    List<CostType> findByNos(Iterable<String> nos);
}
