package com.qianyu.module.geo.dal.dataobject.record;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianyu.module.geo.dal.dataobject.question.GeoQuestionDO;
import lombok.Data;

@TableName("geo_publish_record")
@KeySequence("geo_publish_record_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
public class GeoPublishRecordItemDO extends GeoPublishRecordDO {
    private String taskName;
    private String accountName;
    private String title;
    private Integer platform;
}
