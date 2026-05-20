package com.qianyu.module.geo.service.article;

import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.framework.common.util.object.BeanUtils;
import com.qianyu.module.geo.controller.admin.article.vo.GeoArticlePageReqVO;
import com.qianyu.module.geo.controller.admin.article.vo.GeoArticleSaveReqVO;
import com.qianyu.module.geo.dal.dataobject.article.GeoArticleDO;
import com.qianyu.module.geo.dal.mysql.article.GeoArticleMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

/**
 * 创作文章 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class GeoArticleServiceImpl implements GeoArticleService {

    @Resource
    private GeoArticleMapper articleMapper;

    @Override
    public Long createArticle(GeoArticleSaveReqVO createReqVO) {
        // 插入
        GeoArticleDO article = BeanUtils.toBean(createReqVO, GeoArticleDO.class);
        articleMapper.insert(article);

        // 返回
        return article.getId();
    }

    @Override
    public void updateArticle(GeoArticleSaveReqVO updateReqVO) {
        // 更新
        GeoArticleDO updateObj = BeanUtils.toBean(updateReqVO, GeoArticleDO.class);
        articleMapper.updateById(updateObj);
    }

    @Override
    public void deleteArticle(Long id) {
        // 删除
        articleMapper.deleteById(id);
    }

    @Override
        public void deleteArticleListByIds(List<Long> ids) {
        // 删除
        articleMapper.deleteByIds(ids);
        }


/*    private void validateArticleExists(Long id) {
        if (articleMapper.selectById(id) == null) {
            throw exception(ARTICLE_NOT_EXISTS);
        }
    }*/

    @Override
    public GeoArticleDO getArticle(Long id) {
        return articleMapper.selectById(id);
    }

    @Override
    public PageResult<GeoArticleDO> getArticlePage(GeoArticlePageReqVO pageReqVO) {
        return articleMapper.selectPage(pageReqVO);
    }

}