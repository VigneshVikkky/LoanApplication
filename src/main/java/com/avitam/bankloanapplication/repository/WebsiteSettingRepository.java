package com.avitam.bankloanapplication.repository;


import com.avitam.bankloanapplication.model.entity.WebsiteSetting;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository("WebsiteSettingRepository")
public interface WebsiteSettingRepository extends MongoRepository<WebsiteSetting, String> {
    Object findByStatusOrderById(boolean b);


    void deleteByRecordId(String recordId);

    WebsiteSetting findByRecordId(String recordId);
}
