package dev.hbgl.hhn.schattenbuchhaltung.repository;

import dev.hbgl.hhn.schattenbuchhaltung.domain.Division;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Division entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DivisionRepository extends JpaRepository<Division, Long> {}
