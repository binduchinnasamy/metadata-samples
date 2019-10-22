### ADLS Gen 2 Scanner

Scanning ADLS Gen 2 folder strucutre is done using databricks job. 

#### Source - ADLS Gen 2 file system
#### Output location - Blob storage 

Access to both ADLS Gen 2 and Blob storage is utilized 

This python notebook needs the following values in the **Spark Cluster's environment variables**
  
1. ADLSGen2_Resource_Group='ADLS Gen 2 Resource Group location'
2. Scan_Depth=3
3. BlobStorage_Output=[Name of the storage account container where the output should made]
4. KeyVault_ADLSGen2_Access_Secret_Name=[Secret name in KeyVault]
5. PYSPARK_PYTHON=/databricks/python3/bin/python3
6. ADLSGen2_Subscription_Id=[Subscription ID]
7. KeyVault_BlobStorage_Access_Secret_Name=[Secret name in keyvault]
8. BlobStorage_URL=[Blob Storage URL - example - accountname.blob.core.windows.net]
9. KeyVault_Client_Secret_Secret_Name=[Serice Principle Sceret name in KeyVault]
10. AAD_Client_Id=[AAD Client ID of the service principal]
11. AAD_Tenant_Id=[Tenant ID]
12. Azure_KeyVault_Scope=[KeyVault scope that is managed by databricks]
13. ADLSGen2_URL=[URL of ADS Gen 2 account - example accountname.dfs.core.windows.net]


