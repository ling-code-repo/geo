package com.qianyu.module.geo.dal.dataobject.question;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("geo_question")
@KeySequence("geo_question_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
public class GeoQuestionItemDO extends GeoQuestionDO {
    private String word;
    private String target;
}
