package com.ms.cse.dqprofileapp.clients;

import java.util.logging.Logger;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

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

    public JsonNode createBulk(JsonNode requestBody){
        String entityBulkUrl = this.baseUrl + "entity/bulk";

        HttpResponse<JsonNode> response =
                Unirest.post(entityBulkUrl)
                    .header("Content-Type", "application/json")
                    .body(requestBody)
                    .asJson();
        
        logger.info(response.getBody().toPrettyString());       
        System.out.println(response.getBody().toPrettyString());
        
        return response.getBody();
    }
}