package dev.hbgl.hhn.schattenbuchhaltung.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link HistoryEntryFieldSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class HistoryEntryFieldSearchRepositoryMockConfiguration {

    @MockBean
    private HistoryEntryFieldSearchRepository mockHistoryEntryFieldSearchRepository;
}
