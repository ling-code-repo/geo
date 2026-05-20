package com.qianyu.module.geo.controller.admin.statistics;

import com.qianyu.framework.common.pojo.CommonResult;
import com.qianyu.module.geo.component.StatisticsComponent;
import com.qianyu.module.geo.controller.admin.statistics.vo.ArticleRespVO;
import com.qianyu.module.geo.controller.admin.statistics.vo.MetricRespVO;
import com.qianyu.module.geo.controller.admin.statistics.vo.RecordRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.qianyu.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 发布记录")
@RestController
@RequestMapping("/geo/statistics")
@Validated
public class GeoStatisticsController {

    @Autowired
    private StatisticsComponent statisticsComponent;


    @GetMapping("/metric")
    @Operation(summary = "首页指标")
    public CommonResult<List<MetricRespVO>> metric() {
        List<MetricRespVO> rst = statisticsComponent.metric();
        return success(rst);
    }

    @GetMapping("/article")
    @Operation(summary = "ai创作")
    public CommonResult<ArticleRespVO> article() {
        return success(statisticsComponent.article());
    }

    @GetMapping("/record")
    @Operation(summary = "投喂数量")
    public CommonResult<RecordRespVO> record() {
        return success(statisticsComponent.record());
    }



}