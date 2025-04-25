package com.luanvan.identity.repository;

import com.luanvan.identity.entity.ActiveSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActiveSessionRepository extends MongoRepository<ActiveSession, String> {
    void deleteByMssv(String mssv);
}
