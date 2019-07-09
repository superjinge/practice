package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.CustomException;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author : superjinge
 * @date : 2019/07/03
 */
@Service
public class CmsConfigService {
    @Autowired
    private CmsConfigRepository cmsConfigRepository;


    public CmsConfig getModel(String id) {
        Optional<CmsConfig> byId = cmsConfigRepository.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        }
        throw new CustomException(CmsCode.CMS_PAGE_NOT_FOUNT);
    }
}
