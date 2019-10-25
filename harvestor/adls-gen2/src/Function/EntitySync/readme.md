#### The following environment variable must present for this function to work correctly

1. json_generator_url - URL of JSON Generator service
2. api_access_layer_url - URL of API Access layer
3. qns_url - URL of QNS Service
4. trigger_STORAGE - Blob storage connection string
5. AzureWebJobsStorage - Storage account connection string for Azure function to use internally 

#### function.json parameters
Ensure the following varialbes in function.json correctly set 

1. "path": [Container Name in storage account where the files will be placed, so that this function can be triggered ]   
2. "connection": [Name of the storage account connection string in App Settings or Docker ENV varaible]

#### host.json parameters
As this function will be running in ASE environment and longer duration at times, ensure to set unlimited execution timeout in host.json

 "functionTimeout": "-1",