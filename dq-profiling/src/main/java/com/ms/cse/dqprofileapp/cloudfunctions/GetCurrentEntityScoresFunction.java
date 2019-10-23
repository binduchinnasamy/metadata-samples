package com.ms.cse.dqprofileapp.cloudfunctions;

import com.ms.cse.dqprofileapp.extensions.TimestampExtension;
import com.ms.cse.dqprofileapp.models.EntityScore;
import com.ms.cse.dqprofileapp.repositories.EntityScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;
import java.util.function.Function;

@Component
public class GetCurrentEntityScoresFunction {
    @Autowired
    private EntityScoreRepository entityScoreRepository;

    @Bean
    public Function<Timestamp, List<EntityScore>> getCurrentEntityScores() {
        return waterMarkDate -> {
            try {
                List<EntityScore> entityScores1 = (List) entityScoreRepository.findAll();
            } catch (Exception e){
                System.out.println(e);
            }
            List<EntityScore> entityScores = entityScoreRepository.findByUpdateTimestampBetweenOrderByUpdateTimestampDesc(waterMarkDate, TimestampExtension.now());
            return entityScores;
        };
    }
}
