package com.ms.cse.dqprofileapp.repositories;

import com.ms.cse.dqprofileapp.models.RulesInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface RulesInfoRepository extends CrudRepository<RulesInfo, Integer> {
    List<RulesInfo> findByUpdateTimestampBetweenOrderByUpdateTimestampDesc(Timestamp start, Timestamp stop);
}