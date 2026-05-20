package com.qianyu.module.geo.controller.admin.user.vo.GeoUserReqVO;

import cn.hutool.core.util.ObjectUtil;
import com.qianyu.framework.common.validation.Mobile;
import com.qianyu.module.system.framework.operatelog.core.DeptParseFunction;
import com.qianyu.module.system.framework.operatelog.core.PostParseFunction;
import com.qianyu.module.system.framework.operatelog.core.SexParseFunction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mzt.logapi.starter.annotation.DiffLogField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.util.Set;

@Schema(description = "管理后台 - 用户创建/修改 Request VO")
@Data
public class UGeoUserSaveReqVO {

    @Schema(description = "用户账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "yudao")
    @NotBlank(message = "用户账号不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "用户账号由 数字、字母 组成")
    @Size(min = 4, max = 30, message = "用户账号长度为 4-30 个字符")
    @DiffLogField(name = "用户账号")
    private String username;

    // ========== 仅【创建】时，需要传递的字段 ==========

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @Length(min =6, max = 20, message = "密码长度为 8-18 位")
    private String password;


}
