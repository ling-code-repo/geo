package com.qianyu.module.geo.component.pojo;

import lombok.*;

import java.io.Serializable;


@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PublishTaskPOJO implements Serializable {
    private Long id;
}
