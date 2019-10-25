package com.ms.cse.dqprofileapp.clients;

import java.util.logging.Logger;

import com.ms.cse.dqprofileapp.models.JsonWrapperEntity;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

public class JsonCreatorHttpClient{
    private static JsonCreatorHttpClient single_instance = null; 
    
    private Logger logger;
    private String baseUrl;
    private String funcCode;

    private JsonCreatorHttpClient(String baseUrl, String funcCode, Logger logger)
    {
        this.baseUrl = baseUrl;
        this.funcCode = funcCode;
        this.logger = logger;
    } 
    
    public static JsonCreatorHttpClient getInstance(String baseUrl, String funcCode, Logger logger)
    { 
        if (single_instance == null)
            single_instance = new JsonCreatorHttpClient(baseUrl, funcCode, logger);
        
        return single_instance; 
    }

    public JsonNode getJson(JsonWrapperEntity[] entities) {
        HttpResponse<JsonNode> response =
                Unirest.post(this.baseUrl)
                    .header("Content-Type", "application/json")
                    .queryString("code", this.funcCode)
                    .body(entities)
                    .asJson();
        
        logger.info(response.getBody().toPrettyString());       

        return response.getBody();
    }
}