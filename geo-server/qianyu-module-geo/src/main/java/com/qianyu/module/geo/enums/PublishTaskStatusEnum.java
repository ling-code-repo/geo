package com.qianyu.module.geo.enums;

public enum PublishTaskStatusEnum {
    NOT_PUBLISH,
    PUBLISHING,
    PUBLISH_SUCCESS,
    ;

    public static PublishTaskStatusEnum enumOf(Integer value) {
        for (PublishTaskStatusEnum item : values()) {
            if (item.ordinal() == value) {
                return item;
            }
        }
        return null;
    }
}
