package com.ms.cse.dqprofileapp.clients;

import java.util.logging.Logger;

import com.google.gson.JsonObject;
import com.ms.cse.dqprofileapp.models.QualifiedNameServiceResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public class QnsHttpClient {
    private static QnsHttpClient single_instance = null; 
    
    private Logger logger;
    private String baseUrl;
    private String funcCode;
    private String typeName = "dq_rule";

    private QnsHttpClient(String baseUrl, String funcCode, Logger logger)
    {
        this.baseUrl = baseUrl;
        this.funcCode = funcCode;
        this.logger = logger;
    } 
    
    public static QnsHttpClient getInstance(String baseUrl, String funcCode, Logger logger)
    { 
        if (single_instance == null)
            single_instance = new QnsHttpClient(baseUrl, funcCode, logger);
        
        return single_instance; 
    }

    public QualifiedNameServiceResponse getQualifiedName(String columnQualifiedName, String ruleId){

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("col_qualified_name", columnQualifiedName);
        jsonBody.addProperty("rule_id", ruleId);

        HttpResponse<QualifiedNameServiceResponse> response =
                Unirest.post(this.baseUrl)
                        .header("Content-Type", "application/json")
                        .queryString("code", this.funcCode)
                        .queryString("typeName", typeName)
                        .body(jsonBody)
                        .asObject(QualifiedNameServiceResponse.class);

        QualifiedNameServiceResponse qnsResponse = response.getBody();

        logger.info(qnsResponse.toString());
        System.out.println("getQualifiedName.qnsResponse: " + qnsResponse.toString());

        return response.getBody();
    }
}