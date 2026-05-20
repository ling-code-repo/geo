package com.qianyu.module.geo.controller.admin.file;

import cn.hutool.core.io.IoUtil;
import com.qianyu.framework.common.pojo.CommonResult;
import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.framework.common.util.object.BeanUtils;
import com.qianyu.module.geo.controller.admin.file.vo.*;
import com.qianyu.module.geo.controller.admin.word.vo.WorkSimpleRespVO;
import com.qianyu.module.geo.dal.dataobject.file.GeoFileDO;
import com.qianyu.module.geo.dal.dataobject.file.GeoFileItemDO;
import com.qianyu.module.geo.dal.dataobject.word.GeoWordDO;
import com.qianyu.module.geo.enums.GeoFileCategoryEnum;
import com.qianyu.module.geo.service.file.GeoFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.List;

import static com.qianyu.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 企业图库")
@RestController
@RequestMapping("/geo/file")
@Validated
@Slf4j
public class GeoFileController {

    @Resource
    private GeoFileService geoFileService;

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除企业图库")
//                @PreAuthorize("@ss.hasPermission('geo:file:delete')")
    public CommonResult<Boolean> deleteFileList(@RequestParam("ids") List<Long> ids) throws Exception{
        geoFileService.deleteFileListByIds(ids);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获得企业图库分页")
//    @PreAuthorize("@ss.hasPermission('geo:file:query')")
    public CommonResult<PageResult<GeoFileRespVO>> getFilePage(@Validated GeoFilePageReqVO pageReqVO) {
        PageResult<GeoFileItemDO> pageResult = geoFileService.getFilePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, GeoFileRespVO.class));
    }

    @PostMapping("/upload")
//    @Operation(summary = "上传文件", description = "模式一：后端上传文件")
    public CommonResult<String> uploadFile(@Valid GeoFileUploadReqVO uploadReqVO) throws Exception {
        MultipartFile file = uploadReqVO.getFile();
        byte[] content = IoUtil.readBytes(file.getInputStream());
        return success(geoFileService.createFile(content,uploadReqVO.getCategory(), file.getOriginalFilename(),
                uploadReqVO.getDirectory(), file.getContentType()));
    }

    @PostMapping("/create")
    @Operation(summary = "创建文件", description = "模式二：前端上传文件：配合 presigned-url 接口，记录上传了上传的文件")
    public CommonResult<Long> createFile(@Valid @RequestBody GeoFileCreateReqVO createReqVO) {
        return success(geoFileService.createFile(createReqVO));
    }

    @GetMapping(value = {"/list-all-simple", "simple-list"})
    @Operation(summary = "获取岗位全列表", description = "只包含被开启的岗位，主要用于前端的下拉选项")
    public CommonResult<List<FileSimpleRespVO>> getSimpleList() {
        // 获得岗位列表，只要开启状态的
        List<GeoFileItemDO> list = geoFileService.selectList();
        // 排序后，返回给前端
        list.sort(Comparator.comparing(GeoFileItemDO::getId));
        return success(BeanUtils.toBean(list, FileSimpleRespVO.class));
    }

}