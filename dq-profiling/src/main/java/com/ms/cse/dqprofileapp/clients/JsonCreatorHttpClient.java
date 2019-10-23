package com.ms.cse.dqprofileapp.clients;

import java.util.logging.Logger;

import com.google.gson.JsonObject;
import com.ms.cse.dqprofileapp.models.JsonWrapperEntity;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

public class JsonCreatorHttpClient{
    private static JsonCreatorHttpClient single_instance = null; 
    
    private Logger logger;
    private String url;

    private JsonCreatorHttpClient(String url, Logger logger) 
    {
        this.url = url;
        this.logger = logger;
    } 
    
    public static JsonCreatorHttpClient getInstance(String qnsURL, Logger logger) 
    { 
        if (single_instance == null)
            single_instance = new JsonCreatorHttpClient(qnsURL, logger); 
        
        return single_instance; 
    }

    public JsonNode getJson(JsonWrapperEntity[] entities) {
        HttpResponse<JsonNode> response = Unirest.post(url)
                                               .header("Content-Type", "application/json")
                                               .body(entities)
                                               .asJson();
        
        logger.info(response.getBody().toPrettyString());       
        System.out.println(response.getBody().toPrettyString());
        
        return response.getBody();
    }
}