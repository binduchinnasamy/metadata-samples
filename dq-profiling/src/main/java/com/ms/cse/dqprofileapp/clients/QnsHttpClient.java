package com.ms.cse.dqprofileapp.clients;

import org.springframework.beans.factory.annotation.Value;

public class QnsHttpClient {
    private static QnsHttpClient single_instance = null; 
    
    @Value("${dqProfileApp.qns-svc.baseUrl}")
    private String qnsSvcUrl;
    @Value("${dqProfileApp.qns-svc.code}")
    private String qnsSvcCode;
    
    private QnsHttpClient() 
    {
        System.out.println("QNS Svc URL:" + qnsSvcUrl + "?code=" + qnsSvcCode);
    } 
    
    public static QnsHttpClient getInstance() 
    { 
        if (single_instance == null) 
            single_instance = new QnsHttpClient(); 
  
        return single_instance; 
    }
}