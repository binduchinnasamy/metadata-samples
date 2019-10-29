# metadata-samples

#### The following environment variables needed for this component to work as expected.

1. DQPROFILEAPP_MSSQL_USERNAME: username for connecting to Azure SQL DB, example value “nischays@nischaysignitetest”
2. DQPROFILEAPP_MSSQL_PASSWORD: password for connecting to Azure SQL DB (this should be the KeyVault reference)
3. DQPROFILEAPP_MSSQL_URL: url for connecting to Azure SQL DB, example value “jdbc:sqlserver://nischaysignitetest.database.windows.net:1433;database=ignitetest”
4. DQPROFILEAPP_QNS-SVC_BASEURL: url for connecting to QNS
5. DQPROFILEAPP_QNS-SVC_CODE: function auth code for connecting to QNS
6. DQPROFILEAPP_JSONCREATOR-SVC_BASEURL: url for connecting to JSONCreator
7. DQPROFILEAPP_JSONCREATOR-SVC_CODE: function auth code for connecting to JSONCreator
8. DQPROFILEAPP_ATLASWRAPPER-SVC_BASEURL: url for connecting to Atlas Wrapper
9. DQPROFILEAPP_UPSERTDQRULES_QUERYALL: flag that I have added for testing purposes, this by passes the date range query and queries every single entry from the table DQ_RULESINFO, currently the default value in application.yml is set as true
10. DQPROFILEAPP_UPDATECOLUMNSCORES_QUERYALL: flag that I have added for testing purposes, this by passes the date range query and queries every single entry from the view VW_COLUMNSCORE, currently the default value in application.yml is set as true
