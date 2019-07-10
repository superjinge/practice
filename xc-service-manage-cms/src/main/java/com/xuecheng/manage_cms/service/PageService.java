package com.xuecheng.manage_cms.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
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

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CmsTemplateRepository templateRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

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
            //更新dataurl
            one.setDataUrl(cmsPage.getDataUrl());
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

    //页面静态化方法
    public String getPageHtml(String pageId) {


        //2.远程请求获取数据模型
        Map model = getModelByPageId(pageId);
        if (model == null) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }

        //3.获取页面模板信息
        String templateModel = getTemplateByPageId(pageId);
        if (StringUtils.isEmpty(templateModel)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }

        //4.执行页面静态化
        String html = generateHtml(templateModel, model);

        if (StringUtils.isEmpty(html)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        return html;
    }

    /**
     * //4.执行页面静态化
     *
     * @param templateContent
     * @param model
     * @return
     */
    private String generateHtml(String templateContent, Map model) {
        try {
            //创建配置对象
            Configuration configuration = new Configuration(Configuration.getVersion());
            //创建模板加载器
            StringTemplateLoader templateLoader = new StringTemplateLoader();
            templateLoader.putTemplate("template", templateContent);
            //向configuration配置模板加载器
            configuration.setTemplateLoader(templateLoader);

            //h获取模板
            Template template = configuration.getTemplate("template");

            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getTemplateByPageId(String pageId) {
        //1.静态化程序获取页面url
        //取出页面信息
        CmsPage cmsPage = findById(pageId).getCmsPage();

        //获取页面模板id
        String templateId = cmsPage.getTemplateId();
        if (StringUtils.isEmpty(templateId)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //查询模板信息
        Optional<CmsTemplate> template = templateRepository.findById(templateId);
        if (template.isPresent()) {
            CmsTemplate cmsTemplate = template.get();
            //获取模板文件ID
            String templateFileId = cmsTemplate.getTemplateFileId();
            //从gridFs取模板文件

            //根据文件id取文件

            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is("5d25ea949754d8390cc7e28f")));

            //打开下载流对象
            GridFSDownloadStream gridFSDownloadStream =
                    gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            //创建gridFsResource，用于获取流对象
            GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
            //获取流中的数据
            try {
                String content = IOUtils.toString(gridFsResource.getInputStream(), "GBK");
                return content;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private Map getModelByPageId(String pageId) {
        //1.静态化程序获取页面url
        //取出页面信息
        CmsPage cmsPage = findById(pageId).getCmsPage();
        //取出页面dataurl
        String dataUrl = cmsPage.getDataUrl();
        if (StringUtils.isEmpty(dataUrl)) {
            //页面不存在
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }

        //通过restTemplate 请求dataurlh获取数据
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        return forEntity.getBody();
    }
}
