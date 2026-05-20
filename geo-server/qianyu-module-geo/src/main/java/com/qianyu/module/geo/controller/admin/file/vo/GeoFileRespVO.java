package com.qianyu.module.geo.controller.admin.file.vo;

import com.qianyu.framework.common.pojo.CommonResult;
import com.qianyu.module.geo.dal.dataobject.file.GeoFileItemDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 企业图库 Response VO")
@Data
@ExcelIgnoreUnannotated
public class GeoFileRespVO extends GeoFileItemDO {
}
