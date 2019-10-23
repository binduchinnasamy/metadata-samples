package com.ms.cse.dqprofileapp.clients;

import java.util.logging.Logger;

import com.google.gson.JsonObject;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

public class QnsHttpClient {
    private static QnsHttpClient single_instance = null; 
    
    private Logger logger;
    private String url;
    private String typeName = "dq_rule";

    private QnsHttpClient(String url, Logger logger) 
    {
        this.url = url;
        this.logger = logger;
    } 
    
    public static QnsHttpClient getInstance(String qnsURL, Logger logger) 
    { 
        if (single_instance == null)
            single_instance = new QnsHttpClient(qnsURL, logger); 
        
        return single_instance; 
    }

    public JsonNode getQualifiedName(String columnQualifiedName, String ruleId){

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("col_qualified_name", columnQualifiedName);
        jsonBody.addProperty("rule_id", ruleId);

        HttpResponse<JsonNode> response = Unirest.post(url)
                                               .header("Content-Type", "application/json")
                                               .queryString("typeName", typeName)
                                               .body(jsonBody)
                                               .asJson();
        
        logger.info(response.getBody().toPrettyString());       
        System.out.println(response.getBody().toPrettyString());

        return response.getBody();
    }
}