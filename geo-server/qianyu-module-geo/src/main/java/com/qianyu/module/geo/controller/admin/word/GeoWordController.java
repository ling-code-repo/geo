package com.qianyu.module.geo.controller.admin.word;

import com.qianyu.framework.apilog.core.annotation.ApiAccessLog;
import com.qianyu.framework.common.enums.CommonStatusEnum;
import com.qianyu.framework.common.pojo.CommonResult;
import com.qianyu.framework.common.pojo.PageParam;
import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.framework.common.util.object.BeanUtils;
import com.qianyu.framework.excel.core.util.ExcelUtils;
import com.qianyu.module.geo.controller.admin.word.vo.*;
import com.qianyu.module.geo.dal.dataobject.word.GeoWordDO;
import com.qianyu.module.geo.service.word.GeoWordService;
import com.qianyu.module.system.controller.admin.dept.vo.post.PostSimpleRespVO;
import com.qianyu.module.system.dal.dataobject.dept.PostDO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.qianyu.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.qianyu.framework.common.pojo.CommonResult.error;
import static com.qianyu.framework.common.pojo.CommonResult.success;
import static com.qianyu.module.infra.enums.ErrorCodeConstants.GEO_CREATE_WORD_ERROR;

@Slf4j
@Tag(name = "管理后台 - 蒸馏词")
@RestController
@RequestMapping("/geo/word")
@Validated
public class GeoWordController {

    @Resource
    private GeoWordService wordService;

    @PostMapping("/create")
    @Operation(summary = "创建蒸馏词")
//    @PreAuthorize("@ss.hasPermission('geo:word:create')")
    public CommonResult<Long> createWord(@Valid @RequestBody GeoWordSaveReqVO createReqVO) {
        try {
            return success(wordService.createWord(createReqVO));
        } catch (Exception e) {
            log.error("创建词错误！", e);
        }
        return error(GEO_CREATE_WORD_ERROR);

    }

    @PostMapping("/distill")
    @Operation(summary = "蒸馏词")
//    @PreAuthorize("@ss.hasPermission('geo:word:create')")
    public CommonResult<List<String>> distillWord(@Valid @RequestBody GeoWordDistillReqVO vo) {
        try {
            return success(wordService.distillWord(vo));
        } catch (Exception e) {
            log.error("创建词错误！", e);
        }
        return error(GEO_CREATE_WORD_ERROR);

    }

    @PutMapping("/update")
    @Operation(summary = "更新蒸馏词")
//    @PreAuthorize("@ss.hasPermission('geo:word:update')")
    public CommonResult<Boolean> updateWord(@Valid @RequestBody GeoWordSaveReqVO updateReqVO) {
        wordService.updateWord(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除蒸馏词")
    @Parameter(name = "id", description = "编号", required = true)
//    @PreAuthorize("@ss.hasPermission('geo:word:delete')")
    public CommonResult<Boolean> deleteWord(@RequestParam("id") Long id) {
        return deleteWordList(Arrays.asList(id));
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除蒸馏词")
//    @PreAuthorize("@ss.hasPermission('geo:word:delete')")
    public CommonResult<Boolean> deleteWordList(@RequestParam("ids") List<Long> ids) {
        wordService.deleteWordListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得蒸馏词")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
//    @PreAuthorize("@ss.hasPermission('geo:word:query')")
    public CommonResult<GeoWordRespVO> getWord(@RequestParam("id") Long id) {
        GeoWordDO word = wordService.getWord(id);
        return success(BeanUtils.toBean(word, GeoWordRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得蒸馏词分页")
//    @PreAuthorize("@ss.hasPermission('geo:word:query')")
    public CommonResult<PageResult<GeoWordRespVO>> getWordPage(@Valid GeoWordPageReqVO pageReqVO) {
        PageResult<GeoWordDO> pageResult = wordService.getWordPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, GeoWordRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出蒸馏词 Excel")
//    @PreAuthorize("@ss.hasPermission('geo:word:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportWordExcel(@Valid GeoWordPageReqVO pageReqVO,
                                HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<GeoWordDO> list = wordService.getWordPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "蒸馏词.xls", "数据", GeoWordRespVO.class,
                BeanUtils.toBean(list, GeoWordRespVO.class));
    }

    @GetMapping(value = {"/list-all-simple", "simple-list"})
    @Operation(summary = "获取岗位全列表", description = "只包含被开启的岗位，主要用于前端的下拉选项")
    public CommonResult<List<WorkSimpleRespVO>> getSimplePostList() {
        // 获得岗位列表，只要开启状态的
        List<GeoWordDO> list = wordService.selectList(null);
        // 排序后，返回给前端
        list.sort(Comparator.comparing(GeoWordDO::getCreateTime));
        return success(BeanUtils.toBean(list, WorkSimpleRespVO.class));
    }

}