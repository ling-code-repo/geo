package com.qianyu.module.geo.enums;

public enum PublishStatusEnum {
    DISABLED,
    ENABLED,
    ;

    public static PublishStatusEnum enumOf(Integer value) {
        for (PublishStatusEnum item : values()) {
            if (item.ordinal() == value) {
                return item;
            }
        }
        return null;
    }
}
