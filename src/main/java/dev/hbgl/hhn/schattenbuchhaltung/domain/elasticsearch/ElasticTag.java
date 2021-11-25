package dev.hbgl.hhn.schattenbuchhaltung.domain.elasticsearch;

import dev.hbgl.hhn.schattenbuchhaltung.domain.Tag;
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

    @Field(type = FieldType.Text)
    public String invariant;

    @Field(type = FieldType.Text)
    public String de;

    @Field(type = FieldType.Text)
    public String en;

    @Field(type = FieldType.Keyword)
    public String normalized;

    public static ElasticTag fromEntity(Tag entity) {
        var instance = new ElasticTag();
        instance.id = entity.getId();
        instance.invariant = entity.getText();
        instance.de = entity.getText();
        instance.en = entity.getText();
        instance.normalized = entity.getTextNormalized();
        return instance;
    }

    public static void setup(RestHighLevelClient client) throws Exception {
        setupIndex(client);
    }

    private static void setupIndex(RestHighLevelClient client) throws Exception {
        var existsReq = new GetIndexRequest(INDEX_NAME);
        if (client.indices().exists(existsReq, RequestOptions.DEFAULT)) {
            // TODO: Remove debug code
            // var indexDeleteReq = new org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest(INDEX_NAME);
            // client.indices().delete(indexDeleteReq, RequestOptions.DEFAULT);
            return;
        }

        var createReq = new CreateIndexRequest(INDEX_NAME);
        createReq.mapping(
            """
        {
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
        """,
            XContentType.JSON
        );

        client.indices().create(createReq, RequestOptions.DEFAULT);
    }
}
