package dev.hbgl.hhn.schattenbuchhaltung.repository;

import dev.hbgl.hhn.schattenbuchhaltung.domain.TagCustomValue;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the TagCustomValue entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TagCustomValueRepository extends JpaRepository<TagCustomValue, Long> {}
