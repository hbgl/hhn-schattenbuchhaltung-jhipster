package dev.hbgl.hhn.schattenbuchhaltung.domain.elasticsearch;

import dev.hbgl.hhn.schattenbuchhaltung.domain.Tag;
import java.nio.charset.StandardCharsets;
import org.elasticsearch.action.ingest.DeletePipelineRequest;
import org.elasticsearch.action.ingest.GetPipelineRequest;
import org.elasticsearch.action.ingest.PutPipelineRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.bytes.BytesArray;
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
        return et;
    }

    public static class Content {

        @Field(type = FieldType.Text)
        public String fallback;

        @Field(type = FieldType.Text)
        public String de;

        @Field(type = FieldType.Text)
        public String en;

        @Field(type = FieldType.Keyword)
        public String language;

        @Field(type = FieldType.Boolean)
        public Boolean supported;

        public static Content fromContent(String content) {
            var text = new Content();
            text.fallback = content;
            return text;
        }
    }

    public static void setup(RestHighLevelClient client) throws Exception {
        setupPipeline(client);
        setupIndex(client);
    }

    private static void setupPipeline(RestHighLevelClient client) throws Exception {
        // TODO: Remove debug code
        // try {
        //     var pipelineDeleteReq1 = new DeletePipelineRequest(PIPELINE_NAME_LANG);
        //     client.ingest().deletePipeline(pipelineDeleteReq1, RequestOptions.DEFAULT);
        // } catch (Throwable t) {}

        // try {
        //     var pipelineDeleteReq2 = new DeletePipelineRequest(PIPELINE_NAME_DEFAULT);
        //     client.ingest().deletePipeline(pipelineDeleteReq2, RequestOptions.DEFAULT);
        // } catch (Throwable t) {}

        var langPipeExistsReq = new GetPipelineRequest(PIPELINE_NAME_DEFAULT);
        var langPipeExistsRes = client.ingest().getPipeline(langPipeExistsReq, RequestOptions.DEFAULT);
        if (!langPipeExistsRes.isFound()) {
            var langPipePutReq = new PutPipelineRequest(
                PIPELINE_NAME_LANG,
                new BytesArray(
                    """
                {
                    "processors": [
                        {
                            "inference": {
                                "model_id": "lang_ident_model_1",
                                "inference_config": {
                                    "classification": {
                                        "num_top_classes": 3
                                    }
                                },
                                "field_map": {
                                    "content.fallback": "text"
                                },
                                "target_field": "_ml.lang_ident"
                            }
                        },
                        {
                            "rename": {
                                "field": "_ml.lang_ident.predicted_value",
                                "target_field": "content.language"
                            }
                        },
                        {
                            "script": {
                                "lang": "painless",
                                "source": "ctx.content.supported = (['de', 'en'].contains(ctx.content.language))"
                            }
                        },
                        {
                            "set": {
                                "if": "ctx.content.supported",
                                "field": "content.{{content.language}}",
                                "value": "{{content.fallback}}",
                                "override": false
                            }
                        },
                        {
                            "remove": {
                                "field": "_ml"
                            }
                        },
                        {
                            "remove": {
                                "if": "ctx.content.supported",
                                "field": "content.fallback"
                            }
                        }
                    ]
                }
                """.getBytes(
                            StandardCharsets.UTF_8
                        )
                ),
                XContentType.JSON
            );
            client.ingest().putPipeline(langPipePutReq, RequestOptions.DEFAULT);
        }

        var defaultPipeExistsReq = new GetPipelineRequest(PIPELINE_NAME_DEFAULT);
        var defaultPipeExistsRes = client.ingest().getPipeline(defaultPipeExistsReq, RequestOptions.DEFAULT);
        if (!defaultPipeExistsRes.isFound()) {
            var defaultPipePutReq = new PutPipelineRequest(
                PIPELINE_NAME_DEFAULT,
                new BytesArray(
                    """
                {
                    "processors": [
                        {
                            "pipeline": {
                                "if": "ctx.containsKey('content')",
                                "name": "%s"
                            }
                        }
                    ]
                }
                """.formatted(
                            PIPELINE_NAME_LANG
                        )
                        .getBytes(StandardCharsets.UTF_8)
                ),
                XContentType.JSON
            );
            client.ingest().putPipeline(defaultPipePutReq, RequestOptions.DEFAULT);
        }
    }

    private static void setupIndex(RestHighLevelClient client) throws Exception {
        var existsReq = new GetIndexRequest(INDEX_NAME);
        if (client.indices().exists(existsReq, RequestOptions.DEFAULT)) {
            return;
        }

        var createReq = new CreateIndexRequest(INDEX_NAME);
        createReq.mapping(
            """
        {
            "properties": {
                "content": {
                    "properties": {
                        "language": {
                            "type": "keyword"
                        },
                        "supported": {
                            "type": "boolean"
                        },
                        "fallback": {
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
                        }
                    }
                }
            }
        }
        """,
            XContentType.JSON
        );
        createReq.settings(
            """
        {
            "index": {
                "default_pipeline": "%s"
            }
        }
        """.formatted(
                    PIPELINE_NAME_DEFAULT
                ),
            XContentType.JSON
        );
        client.indices().create(createReq, RequestOptions.DEFAULT);
    }
}
// Run this query to search tags.
// GET /tag/_search
// {
//   "query": {
//     "dis_max": {
//       "queries": [
//         {
//           "multi_match" : {
//             "query": "admin",
//             "type": "phrase_prefix",
//             "fields": [ "content.de", "content.en", "content.fallback" ]
//           }
//         }
//       ]
//     }
//   }
// }
