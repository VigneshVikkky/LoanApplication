package com.avitam.bankloanapplication.repository;

import com.avitam.bankloanapplication.model.entity.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends MongoRepository<Role, String> {

    Role findByRecordId(String recordId);

    void deleteByRecordId(String id);

    List<Role> findByStatusOrderByIdentifier(boolean b);
}
