package com.ms.cse.dqprofileapp.cloudfunctions;

import com.microsoft.azure.functions.ExecutionContext;
import com.ms.cse.dqprofileapp.extensions.TimestampExtension;
import com.ms.cse.dqprofileapp.models.ColumnScore;
import com.ms.cse.dqprofileapp.repositories.ColumnScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.sql.Timestamp;
import java.util.List;
import java.util.function.Function;

public class GetCurrentColumnScoresFunction {
    @Autowired
    private ColumnScoreRepository columnScoreRepository;

    @Bean
    public Function<Timestamp, List<ColumnScore>> getCurrentColumnScores() {
        return waterMarkDate -> {
            try {
                List<ColumnScore> entityScores1 = (List) columnScoreRepository.findAll();
            } catch (Exception e){
                System.out.println(e);
            }
            List<ColumnScore> columnScores = columnScoreRepository.findByUpdateTimestampBetweenOrderByUpdateTimestampDesc(waterMarkDate, TimestampExtension.now());
            return columnScores;
        };
    }
}
