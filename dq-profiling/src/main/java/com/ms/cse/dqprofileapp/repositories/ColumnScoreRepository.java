package com.ms.cse.dqprofileapp.repositories;

import com.ms.cse.dqprofileapp.models.ColumnScore;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface ColumnScoreRepository extends CrudRepository<ColumnScore, Integer> {
    List<ColumnScore> findByUpdateTimestampBetweenOrderByUpdateTimestampDesc(Timestamp start, Timestamp stop);
}