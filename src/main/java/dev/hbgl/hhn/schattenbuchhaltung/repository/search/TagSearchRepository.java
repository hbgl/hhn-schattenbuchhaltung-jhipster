package dev.hbgl.hhn.schattenbuchhaltung.repository.search;

import dev.hbgl.hhn.schattenbuchhaltung.domain.elasticsearch.ElasticTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link ElasticTag} entity.
 */
public interface TagSearchRepository extends ElasticsearchRepository<ElasticTag, Long> {
    @Query(
        """
    {
        "dis_max": {
            "queries": [
                {
                    "term": {
                        "content.normalized": {
                            "value": "?1",
                            "boost": 42.0
                        }
                    }
                },
                {
                    "multi_match" : {
                        "query": "?0",
                        "type": "phrase_prefix",
                        "fields": [ "content.de", "content.en", "content.fallback" ]
                    }
                },
                {
                    "multi_match" : {
                        "query": "?0",
                        "type": "best_fields",
                        "fields": [ "content.de", "content.en", "content.fallback" ]
                    }
                }
            ]
        }
    }
    """
    )
    Page<ElasticTag> findTextAutocomplete(String text, String textNormalized, Pageable pageable);
}
