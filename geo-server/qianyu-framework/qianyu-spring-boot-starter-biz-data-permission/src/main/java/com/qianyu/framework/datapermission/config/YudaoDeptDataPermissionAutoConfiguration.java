package com.qianyu.framework.datapermission.config;

import com.qianyu.framework.common.biz.system.permission.PermissionCommonApi;
import com.qianyu.framework.datapermission.core.rule.dept.DeptDataPermissionRule;
import com.qianyu.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import com.qianyu.framework.security.core.LoginUser;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * 基于部门的数据权限 AutoConfiguration
 *
 * @author qianyu
 */
@AutoConfiguration
@ConditionalOnClass(LoginUser.class)
@ConditionalOnBean(value = {DeptDataPermissionRuleCustomizer.class})
public class YudaoDeptDataPermissionAutoConfiguration {

    @Bean
    public DeptDataPermissionRule deptDataPermissionRule(PermissionCommonApi permissionApi,
                                                         List<DeptDataPermissionRuleCustomizer> customizers) {
        // 创建 DeptDataPermissionRule 对象
        DeptDataPermissionRule rule = new DeptDataPermissionRule(permissionApi);
        // 补全表配置
        customizers.forEach(customizer -> customizer.customize(rule));
        return rule;
    }

}
