package com.puzzlelog.api.repository.mongo;

import com.puzzlelog.api.dao.document.Asset;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface AssetRepository extends MongoRepository<Asset, String> {

    List<Asset> findByDeletedFalse();

    List<Asset> findByTypeAndDeletedFalse(String type);

    Optional<Asset> findByIdAndDeletedFalse(String id);
}