package dev.hbgl.hhn.schattenbuchhaltung.repository;

import dev.hbgl.hhn.schattenbuchhaltung.domain.TagCustomType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the TagCustomType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TagCustomTypeRepository extends JpaRepository<TagCustomType, Long> {}
