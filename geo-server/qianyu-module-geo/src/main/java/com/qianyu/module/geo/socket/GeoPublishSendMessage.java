package com.qianyu.module.geo.socket;

import lombok.*;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Data
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoPublishSendMessage implements Serializable {

    private Long id;

    private List<String> fileList;

    private Integer declareAi;

    private String contentMarkdown;

    private String title;
    private Integer headless;

    private List<String> platformNames;
}
