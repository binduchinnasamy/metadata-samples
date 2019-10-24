import logging
import requests
import json
import os
import azure.functions as func


def main(blob: func.InputStream):
    logging.info("Python blob trigger function processed blob")                
    blob_size=blob.length

    json_generator_url = os.environ["json_generator_url"]
    api_access_layer_url = os.environ["api_access_layer_url"]
    qns_service_url=os.environ["qns_url"]

    location_of_at=blob.name.find('@')
    location_of_root=blob.name.find('/')
    filesystem_name=blob.name[location_of_root+1:location_of_at]
    adls_gen2_url="https://"+blob.name[location_of_at+1:blob.name.find('.')]+".dfs.core.windows.net"

    guids ={
        "azure_storage_account":-100,
        "azure_datalake_gen2_service":-101,
        "azure_datalake_gen2_filesystem": -102
    }

    if blob_size >0: 

        json_object= json.loads(blob.read())

        storage_ac_json = json_object["azure_storage_account"]
        service_json=json_object["azure_datalake_gen2_service"]
        filesystem_json = json_object["azure_datalake_gen2_filesystem"]
        resource_set_json = json_object["azure_datalake_gen2_resource_set"]
        
        final_list=[]
        if resource_set_json and filesystem_json and resource_set_json:            
                      
            storageac_json_qns=validatewithQNS(storage_ac_json,qns_service_url,adls_gen2_url,filesystem_name,'storageac')
            service_json_qns=validatewithQNS(service_json,qns_service_url,adls_gen2_url,filesystem_name,'service')
            filesystem_json_qns=validatewithQNS(filesystem_json,qns_service_url,adls_gen2_url,filesystem_name,'filesystem')
            resource_set_json_qns=validatewithQNS(resource_set_json,qns_service_url,adls_gen2_url,filesystem_name,'resourceset')
            
            #guidvalue=storageac_json_qns["attributes"]
            if storageac_json_qns["guid"]:
                guids['azure_storage_account']=storageac_json_qns["guid"]
            if service_json_qns["guid"]:
                guids['azure_datalake_gen2_service']=service_json_qns["guid"]
            if filesystem_json_qns['guid']:
                guids['azure_datalake_gen2_filesystem']=filesystem_json_qns["guid"]
            
            #final_list.append(resource_set_json_qns)
            # Add Resource Set -1            
            for entity in resource_set_json_qns['entity']:
                resourceset_link_object={
                    "attr_name":"filesystem",
                    "attr_value": {
                        "guid":guids['azure_datalake_gen2_filesystem'],
                        "typeName":"azure_datalake_gen2_filesystem",
                        "optionalAttributes":{}
                    }
                }
                entity['entity']['attributes'].append(resourceset_link_object)
                final_list.append(entity['entity'])
            # Add file system # 2
            filesystem_link_object={
                    "attr_name":"datalakeService",
                    "attr_value": {
                        "guid":guids['azure_datalake_gen2_service'],
                        "typeName":"azure_datalake_gen2_service",
                        "optionalAttributes":{}
                    }
                }
            filesystem_json_qns['entity']['attributes'].append(filesystem_link_object)
            final_list.append(filesystem_json_qns['entity'])
            # Add Service
            service_link_object={
                "attr_name":"account",
                "attr_value":{
                    "guid":guids['azure_storage_account'],
                    "typeName":"azure_storage_account",
                    "optionalAttributes":{}
                }
            }
            service_json_qns['entity']['attributes'].append(service_link_object)
            final_list.append(service_json_qns['entity'])
            # Add Account            
            final_list.append(storageac_json_qns['entity'])            
            # call json generator service
            test_json=json.dumps(final_list)    
            return_json=httpPostJsonGenerator(final_list,json_generator_url)
            if return_json.status_code == 200:
                json_entity_objects=json.loads(return_json.text)
                # call Json api access service
                filteredvalue = json_entity_objects["entities"]
                api_output = httpPostJsonGenerator(json_entity_objects,api_access_layer_url)
                if api_output.status_code ==200:
                    logging.info("Entity created successfully!")
                else:
                    logging.error("Error in calling meta access layer with error code:"+api_output.status_code + " "+api_output.text)
        else:
            logging.error("input request json is empty")   

def httpPostJsonGenerator(input_content, url):    
    test_json=json.dumps(input_content)
    try:        
        headers = {'Content-type': 'application/json'}
        response = requests.post(url,data=json.dumps(input_content),headers=headers)              
        return response
    except Exception as e:        
        logging.error("error occured in calling json generator: "+e)
    except requests.RequestException as RequestException:
        logging.error("Request Exception "+ RequestException)

def validatewithQNS(input_content, qns_service_url, adls_gen2_uri, file_system, entity_type):    
    if entity_type=='storageac':
        entity_with_qns=process_entity(input_content,adls_gen2_uri,file_system,qns_service_url, entity_type)
        return entity_with_qns
    if entity_type=='filesystem':
        entity_with_qns=process_entity(input_content,adls_gen2_uri,file_system,qns_service_url, entity_type)
        return entity_with_qns
    if entity_type=='service':
        entity_with_qns=process_entity(input_content,adls_gen2_uri,file_system,qns_service_url, entity_type)
        return entity_with_qns
    elif entity_type=='resourceset':
        entities_with_qns=[]
        for entity in input_content:
            entity_with_qns=process_entity(entity,adls_gen2_uri,file_system,qns_service_url, entity_type)
            entities_with_qns.append(entity_with_qns)       
        #return entities_with_qns
        return {'entity':entities_with_qns,'guid':''}

def process_entity(entity,adls_gen2_uri,file_system,qns_service_url, entity_type):
    entity_path=entity['attributes'][1]['attr_value']
    response=getQualifiedName(adls_gen2_uri,file_system,entity_path,qns_service_url,entity_type)
    if response.status_code == 200:
        response_json=json.loads(response.text)
        guid=""        
        if response_json['isExists']==False:
            entity['attributes'][0]['attr_value'] =response_json['qualifiedName']
        elif response_json['isExists']==True:
            entity['attributes'][0]['attr_value'] =response_json['qualifiedName']
            guid_object ={
                "guid":response_json['guid']
            }
            #entity['attributes'].append(guid_object)
            entity['guid']=response_json['guid']
            guid=response_json['guid']       
        return {'entity':entity,'guid':guid}
    else:
        return None 
def getQualifiedName(adls_gen2_uri, file_system, entity_path, qns_service_url,entity_type):
    qns_request_obj ={}
    if entity_type =='resourceset':
        qns_request_obj ={
        "azure_storage_uri":adls_gen2_uri,
        "filesystem_name":file_system,
        "resource_set_path":entity_path
        }
        qns_service_url=qns_service_url+'&typeName=azure_datalake_gen2_resource_set'
    elif entity_type =='storageac':
        qns_request_obj ={
        "uri":adls_gen2_uri
        }
        qns_service_url=qns_service_url+'&typeName=azure_storage_account'
    elif entity_type =='service':
        qns_request_obj ={
        "uri":adls_gen2_uri
        }
        qns_service_url=qns_service_url+'&typeName=azure_datalake_gen2_service'
    elif entity_type =='filesystem':
        qns_request_obj ={
        "azure_storage_uri":adls_gen2_uri,
        "filesystem_name":file_system        
        }
        qns_service_url=qns_service_url+'&typeName=azure_datalake_gen2_filesystem'
    headers = {'Content-type': 'application/json'}
    response=requests.post(qns_service_url,data=json.dumps(qns_request_obj),headers=headers)                  
    return response

    