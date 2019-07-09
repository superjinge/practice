package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author : superjinge
 * @date : 2019/07/03
 */
public interface CmsConfigRepository extends MongoRepository<CmsConfig, String> {
}
