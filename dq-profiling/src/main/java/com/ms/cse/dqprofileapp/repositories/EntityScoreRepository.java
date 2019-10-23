package com.ms.cse.dqprofileapp.repositories;

import com.ms.cse.dqprofileapp.models.EntityScore;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface EntityScoreRepository extends CrudRepository<EntityScore, Integer> {
    List<EntityScore> findByUpdateTimestampBetweenOrderByUpdateTimestampDesc(Timestamp start, Timestamp stop);
}
