package com.ms.cse.dqprofileapp.clients;

import java.util.logging.Logger;

import com.ms.cse.dqprofileapp.models.MutatedEntities;
import com.ms.cse.dqprofileapp.models.QualifiedNameServiceResponse;
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

    public MutatedEntities createBulk(JsonNode requestBody){
        String entityBulkUrl = this.baseUrl + "entity/bulk";

        HttpResponse<MutatedEntities> response =
                Unirest.post(entityBulkUrl)
                    .header("Content-Type", "application/json")
                    .body(requestBody)
                    .asObject(MutatedEntities.class);

        MutatedEntities mutatedEntities = response.getBody();

        logger.info(mutatedEntities.toString());
        System.out.println("createBulk.mutatedEntities: " + mutatedEntities.toString());
        
        return mutatedEntities;
    }
}