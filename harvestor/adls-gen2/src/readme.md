### ADLS Gen 2 Scanner

Scanning ADLS Gen 2 folder strucutre is done using databricks job. 

#### Source - ADLS Gen 2 file system
#### Output location - Blob storage 

Access to both ADLS Gen 2 and Blob storage is utilized 

This python notebook needs the following values in the **Spark Cluster's environment variables**
  
1. ADLSGEN2_RESOURCE_GROUP=[ADLS Gen 2 Resource Group name]
2. SCAN_DEPTH=3
3. BLOBSTORAGE_OUTPUT_CONTAINER=[Name of the storage account container where the output should made]
4. KEYVAULT_ADLSGEN2_ACCESS_SECRET_NAME=[Secret name in KeyVault]
5. PYSPARK_PYTHON=/databricks/python3/bin/python3
6. ADLSGEN2_SUBSCRIPTION_ID=[Subscription ID]
7. KEYVAULT_BLOBSTORAGE_ACCESS_SECRET_NAME=[Secret name in keyvault]
8. BLOBSTORAGE_URL=[Blob Storage URL - example - accountname.blob.core.windows.net]
9. KEYVAULT_CLIENT_SECRET_SECRET_NAME=[Serice Principle Sceret name in KeyVault]
10. AAD_CLIENT_ID=[AAD Client ID of the service principal]
11. AAD_TENANT_ID=[Tenant ID]
12. KEYVAULT_SCOPE=[KeyVault scope that is managed by databricks]
13. ADLSGEN2_URL=[URL of ADS Gen 2 account - example accountname.dfs.core.windows.net]

#### PyPi libraries 

The following Python libraries needs to be installed in the cluster, the code is tested with the following Python package and 

1. azure-common
2. azure-mgmt-resource==5.1.0
3. azure-mgmt-storage==5.0.0
4. azure-storage-blob==2.1.0
