package com.ms.cse.dqprofileapp.clients;

import java.util.logging.Logger;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

public class AtlasWrapperHttpClient{
    private static AtlasWrapperHttpClient single_instance = null;
    private Logger logger;
    private String baseUrl;

    private AtlasWrapperHttpClient(String baseUrl, Logger logger)
    {
        this.baseUrl = baseUrl;
        this.logger = logger;
    }

    public static AtlasWrapperHttpClient getInstance(String baseUrl, Logger logger)
    {
        if (single_instance == null)
            single_instance = new AtlasWrapperHttpClient(baseUrl, logger);

        return single_instance;
    }

    public void upsertEntity(JsonNode entity){
        String entityBulkUrl = this.baseUrl + "entity";
        HttpResponse<JsonNode> response =
                Unirest.post(entityBulkUrl)
                        .header("Content-Type", "application/json")
                        .body(entity)
                        .asJson();

        logger.info("createEntity.responseBody: " + response.getBody().toPrettyString());
    }

    public JsonNode getEntity(String entityGuid){
        String searchUrl = this.baseUrl + "entity/guid/" + entityGuid;
        HttpResponse<JsonNode> response = Unirest.get(searchUrl).asJson();

        logger.info("getEntity: " + response.getBody().toPrettyString());

        return response.getBody();
    }

    public JsonNode search(String criteria){
        String searchUrl = this.baseUrl + "search";
        HttpResponse<JsonNode> response =
                Unirest.get(searchUrl)
                        .queryString("query", criteria)
                        .asJson();

        logger.info("search: " + response.getBody().toPrettyString());

        return response.getBody();
    }

    public JsonNode createBulk(JsonNode requestBody){
        String entityBulkUrl = this.baseUrl + "entity/bulk";
        HttpResponse<JsonNode> response =
                Unirest.post(entityBulkUrl)
                        .header("Content-Type", "application/json")
                        .body(requestBody)
                        .asJson();

        logger.info("createBulk.mutatedEntities: " + response.getBody().toPrettyString());

        return response.getBody();
    }
}