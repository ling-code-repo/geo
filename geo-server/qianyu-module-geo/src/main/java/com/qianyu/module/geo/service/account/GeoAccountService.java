package com.qianyu.module.geo.service.account;

import java.util.*;
import javax.validation.*;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qianyu.module.geo.controller.admin.account.vo.*;
import com.qianyu.module.geo.dal.dataobject.account.GeoAccountDO;
import com.qianyu.framework.common.pojo.PageResult;

/**
 * 授权账号 Service 接口
 *
 * @author 系统管理员
 */
public interface GeoAccountService {

    /**
     * 更新授权账号
     *
     * @param updateReqVO 更新信息
     */
    void updateAccount(@Valid GeoAccountSaveReqVO updateReqVO);

    /**
     * 删除授权账号
     *
     * @param id 编号
     */
    void deleteAccount(Long id);

    /**
    * 批量删除授权账号
    *
    * @param ids 编号
    */
    void deleteAccountListByIds(List<Long> ids);

    /**
     * 获得授权账号
     *
     * @param id 编号
     * @return 授权账号
     */
    GeoAccountDO getAccount(Long id);

    /**
     * 获得授权账号分页
     *
     * @param pageReqVO 分页查询
     * @return 授权账号分页
     */
    PageResult<GeoAccountDO> getAccountPage(GeoAccountPageReqVO pageReqVO);

    Long saveOrUpdateAccount(GeoAccountSaveOrUpdateReqVO vo);
}