package com.ms.cse.dqprofileapp.clients;

import java.util.logging.Logger;

import com.google.gson.JsonObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

public class QnsHttpClient {
    private static QnsHttpClient single_instance = null; 
    
    private Logger logger;
    private String url;
    private String typeName = "dq_rule";

    private QnsHttpClient(String url) 
    {
        this.url = url;
        //this.logger = logger;        
        //this.logger.info("QNS Svc URL:" + qnsSvcUrl + "?code=" + qnsSvcCode);
    } 
    
    public static QnsHttpClient getInstance(String qnsURL) 
    { 
        if (single_instance == null)
            single_instance = new QnsHttpClient(qnsURL); 
        
        return single_instance; 
    }

    public void getQualifiedName(){

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("col_qualified_name", "storageuri/filesystemname/f1/f2/f3");
        jsonBody.addProperty("rule_id", "rule1");

        HttpResponse<JsonNode> response = Unirest.post(url)
                                               .header("Content-Type", "application/json")
                                               .queryString("typeName", typeName)
                                               .body(jsonBody)
                                               .asJson();
                                                
        System.out.println(response.toString());
    }
}