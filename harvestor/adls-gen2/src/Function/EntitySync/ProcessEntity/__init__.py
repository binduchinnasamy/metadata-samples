import logging
import requests
import json
import os
import azure.functions as func


def main(myblob: func.InputStream):
    logging.info(f"Python blob trigger function processed blob \n"
                 f"Name: {myblob.name}\n"
                 f"Blob Size: {myblob.length} bytes")
    blob_size=myblob.length
    json_generator_url = os.environ["json_generator_url"]
    api_access_layer_url = os.environ["api_access_layer_url"]

    if blob_size >0:             
        json_object= json.loads(myblob.read())        
        # call json generator service    
        return_json=httpPostJsonGenerator(json_object,json_generator_url)
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

def validatewithQNS(input_content, url):
    try:
        headers = {'Content-type': 'application/json'}
        response = requests.post(url,data=json.dumps(input_content),headers=headers)  
        # business logic
    except Exception as e:
        logging.error("error occured in calling QNS generator: "+ e)