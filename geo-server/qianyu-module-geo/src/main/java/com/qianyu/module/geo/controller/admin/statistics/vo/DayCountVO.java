package com.qianyu.module.geo.controller.admin.statistics.vo;

// DayCountVO.java
public class DayCountVO {
    private String date;      // 日期字符串，格式为 MM-dd
    private Long count;       // 对应日期的数量

    // 构造函数、getter和setter方法
    public DayCountVO(String date, Long count) {
        this.date = date;
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}