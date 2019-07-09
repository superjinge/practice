package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author : superjinge
 * @date : 2019/06/25
 */
@Service
@Slf4j
public class PageService {
    @Autowired
    private CmsPageRepository cmsPageRepository;

    /**
     * 页面列表分页查询
     *
     * @param page           当前页码
     * @param size           页面显示个数
     * @param queryPageParam 查询条件
     * @return 页面列表
     */
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageParam) {
        if (queryPageParam == null) {
            queryPageParam = new QueryPageRequest();
        }

        //增加自定义条件查询  => 条件匹配器
        //根据页面名称 来模糊查询 => 需要自定义字符串的匹配器实现模糊查询
        ExampleMatcher exampleMatcher = ExampleMatcher.matching().
                withMatcher("pageAlies", ExampleMatcher.GenericPropertyMatchers.contains());
        //条件值对象 => 精确查询
        CmsPage cmsPage = new CmsPage();
        // 站点ID
        if (StringUtils.isNotEmpty(queryPageParam.getSiteId())) {
            cmsPage.setSiteId(queryPageParam.getSiteId());
        }
        // 站点别名
        if (StringUtils.isNotEmpty(queryPageParam.getPageAliase())) {
            cmsPage.setPageAliase(queryPageParam.getPageAliase());
        }

        //创建条件实例
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);

        if (page <= 0) {
            page = 1;
        }
        //为了适应mongodb的接口将页码减1
        page = page - 1;
        if (size <= 0) {
            size = 20;
        }
        //分页对象
        Pageable pageable = new PageRequest(page, size);
        //分页查询
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
        QueryResult<CmsPage> cmsPageQueryResult = new QueryResult<CmsPage>();
        cmsPageQueryResult.setList(all.getContent());
        cmsPageQueryResult.setTotal(all.getTotalElements());
        //返回结果
        return new QueryResponseResult(CommonCode.SUCCESS, cmsPageQueryResult);
    }

    /**
     * 新增页面
     *
     * @param cmsPage
     * @return
     */
    public CmsPageResult add(CmsPage cmsPage) {
        //1.首先,校验页面是否存在 : 站点ID ,页面名称,页面webPath
        CmsPage cmsPageData = cmsPageRepository.findBySiteIdAndPageNameAndPageWebPath(cmsPage.getSiteId(),
                cmsPage.getPageName(), cmsPage.getPageWebPath());

        if (null != cmsPageData) {
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }

        //添加页面主键由spring data 自动生成
        cmsPage.setPageId(null);
        cmsPageRepository.save(cmsPage);
        //返回结果
        return new CmsPageResult(CommonCode.SUCCESS, cmsPage);
    }

    /**
     * 根据Id查询页面
     *
     * @param id
     * @return
     */
    public CmsPageResult findById(String id) {
        Optional<CmsPage> findResult = cmsPageRepository.findById(id);
        if (findResult.isPresent()) {
            return new CmsPageResult(CommonCode.SUCCESS, findResult.get());
        } else {
            log.error("id为{}的记录不存在", id);
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOT_FOUNT);
        }
        return null;
    }

    /**
     * 更新页面信息
     *
     * @param id
     * @param cmsPage
     * @return
     */
    public CmsPageResult updateCmsPage(String id, CmsPage cmsPage) {
        CmsPageResult byId = this.findById(id);
        //1.首先查询是否存在
        Optional<CmsPage> findResult = cmsPageRepository.findById(id);
        if (findResult.isPresent()) {
            CmsPage one = findResult.get();
            one.setTemplateId(cmsPage.getTemplateId());
            //更新所属站点
            one.setSiteId(cmsPage.getSiteId());
            //更新页面别名
            one.setPageAliase(cmsPage.getPageAliase());
            //更新页面名称
            one.setPageName(cmsPage.getPageName());
            //更新访问路径
            one.setPageWebPath(cmsPage.getPageWebPath());
            //更新物理路径
            one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            //执行更新
            CmsPage save = cmsPageRepository.save(one);
            if (save != null) {
                //返回成功
                CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, save);
                return cmsPageResult;
            }

        }
        log.error("id为{}的记录不存在", id);
        return new CmsPageResult(CommonCode.FAIL, null);
    }

    /**
     * 删除页面
     *
     * @param id
     * @return
     */
    public ResponseResult deleteCmsPage(String id) {
        Optional<CmsPage> byId = cmsPageRepository.findById(id);
        if (byId.isPresent()) {
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        log.error("id为{}的记录不存在", id);
        return new ResponseResult(CommonCode.FAIL);
    }
}
