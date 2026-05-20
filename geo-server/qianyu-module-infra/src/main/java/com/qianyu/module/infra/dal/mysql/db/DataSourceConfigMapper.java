package com.qianyu.module.infra.dal.mysql.db;

import com.qianyu.framework.mybatis.core.mapper.BaseMapperX;
import com.qianyu.module.infra.dal.dataobject.db.DataSourceConfigDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据源配置 Mapper
 *
 * @author qianyu
 */
@Mapper
public interface DataSourceConfigMapper extends BaseMapperX<DataSourceConfigDO> {
}
