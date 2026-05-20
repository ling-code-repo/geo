package com.qianyu.module.geo.controller.admin.statistics.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@Data
public class RecordRespVO implements Serializable {

    private List<String > x;

    private List<Long> y;
}
