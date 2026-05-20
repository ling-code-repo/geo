package com.qianyu.module.geo.enums;

public enum GeoCreationEnum {
    WAIT_CREATION,
    CREATION,
    CREATION_SUCCESS;

    public static GeoCreationEnum enumOf(Integer value) {
        for (GeoCreationEnum item : values()) {
            if (item.ordinal() == value) {
                return item;
            }
        }
        return null;
    }
}
