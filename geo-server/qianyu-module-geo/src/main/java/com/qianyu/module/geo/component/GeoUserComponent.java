package com.qianyu.module.geo.component;

import com.qianyu.framework.common.exception.ErrorCode;
import com.qianyu.framework.common.util.json.JsonUtils;
import com.qianyu.module.geo.controller.admin.user.vo.GeoUserReqVO.UGeoUserSaveReqVO;
import com.qianyu.module.system.controller.admin.user.vo.user.UserSaveReqVO;
import com.qianyu.module.system.dal.dataobject.dept.DeptDO;
import com.qianyu.module.system.dal.dataobject.dept.PostDO;
import com.qianyu.module.system.dal.dataobject.permission.RoleDO;
import com.qianyu.module.system.dal.mysql.dept.DeptMapper;
import com.qianyu.module.system.dal.mysql.dept.PostMapper;
import com.qianyu.module.system.dal.mysql.permission.RoleMapper;
import com.qianyu.module.system.service.dept.DeptService;
import com.qianyu.module.system.service.permission.PermissionService;
import com.qianyu.module.system.service.user.AdminUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.Valid;

import java.util.Set;

import static com.qianyu.module.system.enums.ErrorCodeConstants.GEO_USER_REGISTER_ERROR;

@Slf4j
@Component
public class GeoUserComponent {

    @Resource
    private AdminUserService userService;
    @Autowired
    private DeptMapper deptMapper;

    @Autowired
    private PostMapper postMapper;


    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RoleMapper roleMapper;

    @Transactional
    public ErrorCode registerUser(@Valid UGeoUserSaveReqVO reqVO) {
        UserSaveReqVO userSaveReqVO = new UserSaveReqVO();
        userSaveReqVO.setUsername(reqVO.getUsername());
        userSaveReqVO.setPassword(reqVO.getPassword());
        userSaveReqVO.setNickname(reqVO.getUsername());
        DeptDO deptDO = deptMapper.selectOne(DeptDO::getName, "新部门");
        if (deptDO == null){
            log.error("注册失败,部门不存在！数据 ：{}", JsonUtils.toJsonString(reqVO));
            return GEO_USER_REGISTER_ERROR;
        }

        PostDO postDO = postMapper.selectOne(PostDO::getName, "普通员工");
        if (postDO == null){
            log.error("注册失败,岗位不存在！数据 ：{}", JsonUtils.toJsonString(reqVO));
            return GEO_USER_REGISTER_ERROR;
        }
        userSaveReqVO.setPostIds(Set.of(postDO.getId()));
        userSaveReqVO.setDeptId(deptDO.getId());
        Long id = userService.doCreateUser(userSaveReqVO);
        RoleDO roleDO = roleMapper.selectOne(RoleDO::getName, "geo角色");
        if (roleDO == null){
            log.error("注册失败,角色不存在！数据 ：{}", JsonUtils.toJsonString(reqVO));
            return GEO_USER_REGISTER_ERROR;
        }
        permissionService.doAssignUserRole(id, Set.of(roleDO.getId()));
        return null;
    }
}
