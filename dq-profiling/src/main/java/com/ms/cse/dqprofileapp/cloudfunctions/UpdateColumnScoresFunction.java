package com.ms.cse.dqprofileapp.cloudfunctions;

import com.ms.cse.dqprofileapp.clients.AtlasWrapperHttpClient;
import com.ms.cse.dqprofileapp.extensions.TimestampExtension;
import com.ms.cse.dqprofileapp.models.ColumnScore;
import com.ms.cse.dqprofileapp.models.FunctionInput;
import com.ms.cse.dqprofileapp.repositories.ColumnScoreRepository;
import kong.unirest.JsonNode;
import kong.unirest.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;
import java.util.function.Function;

@Component
public class UpdateColumnScoresFunction {
    @Autowired
    private ColumnScoreRepository columnScoreRepository;

    @Value("${dqProfileApp.atlaswrapper-svc.baseUrl}")
    private String atlasWrapperSvcUrl;

    @Value("${dqProfileApp.updateColumnScores.queryAll}")
    private boolean queryAll;

    @Bean
    public Function<FunctionInput, Integer> updateColumnScores() {
        return input -> {
            List<ColumnScore> columnScores = queryAll ?
                    (List)columnScoreRepository.findAll() :
                    columnScoreRepository.findByUpdateTimestampBetweenOrderByUpdateTimestampDesc(input.getTimeStamp(), TimestampExtension.now());
            try {
                //call atlas to find the column entity
                AtlasWrapperHttpClient atlasWrapperClient = AtlasWrapperHttpClient.getInstance(this.atlasWrapperSvcUrl, input.getExecutionContext().getLogger());
                for(ColumnScore columnScore: columnScores) {
                    String columnFqdn = columnScore.getColumnFqdn();
                    String searchCriteria = "column+where+qualifiedName=" + columnFqdn;
                    JsonNode searchResult = atlasWrapperClient.search(searchCriteria);
                    for (Object obj : searchResult.getObject().getJSONArray("entities")) {
                        JSONObject entity = (JSONObject)obj;
                        String typeName = entity.getString("typeName");
                        String qualifiedName = entity.getJSONObject("attributes").getString("qualifiedName");
                        if (typeName.equalsIgnoreCase("column") && qualifiedName.equalsIgnoreCase(columnFqdn)) {
                            String entityId = entity.getString("guid");

                            //get atlas entity using its guid
                            JsonNode colEntity = atlasWrapperClient.getEntity(entityId);

                            colEntity = PrepareColumnWithUpdatedAttributes(colEntity, columnScore);
                            atlasWrapperClient.upsertEntity(colEntity);
                        }
                    }
                }
            } catch (Exception e){
                input.getExecutionContext().getLogger().info(e.toString());
            }
            return 0;
        };
    }

    private JsonNode PrepareColumnWithUpdatedAttributes(JsonNode colEntity, ColumnScore columnScore){
        JSONObject attributes = colEntity.getObject().getJSONObject("entity").getJSONObject("attributes");

        attributes.remove("last_update_time");
        attributes.put("last_update_time", columnScore.getUpdateTimestamp().getTime());
        attributes.remove("total_rows_failed");
        attributes.put("total_rows_failed", columnScore.getRowsFailed());
        attributes.remove("total_rows_passed");
        attributes.put("total_rows_passed", columnScore.getRowsPassed());

        colEntity.getObject().getJSONObject("entity").remove("attributes");
        colEntity.getObject().getJSONObject("entity").put("attributes", attributes);
        return colEntity;
    }
}
