package dev.hbgl.hhn.schattenbuchhaltung.repository;

import dev.hbgl.hhn.schattenbuchhaltung.domain.Tag;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Tag entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    @Query("SELECT t FROM Tag t WHERE t.textNormalized IN ?1")
    List<Tag> findAllByNormalizedText(Iterable<String> normalizedTexts);

    @Query("DELETE FROM Tag t WHERE t.textNormalized IN ?1")
    Long deleteAllByNormalizedText(Iterable<String> normalizedTexts);
}
