package dev.hbgl.hhn.schattenbuchhaltung.domain.elasticsearch;

import dev.hbgl.hhn.schattenbuchhaltung.domain.Tag;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.ingest.DeletePipelineRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = ElasticTag.INDEX_NAME, createIndex = false)
public class ElasticTag {

    public static final String INDEX_NAME = "tag";
    public static final String PIPELINE_NAME_DEFAULT = "tag";
    public static final String PIPELINE_NAME_LANG = "tag_lang";

    @Id
    public Long id;

    @Field(type = FieldType.Object)
    public Content content;

    public static ElasticTag fromEntity(Tag entity) {
        var et = new ElasticTag();
        et.id = entity.getId();
        et.content = Content.fromTag(entity);
        return et;
    }

    public static class Content {

        @Field(type = FieldType.Text)
        public String invariant;

        @Field(type = FieldType.Text)
        public String de;

        @Field(type = FieldType.Text)
        public String en;

        @Field(type = FieldType.Keyword)
        public String normalized;

        public static Content fromTag(Tag tag) {
            var content = new Content();
            content.invariant = tag.getText();
            content.de = tag.getText();
            content.en = tag.getText();
            content.normalized = tag.getTextNormalized();
            return content;
        }
    }

    public static void setup(RestHighLevelClient client) throws Exception {
        setupIndex(client);
    }

    private static void setupIndex(RestHighLevelClient client) throws Exception {
        var existsReq = new GetIndexRequest(INDEX_NAME);
        if (client.indices().exists(existsReq, RequestOptions.DEFAULT)) {
            // TODO: Remove debug code
            // var indexDeleteReq = new DeleteIndexRequest(INDEX_NAME);
            // client.indices().delete(indexDeleteReq, RequestOptions.DEFAULT);
            return;
        }

        var createReq = new CreateIndexRequest(INDEX_NAME);
        createReq.mapping(
            """
        {
            "properties": {
                "content": {
                    "properties": {
                        "invariant": {
                            "type": "text",
                            "analyzer": "default"
                        },
                        "en": {
                            "type": "text",
                            "analyzer": "english"
                        },
                        "de": {
                            "type": "text",
                            "analyzer": "german"
                        },
                        "normalized": {
                            "type":  "keyword"
                        }
                    }
                }
            }
        }
        """,
            XContentType.JSON
        );

        client.indices().create(createReq, RequestOptions.DEFAULT);
    }
}
