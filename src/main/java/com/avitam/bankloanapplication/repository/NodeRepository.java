package com.avitam.bankloanapplication.repository;

import com.avitam.bankloanapplication.model.entity.Node;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("NodeRepository")
public interface NodeRepository extends MongoRepository<Node, String> {
    List<Node> findByParentNode(Node parentNode);

    List<Node> findByStatusOrderByDisplayPriority(Boolean status);

    Node findByRecordId(String nodeId);

    void deleteByRecordId(String recordId);

    List<Node> findByParentNodeId(String node);
}
