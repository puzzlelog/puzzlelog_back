package com.puzzlelog.api.repository.mongo;

import com.puzzlelog.api.dao.document.Asset;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface AssetRepository extends MongoRepository<Asset, String> {

    List<Asset> findByDeletedFalse();

    List<Asset> findByTypeAndDeletedFalse(String type);

    Optional<Asset> findByIdAndDeletedFalse(String id);
    
    List<Asset> findByTagsInAndDeletedFalse(List<String> tags);

    // 특정 태그로 하나라도 일치하는 에셋 검색
    List<Asset> findByTagsContainingAndDeletedFalse(String tag);
    
    
    //어셋 type STICKER 잠금
    List<Asset> findByTagsContainingAndTypeAndDeletedFalse(String type);

}