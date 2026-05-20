package com.qianyu.module.geo.component.pojo;

import lombok.*;

import java.io.Serializable;


@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreationTaskPOJO implements Serializable {
    private Long id;

}
