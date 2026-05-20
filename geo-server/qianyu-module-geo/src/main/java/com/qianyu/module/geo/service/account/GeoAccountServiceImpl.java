package com.qianyu.module.geo.service.account;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.framework.common.util.object.BeanUtils;
import com.qianyu.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.qianyu.framework.security.core.util.SecurityFrameworkUtils;
import com.qianyu.module.geo.controller.admin.account.vo.GeoAccountPageReqVO;
import com.qianyu.module.geo.controller.admin.account.vo.GeoAccountSaveOrUpdateReqVO;
import com.qianyu.module.geo.controller.admin.account.vo.GeoAccountSaveReqVO;
import com.qianyu.module.geo.dal.dataobject.account.GeoAccountDO;
import com.qianyu.module.geo.dal.mysql.account.GeoAccountMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

/**
 * 授权账号 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class GeoAccountServiceImpl implements GeoAccountService {

    @Resource
    private GeoAccountMapper accountMapper;

    @Override
    public void updateAccount(GeoAccountSaveReqVO updateReqVO) {
        // 更新
        GeoAccountDO updateObj = BeanUtils.toBean(updateReqVO, GeoAccountDO.class);
        accountMapper.updateById(updateObj);
    }

    @Override
    public void deleteAccount(Long id) {
        // 删除
        accountMapper.deleteById(id);
    }

    @Override
    public void deleteAccountListByIds(List<Long> ids) {
        // 删除
        accountMapper.deleteByIds(ids);
    }

    @Override
    public GeoAccountDO getAccount(Long id) {
        return accountMapper.selectById(id);
    }

    @Override
    public PageResult<GeoAccountDO> getAccountPage(GeoAccountPageReqVO pageReqVO) {
        return accountMapper.selectPage(pageReqVO);
    }

    @Override
    public Long saveOrUpdateAccount(GeoAccountSaveOrUpdateReqVO vo) {
//        Long userId = SecurityFrameworkUtils.getLoginUserId();

        GeoAccountDO geoAccountDO = accountMapper.selectOne(new LambdaQueryWrapperX<GeoAccountDO>().eq(
                GeoAccountDO::getPlatform, vo.getValue()
        ));
        if (geoAccountDO == null) {
            geoAccountDO = new GeoAccountDO();
        }
        geoAccountDO.setPlatform(vo.getValue());
        geoAccountDO.setName(vo.getName());
        geoAccountDO.setPath(vo.getAvatar());
        accountMapper.insertOrUpdate(geoAccountDO);
        return geoAccountDO.getId();
    }

}