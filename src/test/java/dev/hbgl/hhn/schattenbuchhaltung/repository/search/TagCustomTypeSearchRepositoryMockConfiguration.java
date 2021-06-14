package dev.hbgl.hhn.schattenbuchhaltung.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link TagCustomTypeSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class TagCustomTypeSearchRepositoryMockConfiguration {

    @MockBean
    private TagCustomTypeSearchRepository mockTagCustomTypeSearchRepository;
}
