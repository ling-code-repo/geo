package com.qianyu.module.geo.controller.admin.question;

import com.qianyu.module.geo.dal.dataobject.question.GeoQuestionItemDO;
import com.qianyu.module.geo.dal.dataobject.word.GeoWordDO;
import com.qianyu.module.geo.service.word.GeoWordService;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.qianyu.module.geo.controller.admin.question.vo.*;
import com.qianyu.module.geo.dal.dataobject.question.GeoQuestionDO;
import com.qianyu.module.geo.service.question.GeoQuestionService;

@Tag(name = "管理后台 - 扩展问题")
@RestController
@RequestMapping("/geo/question")
@Validated
public class GeoQuestionController {

    @Resource
    private GeoQuestionService questionService;
    @Autowired
    private GeoWordService geoWordService;

    @PostMapping("/create")
    @Operation(summary = "创建扩展问题")
//    @PreAuthorize("@ss.hasPermission('geo:question:create')")
    public CommonResult<Long> createQuestion(@Valid @RequestBody GeoQuestionSaveReqVO createReqVO) {
        return success(questionService.createQuestion(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新扩展问题")
//    @PreAuthorize("@ss.hasPermission('geo:question:update')")
    public CommonResult<Boolean> updateQuestion(@Valid @RequestBody GeoQuestionSaveReqVO updateReqVO) {
        questionService.updateQuestion(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除扩展问题")
    @Parameter(name = "id", description = "编号", required = true)
//    @PreAuthorize("@ss.hasPermission('geo:question:delete')")
    public CommonResult<Boolean> deleteQuestion(@RequestParam("id") Long id) {
        questionService.deleteQuestion(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除扩展问题")
//    @PreAuthorize("@ss.hasPermission('geo:question:delete')")
    public CommonResult<Boolean> deleteQuestionList(@RequestParam("ids") List<Long> ids) {
        questionService.deleteQuestionListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得扩展问题")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
//    @PreAuthorize("@ss.hasPermission('geo:question:query')")
    public CommonResult<GeoQuestionRespVO> getQuestion(@RequestParam("id") Long id) {
        GeoQuestionDO question = questionService.getQuestion(id);
        GeoQuestionRespVO bean = BeanUtils.toBean(question, GeoQuestionRespVO.class);
        GeoWordDO word = geoWordService.getWord(question.getWordId());
        if (word != null) {
            bean.setTarget(word.getTarget());
        }

        return success(bean);
    }

    @GetMapping("/page")
    @Operation(summary = "获得扩展问题分页")
//    @PreAuthorize("@ss.hasPermission('geo:question:query')")
    public CommonResult<PageResult<GeoQuestionRespVO>> getQuestionPage(@Valid GeoQuestionPageReqVO pageReqVO) {
        PageResult<GeoQuestionItemDO> pageResult = questionService.getQuestionPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, GeoQuestionRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出扩展问题 Excel")
//    @PreAuthorize("@ss.hasPermission('geo:question:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportQuestionExcel(@Valid GeoQuestionPageReqVO pageReqVO,
                                    HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<GeoQuestionItemDO> list = questionService.getQuestionPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "扩展问题.xls", "数据", GeoQuestionRespVO.class,
                BeanUtils.toBean(list, GeoQuestionRespVO.class));
    }

}