package com.luanvan.identity.repository;

import com.luanvan.identity.entity.StudentAccount;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface StudentAccountRepository extends MongoRepository<StudentAccount, String> {
    Optional<StudentAccount> findByMssv(String mssv); // Tìm user theo MSSV (Mã số sinh viên)
    Boolean existsByMssv(String mssv);
    List<StudentAccount> findAll();
}
