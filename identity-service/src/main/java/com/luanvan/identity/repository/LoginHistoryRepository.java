package com.luanvan.identity.repository;

import com.luanvan.identity.entity.LoginHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginHistoryRepository extends MongoRepository<LoginHistory, String> {
    List<LoginHistory> findByMssvOrderByLoginTimeDesc(String mssv);
    LoginHistory findFirstByMssvOrderByLoginTimeDesc(String mssv);
}
