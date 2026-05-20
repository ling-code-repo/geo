package com.qianyu.module.geo.dal.dataobject.creation;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("geo_creation")
@KeySequence("geo_creation_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@ToString(callSuper = true)
public class GeoCreationItemDO extends GeoCreationDO {

    private String word;
}
