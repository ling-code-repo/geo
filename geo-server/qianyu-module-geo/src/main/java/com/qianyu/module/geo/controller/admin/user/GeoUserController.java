package com.qianyu.module.geo.controller.admin.user;

import cn.hutool.core.collection.CollUtil;
import com.qianyu.framework.apilog.core.annotation.ApiAccessLog;
import com.qianyu.framework.common.enums.CommonStatusEnum;
import com.qianyu.framework.common.exception.ErrorCode;
import com.qianyu.framework.common.pojo.CommonResult;
import com.qianyu.framework.common.pojo.PageParam;
import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.framework.excel.core.util.ExcelUtils;
import com.qianyu.module.geo.component.GeoUserComponent;
import com.qianyu.module.geo.controller.admin.user.vo.GeoUserReqVO.UGeoUserSaveReqVO;
import com.qianyu.module.system.controller.admin.user.vo.user.*;
import com.qianyu.module.system.convert.user.UserConvert;
import com.qianyu.module.system.dal.dataobject.dept.DeptDO;
import com.qianyu.module.system.dal.dataobject.user.AdminUserDO;
import com.qianyu.module.system.enums.common.SexEnum;
import com.qianyu.module.system.service.dept.DeptService;
import com.qianyu.module.system.service.user.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.qianyu.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.qianyu.framework.common.pojo.CommonResult.error;
import static com.qianyu.framework.common.pojo.CommonResult.success;
import static com.qianyu.framework.common.util.collection.CollectionUtils.convertList;

@Slf4j
@Tag(name = "管理后台 - 用户")
@RestController
@RequestMapping("/geo/user")
@Validated
public class GeoUserController {



    @Autowired
    private GeoUserComponent geoUserComponent;

    @PermitAll
    @PostMapping("/register")
    @Operation(summary = "新增用户")
    public CommonResult<String> registerUser(@Valid @RequestBody UGeoUserSaveReqVO reqVO) {
        ErrorCode errorCode = geoUserComponent.registerUser(reqVO);
        if (errorCode == null){
            return success("注册成功");
        }
        return  error(errorCode);
    }


}
