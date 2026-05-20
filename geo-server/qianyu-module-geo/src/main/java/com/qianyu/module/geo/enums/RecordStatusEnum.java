package com.qianyu.module.geo.enums;

public enum RecordStatusEnum {
    ERROR,
    SUCCESS,
    ;

    public static RecordStatusEnum enumOf(Integer value) {
        for (RecordStatusEnum item : values()) {
            if (item.ordinal() == value) {
                return item;
            }
        }
        return null;
    }
}
