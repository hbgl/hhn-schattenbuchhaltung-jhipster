package dev.hbgl.hhn.schattenbuchhaltung.repository;

import dev.hbgl.hhn.schattenbuchhaltung.domain.Comment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Comment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select comment from Comment comment where comment.author.login = ?#{principal.preferredUsername}")
    List<Comment> findByAuthorIsCurrentUser();

    @Query("select comment from Comment comment left join fetch comment.author where comment.id = :id")
    Optional<Comment> findByIdWithAuthor(@Param("id") Long id);
}
