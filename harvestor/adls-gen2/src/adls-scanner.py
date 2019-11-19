# Databricks notebook source
import os
from azure.common.credentials import ServicePrincipalCredentials
from azure.mgmt.resource import ResourceManagementClient
from azure.mgmt.storage import StorageManagementClient
from azure.storage.blob import BlockBlobService
import json
import time
from datetime import datetime

# COMMAND ----------

# MAGIC %md
# MAGIC Get all the variables from cluster's environment variables. These variables can be pushed to cluster's environment variable either manually or via DevOps pipleline while provisioning the cluster 

# COMMAND ----------

# Get Environment varaibles
KeyVault_Scope = os.environ['KEYVAULT_SCOPE']
KeyVault_ADLSGen2_Access_Secret_Name = os.environ['KEYVAULT_ADLSGEN2_ACCESS_SECRET_NAME']
ADLSGen2_URL = os.environ['ADLSGEN2_URL']
#ADLSGen2_FileSystem = os.environ['ADLSGen2_FileSystem']
KeyVault_BlobStorage_Access_Secret_Name = os.environ['KEYVAULT_BLOBSTORAGE_ACCESS_SECRET_NAME']
BlobStorage_URL = os.environ['BLOBSTORAGE_URL']
BlobStorage_Output_Container = os.environ['BLOBSTORAGE_OUTPUT_CONTAINER']
Scan_Depth=os.environ['SCAN_DEPTH']
AAD_Client_Id=os.environ['AAD_CLIENT_ID']
KeyVault_Client_Secret_Secret_Name=os.environ['KEYVAULT_CLIENT_SECRET_SECRET_NAME']
ADLSGen2_Resource_Group=os.environ['ADLSGEN2_RESOURCE_GROUP']
ADLSGen2_Subscription_Id=os.environ['ADLSGEN2_SUBSCRIPTION_ID']
AAD_Tenant_Id=os.environ['AAD_TENANT_ID']

# COMMAND ----------

# MAGIC %md
# MAGIC Set spart configuration to access ADLS Gen2 file system. This python job using ADLS storage keys to access the file system from Spark.

# COMMAND ----------

spark.conf.set(
  "fs.azure.account.key."+ADLSGen2_URL,
  dbutils.secrets.get(scope = KeyVault_Scope, key =KeyVault_ADLSGen2_Access_Secret_Name ))

# COMMAND ----------

# MAGIC %md
# MAGIC Set spark configuration to access Blob storage to store the output JSON file

# COMMAND ----------

spark.conf.set(
  "fs.azure.account.key."+BlobStorage_URL,
 dbutils.secrets.get(scope = KeyVault_Scope, key = KeyVault_BlobStorage_Access_Secret_Name))

# COMMAND ----------

# MAGIC %md
# MAGIC Method to upload create file in Azure blob storage. <br>
# MAGIC Inputs : Content, file name

# COMMAND ----------

def upload_to_blob(content, file_name):
  try:    
    timenow = datetime.now()   
    file_name = file_name+str(timenow.strftime("-%m%d%Y-%H-%M-%S"))+".json"
    result = dbutils.fs.put("wasbs://"+BlobStorage_Output_Container+"@"+BlobStorage_URL+"/"+file_name,content,True)
    if result == True:
      print('Successfully created file: '+file_name)
    else:
      print("File creation failed")
  except Exception as e:
    print('Error occurred while creating blob', e)

# COMMAND ----------

# MAGIC %md
# MAGIC Method to make JSON in a needed format <br>
# MAGIC Input -  list <br>
# MAGIC Output - Json String

# COMMAND ----------

def makeoutputjson_storageaccount(name,subscriptionId,resourceGroupName,location,createTime,SKU):
  entity_json ={
    "entity_type_name":"azure_storage_account",
    "created_by":"harvester",
     "attributes":[
         {
             "attr_name":"qualifiedName",
             "attr_value":"",
             "is_entityref": False
         },
         {
            "attr_name":"name",
            "attr_value":name,
            "is_entityref": False
        },
        {
            "attr_name":"subscriptionId",
            "attr_value":subscriptionId,
            "is_entityref": False
        }, 
        {
            "attr_name":"resourceGroupName",
            "attr_value":resourceGroupName,
            "is_entityref": False
        },
        {
            "attr_name":"location",
            "attr_value":location,
            "is_entityref": False
        },
        {
            "attr_name":"createTime",
            "attr_value":int(float(createTime)),
            "is_entityref": False
        },
        {
            "attr_name":"accessTier",
            "attr_value":"Unknown",
            "is_entityref": False
        },
        {
            "attr_name":"SKU",
            "attr_value":SKU,
            "is_entityref": False
        },
        {
            "attr_name":"kind",
            "attr_value":"StorageV2",
            "is_entityref": False
        }
     ]
  }
  json_string= json.dumps(entity_json)
  return json_string

# COMMAND ----------

def makeoutputjson_filesystem(name):
  entity_json ={
      "entity_type_name": "azure_datalake_gen2_filesystem",
      "created_by": "harvester",
      "attributes": [{
          "attr_name": "qualifiedName",
          "attr_value": "",
          "is_entityref": False
       }, 
        {
          "attr_name": "name",
          "attr_value": name,
          "is_entityref": False
      }
      ]
    }
  json_string= json.dumps(entity_json)
  return json_string
  

