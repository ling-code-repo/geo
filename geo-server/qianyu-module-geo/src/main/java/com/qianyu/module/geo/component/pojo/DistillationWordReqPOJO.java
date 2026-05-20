package com.qianyu.module.geo.component.pojo;

import lombok.*;

import java.io.Serializable;

@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DistillationWordReqPOJO implements Serializable {
    private String word;
    private String target;
    private long timestamp;
}
