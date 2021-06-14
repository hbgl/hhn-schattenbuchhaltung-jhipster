package dev.hbgl.hhn.schattenbuchhaltung.repository.search;

import dev.hbgl.hhn.schattenbuchhaltung.domain.Comment;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Comment} entity.
 */
public interface CommentSearchRepository extends ElasticsearchRepository<Comment, Long> {}