# COMMAND ----------

def makeoutputjson_service(name):
  entity_json ={
      "entity_type_name": "azure_datalake_gen2_service",
      "created_by": "harvester",
      "attributes": [{
          "attr_name": "qualifiedName",
          "attr_value": "",
          "is_entityref": False
       }, 
        {
          "attr_name": "name",
          "attr_value": name,
          "is_entityref": False
      }
      ]
    }
  json_string= json.dumps(entity_json)
  return json_string
  

# COMMAND ----------

def make_output_json(entitylist):
  entity_final=[]
  for entity in entitylist:
    if entity.endswith('/'):
      entity=entity[0:len(entity)-1]      
    entity_json ={
      "entity_type_name": "azure_datalake_gen2_resource_set",
      "created_by": "harvester",
      "attributes": [{
          "attr_name": "qualifiedName",
          "attr_value": "",
          "is_entityref": False
       }, 
        {
          "attr_name": "name",
          "attr_value": entity,
          "is_entityref": False
      }
      ]
    }
    entity_final.append(entity_json)
  json_string= json.dumps(entity_final)
  return json_string

# COMMAND ----------

# MAGIC %md
# MAGIC Method to scan the ADLS Gen 2 file system folders recursively using databricks dbutils

# COMMAND ----------

def getpath(path, level, entitylist, root_path ):  
  files = dbutils.fs.ls(path)
  for file in files:    
    pathvalue = str(file.path)      
    pathvalue_string = pathvalue.split(root_path)      
    pathvalue_entity =pathvalue_string[-1]    
    entitylist.append(pathvalue_entity)
    if level <= int(Scan_Depth):
        newlevel= level+1        
        getpath(file.path,newlevel,entitylist,root_path)
  return entitylist

# COMMAND ----------

 
def scan_file_system(filesystem_name, account_url):  
  entitylist=[]
  startlevel =1
  try:    
    root_path = "abfss://"+filesystem_name+"@"+account_url+"/"
    entitylist = getpath(root_path,startlevel,entitylist,root_path)
  except:
    print('Error in scan file system')
  finally:
    return entitylist

# COMMAND ----------

# MAGIC %md
# MAGIC <b>Main method</b> <br>
# MAGIC This script is using Azure service principal to get access to the storage account properties <br>
# MAGIC Service principal client id is stored in environment variable and secrets are pulled from Azure KeyVault <br>
# MAGIC This script doesn't mount ADLS Gen 2 file system to databricks, instead it directly access the file system

# COMMAND ----------

subscription_id =  ADLSGen2_Subscription_Id
ad_client_id=AAD_Client_Id
ad_client_secret=dbutils.secrets.get(scope = KeyVault_Scope, key =KeyVault_Client_Secret_Secret_Name ) 
ad_tenantid=AAD_Tenant_Id
#
resource_group_name=ADLSGen2_Resource_Group
storage_account_name=ADLSGen2_URL[0:ADLSGen2_URL.find('.')]
#Make credential object
credentials = ServicePrincipalCredentials(client_id=ad_client_id, secret=ad_client_secret, tenant=ad_tenantid)
resource_client = ResourceManagementClient(credentials, subscription_id)
storage_client = StorageManagementClient(credentials, subscription_id)
storage_account = storage_client.storage_accounts.get_properties(resource_group_name, storage_account_name)
# Get properties of storage account
sa_creation_time=storage_account.creation_time.strftime("%Y-%m-%d %H:%M:%S")
sa_creation_time_test=str(storage_account.creation_time.timestamp())
sa_kind=storage_account.kind
sa_location=storage_account.location
sa_name=storage_account.name
sa_sku=storage_account.sku.name
# Go further only for Storage Gen 2
if sa_kind =='StorageV2':
  # make output json for storage account
  output_json_sa = makeoutputjson_storageaccount(sa_name,subscription_id,resource_group_name,sa_location,sa_creation_time_test,sa_sku)
  output_json_service=makeoutputjson_service(sa_name)
  #Get Storage Account file system properties
  storage_keys = storage_client.storage_accounts.list_keys(resource_group_name, storage_account_name)
  storage_keys = {v.key_name: v.value for v in storage_keys.keys}
  block_blob_service = BlockBlobService(account_name=storage_account_name, account_key=storage_keys['key1'])
  containers = block_blob_service.list_containers()
  filesystems=[]
  # Get file system names
  for container in containers:
      filesystems.append(container.name)
  for filesystem in filesystems:  
    # Pull the entity
    # make output json for file system
    output_json_fs= makeoutputjson_filesystem(filesystem)  
    entitylist= scan_file_system(filesystem,ADLSGen2_URL)
    if len(entitylist) >0:
      output_json= make_output_json(entitylist)
      output_json_filesystem={
        "azure_storage_account":json.loads(output_json_sa),
        "azure_datalake_gen2_service":json.loads(output_json_service),
        "azure_datalake_gen2_filesystem":json.loads(output_json_fs),
        "azure_datalake_gen2_resource_set":json.loads(output_json)
      }
      json_string_final= json.dumps(output_json_filesystem)
      output_filename=filesystem+"@"+ADLSGen2_URL
      upload_to_blob(json_string_final,output_filename)
print('Process completed')
