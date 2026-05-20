package com.qianyu.module.geo.controller.admin.article;

import com.qianyu.module.geo.controller.admin.file.vo.FileSimpleRespVO;
import com.qianyu.module.geo.dal.dataobject.file.GeoFileItemDO;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.constraints.*;
import javax.validation.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;

import com.qianyu.framework.common.pojo.PageParam;
import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.framework.common.pojo.CommonResult;
import com.qianyu.framework.common.util.object.BeanUtils;
import static com.qianyu.framework.common.pojo.CommonResult.success;

import com.qianyu.framework.excel.core.util.ExcelUtils;

import com.qianyu.framework.apilog.core.annotation.ApiAccessLog;
import static com.qianyu.framework.apilog.core.enums.OperateTypeEnum.*;

import com.qianyu.module.geo.controller.admin.article.vo.*;
import com.qianyu.module.geo.dal.dataobject.article.GeoArticleDO;
import com.qianyu.module.geo.service.article.GeoArticleService;

@Tag(name = "管理后台 - 创作文章")
@RestController
@RequestMapping("/geo/article")
@Validated
public class GeoArticleController {

    @Resource
    private GeoArticleService articleService;

    @PostMapping("/create")
    @Operation(summary = "创建创作文章")
//    @PreAuthorize("@ss.hasPermission('geo:article:create')")
    public CommonResult<Long> createArticle(@Valid @RequestBody GeoArticleSaveReqVO createReqVO) {
        return success(articleService.createArticle(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新创作文章")
//    @PreAuthorize("@ss.hasPermission('geo:article:update')")
    public CommonResult<Boolean> updateArticle(@Valid @RequestBody GeoArticleSaveReqVO updateReqVO) {
        articleService.updateArticle(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除创作文章")
    @Parameter(name = "id", description = "编号", required = true)
//    @PreAuthorize("@ss.hasPermission('geo:article:delete')")
    public CommonResult<Boolean> deleteArticle(@RequestParam("id") Long id) {
        articleService.deleteArticle(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除创作文章")
//                @PreAuthorize("@ss.hasPermission('geo:article:delete')")
    public CommonResult<Boolean> deleteArticleList(@RequestParam("ids") List<Long> ids) {
        articleService.deleteArticleListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得创作文章")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
//    @PreAuthorize("@ss.hasPermission('geo:article:query')")
    public CommonResult<GeoArticleUpdateRespVO> getArticle(@RequestParam("id") Long id) {
        GeoArticleDO article = articleService.getArticle(id);
        return success(BeanUtils.toBean(article, GeoArticleUpdateRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得创作文章分页")
//    @PreAuthorize("@ss.hasPermission('geo:article:query')")
    public CommonResult<PageResult<GeoArticleRespVO>> getArticlePage(@Valid GeoArticlePageReqVO pageReqVO) {
        PageResult<GeoArticleDO> pageResult = articleService.getArticlePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, GeoArticleRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出创作文章 Excel")
//    @PreAuthorize("@ss.hasPermission('geo:article:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportArticleExcel(@Valid GeoArticlePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<GeoArticleDO> list = articleService.getArticlePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "创作文章.xls", "数据", GeoArticleRespVO.class,
                        BeanUtils.toBean(list, GeoArticleRespVO.class));
    }

    @GetMapping(value = {"/list-all-simple", "simple-list"})
    @Operation(summary = "获取岗位全列表", description = "只包含被开启的岗位，主要用于前端的下拉选项")
    public CommonResult<List<ArticleSimpleRespVO>> getSimpleList() {
        GeoArticlePageReqVO pageReqVO = new GeoArticlePageReqVO();
        // 获得岗位列表，只要开启状态的
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<GeoArticleDO> list = articleService.getArticlePage(pageReqVO).getList();
        // 排序后，返回给前端
        return success(BeanUtils.toBean(list, ArticleSimpleRespVO.class));
    }

}