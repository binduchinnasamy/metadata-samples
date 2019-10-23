package com.ms.cse.dqprofileapp.clients;

import java.util.logging.Logger;

public class AtlasWrapperHttpClient{
//url + "entity/bulk"
//no code
    private static AtlasWrapperHttpClient single_instance = null;

    private Logger logger;
    private String baseUrl;
    private String funcCode;

    private AtlasWrapperHttpClient(String baseUrl, String funcCode, Logger logger)
    {
        this.baseUrl = baseUrl;
        this.funcCode = funcCode;
        this.logger = logger;
    }

    public static AtlasWrapperHttpClient getInstance(String baseUrl, String funcCode, Logger logger)
    {
        if (single_instance == null)
            single_instance = new AtlasWrapperHttpClient(baseUrl, funcCode, logger);

        return single_instance;
    }
}