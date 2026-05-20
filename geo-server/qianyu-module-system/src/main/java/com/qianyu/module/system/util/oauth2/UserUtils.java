package com.qianyu.module.system.util.oauth2;

import com.qianyu.framework.security.core.util.SecurityFrameworkUtils;
import com.qianyu.module.system.service.auth.AdminAuthService;
import com.qianyu.module.system.service.permission.PermissionService;
import com.qianyu.module.system.service.permission.RoleService;
import com.qianyu.module.system.service.user.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Set;

@Component
public class UserUtils {


    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RoleService roleService;

    private static UserUtils instance;

    @PostConstruct
    public void init() {
        instance = this;
    }

    private static UserUtils obj() {
        return instance;
    }

    /**
     * 获取当前登录用户ID不包含管理员
     *
     * @return
     */
    public static Long getUserIdNoAdmin() {
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        if (loginUserId != null &&
                !obj().roleService.hasAnySuperAdmin(
                        obj().permissionService.getUserRoleIdListByUserId(loginUserId))) {
            return loginUserId;
        }
        return null;
    }

    public static String getUserIdStrNoAdmin() {
        Long userId = getUserIdNoAdmin();
        return userId == null ? null : userId.toString();

    }
}
