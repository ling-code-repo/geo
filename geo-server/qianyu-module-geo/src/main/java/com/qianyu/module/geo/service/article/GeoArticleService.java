package com.qianyu.module.geo.service.article;

import java.util.*;
import javax.validation.*;
import com.qianyu.module.geo.controller.admin.article.vo.*;
import com.qianyu.module.geo.dal.dataobject.article.GeoArticleDO;
import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.framework.common.pojo.PageParam;

/**
 * 创作文章 Service 接口
 *
 * @author 系统管理员
 */
public interface GeoArticleService {

    /**
     * 创建创作文章
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createArticle(@Valid GeoArticleSaveReqVO createReqVO);

    /**
     * 更新创作文章
     *
     * @param updateReqVO 更新信息
     */
    void updateArticle(@Valid GeoArticleSaveReqVO updateReqVO);

    /**
     * 删除创作文章
     *
     * @param id 编号
     */
    void deleteArticle(Long id);

    /**
    * 批量删除创作文章
    *
    * @param ids 编号
    */
    void deleteArticleListByIds(List<Long> ids);

    /**
     * 获得创作文章
     *
     * @param id 编号
     * @return 创作文章
     */
    GeoArticleDO getArticle(Long id);

    /**
     * 获得创作文章分页
     *
     * @param pageReqVO 分页查询
     * @return 创作文章分页
     */
    PageResult<GeoArticleDO> getArticlePage(GeoArticlePageReqVO pageReqVO);

}