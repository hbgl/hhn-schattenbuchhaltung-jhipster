package dev.hbgl.hhn.schattenbuchhaltung.repository;

import dev.hbgl.hhn.schattenbuchhaltung.domain.CostCenter;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the CostCenter entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CostCenterRepository extends JpaRepository<CostCenter, Long> {
    @Query("SELECT cc FROM CostCenter cc WHERE cc.no IN ?1")
    List<CostCenter> findByNos(Iterable<String> nos);

    @Query("SELECT cc FROM CostCenter cc LEFT JOIN FETCH cc.parent WHERE cc.no IN ?1")
    List<CostCenter> findByNosWithParent(Iterable<String> nos);
}
