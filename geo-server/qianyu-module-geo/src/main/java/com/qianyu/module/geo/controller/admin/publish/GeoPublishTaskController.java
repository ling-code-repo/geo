package com.qianyu.module.geo.controller.admin.publish;

import com.qianyu.framework.apilog.core.annotation.ApiAccessLog;
import com.qianyu.framework.common.pojo.CommonResult;
import com.qianyu.framework.common.pojo.PageParam;
import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.framework.common.util.json.JsonUtils;
import com.qianyu.framework.common.util.object.BeanUtils;
import com.qianyu.framework.excel.core.util.ExcelUtils;
import com.qianyu.framework.security.core.util.SecurityFrameworkUtils;
import com.qianyu.module.geo.controller.admin.publish.vo.*;
import com.qianyu.module.geo.dal.dataobject.publish.GeoPublishTaskDO;
import com.qianyu.module.geo.service.publish.GeoPublishTaskService;
import com.qianyu.module.geo.socket.GeoWebSocketMessageListener;
import com.qianyu.module.geo.socket.GeoWebsocketMessageSender;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static com.qianyu.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.qianyu.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 发布任务")
@RestController
@RequestMapping("/geo/publish-task")
@Validated
public class GeoPublishTaskController {

    @Resource
    private GeoPublishTaskService publishTaskService;

    @PostMapping("/create")
    @Operation(summary = "创建发布任务")
//    @PreAuthorize("@ss.hasPermission('geo:publish-task:create')")
    public CommonResult<Long> createPublishTask(@Valid @RequestBody GeoPublishTaskSaveReqVO createReqVO) {
        return success(publishTaskService.createPublishTask(createReqVO));
    }

    @PostMapping("/execute")
    @Operation(summary = "执行发布任务")
//    @PreAuthorize("@ss.hasPermission('geo:publish-task:create')")
    public CommonResult<Boolean> execute(@Valid @RequestBody GeoExecutePublishTaskReqVO vo) {
        publishTaskService.execute(vo.getId());
        return success(true);
    }


    @PostMapping("/update_record")
    @Operation(summary = "执行发布任务")
//    @PreAuthorize("@ss.hasPermission('geo:publish-task:create')")
    public CommonResult<Boolean> updateRecord(@Valid @RequestBody GeoPublishRecordSaveReqVO vo) {
        publishTaskService.updateRecord(vo);
        return success(true);
    }


    @PutMapping("/update")
    @Operation(summary = "更新发布任务")
//    @PreAuthorize("@ss.hasPermission('geo:publish-task:update')")
    public CommonResult<Boolean> updatePublishTask(@Valid @RequestBody GeoPublishTaskSaveReqVO updateReqVO) {
        publishTaskService.updatePublishTask(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除发布任务")
    @Parameter(name = "id", description = "编号", required = true)
//    @PreAuthorize("@ss.hasPermission('geo:publish-task:delete')")
    public CommonResult<Boolean> deletePublishTask(@RequestParam("id") Long id) {
        publishTaskService.deletePublishTask(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除发布任务")
//                @PreAuthorize("@ss.hasPermission('geo:publish-task:delete')")
    public CommonResult<Boolean> deletePublishTaskList(@RequestParam("ids") List<Long> ids) {
        publishTaskService.deletePublishTaskListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得发布任务")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
//    @PreAuthorize("@ss.hasPermission('geo:publish-task:query')")
    public CommonResult<GeoPublishTaskRespVO> getPublishTask(@RequestParam("id") Long id) {
        GeoPublishTaskDO publishTask = publishTaskService.getPublishTask(id);
        return success(BeanUtils.toBean(publishTask, GeoPublishTaskRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得发布任务分页")
//    @PreAuthorize("@ss.hasPermission('geo:publish-task:query')")
    public CommonResult<PageResult<GeoPublishTaskRespVO>> getPublishTaskPage(@Valid GeoPublishTaskPageReqVO pageReqVO) {
        PageResult<GeoPublishTaskDO> pageResult = publishTaskService.getPublishTaskPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, GeoPublishTaskRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出发布任务 Excel")
//    @PreAuthorize("@ss.hasPermission('geo:publish-task:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPublishTaskExcel(@Valid GeoPublishTaskPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<GeoPublishTaskDO> list = publishTaskService.getPublishTaskPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "发布任务.xls", "数据", GeoPublishTaskRespVO.class,
                        BeanUtils.toBean(list, GeoPublishTaskRespVO.class));
    }



}