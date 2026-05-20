package com.qianyu.module.geo.controller.admin.statistics.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
@Data
public class MetricRespVO implements Serializable {

    private String name;

    private Long value;
}
