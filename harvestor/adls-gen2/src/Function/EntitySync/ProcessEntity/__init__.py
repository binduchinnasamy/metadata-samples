import logging
import requests
import json
import os
import azure.functions as func


def main(blob: func.InputStream):
    logging.info(f"Python blob trigger function processed blob \n"
                 f"Name: {blob.name}\n"
                 f"Blob Size: {blob.length} bytes")
    blob_size=blob.length
    json_generator_url = os.environ["json_generator_url"]
    api_access_layer_url = os.environ["api_access_layer_url"]
    qns_service_url=""

    location_of_at=blob.name.find('@')
    location_of_root=blob.name.find('/')
    filesystem_name=blob.name[location_of_root+1:location_of_at]
    adls_gen2_url=blob.name[location_of_at+1:blob.name.find('.')]+".dfs.core.windows.net"

    if blob_size >0:             
        json_object= json.loads(blob.read())
        json_with_qns=validatewithQNS(json_object,qns_service_url,adls_gen2_url,filesystem_name)        
        # call json generator service    
        return_json=httpPostJsonGenerator(json_with_qns,json_generator_url)
        if return_json.status_code == 200:
            json_entity_objects=json.loads(return_json.text)
            # call Json api access service
            api_output = httpPostJsonGenerator(json_entity_objects,api_access_layer_url)
            if api_output.status_code ==200:
                logging.info("Entity created successfully!")
            else:
                logging.error("Error in calling meta access layer with error code:"+api_output.status_code + 
                " "+api_output.text)
    else:
        logging.error("input request json is empty")   

def httpPostJsonGenerator(input_content, url):    
    try:        
        headers = {'Content-type': 'application/json'}
        response = requests.post(url,data=json.dumps(input_content),headers=headers)              
        return response
    except Exception as e:        
        logging.error("error occured in calling json generator: "+e)
    except requests.RequestException as RequestException:
        logging.error("Request Exception "+ RequestException)

def validatewithQNS(input_content, qns_service_url, adls_gen2_uri, file_system):
    for entity in input_content:
        entity_path=entity['attributes'][0]['attr_value']
        qualified_name=getQualifiedName(adls_gen2_uri,file_system,entity_path,qns_service_url)
        entity['attributes'][0]['attr_name']=qualified_name
    return input_content

def getQualifiedName(adls_gen2_uri, file_system, entity_path, qns_service_url):
    qns_request_obj ={
        "azure_storage_uri":adls_gen2_uri,
        "filesystem_name":file_system,
        "resource_set_path":entity_path
    }
    headers = {'Content-type': 'application/json'}
    #response=requests.post(qns_service_url,data=qns_request_obj,headers=headers)
    #if response.status_code==200:
    #    response_text = response.text
    return "qualified_name"

    