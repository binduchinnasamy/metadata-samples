package com.ms.cse.dqprofileapp.clients;

import java.util.logging.Logger;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.UnsupportedEncodingException;

public class AtlasWrapperHttpClient{
//url + "entity/bulk"

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

    public void createEntity(String entityGuid, JSONObject entity){
        String entityBulkUrl = this.baseUrl + "entity/guid/" + entityGuid;

        HttpResponse<JsonNode> responseBody =
                Unirest.post(entityBulkUrl)
                        .header("Content-Type", "application/json")
                        .body(entity)
                        .asJson();

        System.out.println("createEntity.responseBody: " + responseBody.getBody().toPrettyString());
    }
    public JsonNode getEntity(String entityGuid){
        String searchUrl = this.baseUrl + "entity/guid/" + entityGuid;

        HttpResponse<JsonNode> result = Unirest.get(searchUrl)
                .asJson();

        return result.getBody();
    }
    public JsonNode search(String criteria){
        String searchUrl = this.baseUrl + "search";
        //String urlEncodedCriteria = encodeString(criteria);

        HttpResponse<JsonNode> result = Unirest.get(searchUrl)
                                                .queryString("query", criteria)
                                                .asJson();

        return result.getBody();
    }
    public JsonNode create(JsonNode requestBody){
        String entityBulkUrl = this.baseUrl + "entity/bulk";

        HttpResponse<JsonNode> responseBody =
                Unirest.post(entityBulkUrl)
                        .header("Content-Type", "application/json")
                        .body(requestBody)
                        .asJson();

        System.out.println("createBulk.mutatedEntities: " + responseBody.getBody().toPrettyString());

        //        HttpResponse<MutatedEntities> response =
        //                Unirest.post(entityBulkUrl)
        //                    .header("Content-Type", "application/json")
        //                    .body(requestBody)
        //                    .asObject(MutatedEntities.class);

        //        MutatedEntities mutatedEntities = response.getBody();
        //
        //        logger.info(mutatedEntities.toString());
        //        System.out.println("createBulk.mutatedEntities: " + mutatedEntities.toString());
        
        return responseBody.getBody();
    }
    private String encodeString(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }
}