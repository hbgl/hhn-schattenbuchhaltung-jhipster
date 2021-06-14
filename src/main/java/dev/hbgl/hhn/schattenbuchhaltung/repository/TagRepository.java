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
    @Query("select tag from Tag tag where tag.author.login = ?#{principal.preferredUsername}")
    List<Tag> findByAuthorIsCurrentUser();

    @Query("select tag from Tag tag where tag.person.login = ?#{principal.preferredUsername}")
    List<Tag> findByPersonIsCurrentUser();
}
