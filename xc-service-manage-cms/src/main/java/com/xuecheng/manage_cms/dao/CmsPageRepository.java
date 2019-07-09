package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author : superjinge
 * @date : 2019/06/25
 */
public interface CmsPageRepository extends MongoRepository<CmsPage, String> {

    /**
     * 根据页面名称查询
     *
     * @param pageName
     * @return
     */
    CmsPage findByPageName(String pageName);

    /**
     * 根据页面名称和类型查询
     *
     * @param pageName
     * @param pageType
     * @return
     */
    CmsPage findByPageNameAndPageType(String pageName, String pageType);

    /**
     * 根据站点和页面类型查询记录数
     *
     * @param siteId
     * @param pageType
     * @return
     */
    int countBySiteIdAndPageType(String siteId, String pageType);

    /**
     * 根据站点和页面类型分页查询
     *
     * @param siteId
     * @param pageType
     * @param pageable
     * @return
     */
    Page<CmsPage> findBySiteIdAndPageType(String siteId, String pageType, Pageable pageable);


    /**
     * 根据站点ID,页面名称,页面webPath查看页面是否存在
     *
     * @param siteId
     * @param pageName
     * @param pageWebPath
     * @return
     */
    CmsPage findBySiteIdAndPageNameAndPageWebPath(String siteId, String pageName, String pageWebPath);
}
