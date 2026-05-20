package com.qianyu.module.geo.component;

import com.qianyu.module.geo.controller.admin.record.vo.GeoPublishRecordPageReqVO;
import com.qianyu.module.geo.controller.admin.statistics.vo.ArticleRespVO;
import com.qianyu.module.geo.controller.admin.statistics.vo.DayCountVO;
import com.qianyu.module.geo.controller.admin.statistics.vo.MetricRespVO;
import com.qianyu.module.geo.controller.admin.statistics.vo.RecordRespVO;
import com.qianyu.module.geo.dal.mysql.account.GeoAccountMapper;
import com.qianyu.module.geo.dal.mysql.article.GeoArticleMapper;
import com.qianyu.module.geo.dal.mysql.question.GeoQuestionMapper;
import com.qianyu.module.geo.dal.mysql.record.GeoPublishRecordMapper;
import com.qianyu.module.geo.dal.mysql.word.GeoWordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class StatisticsComponent {

    private static final String WORD_NAME = "AI蒸缩词";
    private static final String QUESTION_NAME = "训练问题";
    private static final String CREATION_NAME = "文章数量";
    private static final String ACCOUNT_NAME = "账号数量";

    @Autowired
    private GeoWordMapper geoWordMapper;
    @Autowired
    private GeoQuestionMapper geoQuestionMapper;
    @Autowired
    private GeoArticleMapper geoArticleMapper;
    @Autowired
    private GeoAccountMapper geoAccountMapper;

    @Autowired
    private GeoPublishRecordMapper geoPublishRecordMapper;
    public List<MetricRespVO> metric() {
        List<MetricRespVO> objects = new ArrayList<>();
        objects.add(new MetricRespVO(WORD_NAME, geoWordMapper.count()));
        objects.add(new MetricRespVO(QUESTION_NAME, geoQuestionMapper.count()));
        objects.add(new MetricRespVO(CREATION_NAME, geoArticleMapper.count()));
        objects.add(new MetricRespVO(ACCOUNT_NAME, geoAccountMapper.count()));
        return objects;
    }

    public ArticleRespVO article() {
        List<DayCountVO> result = fixCount(geoArticleMapper.countAIArticles());
        result.sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));
        List<Long> countList = result.stream().map(DayCountVO::getCount).collect(Collectors.toList());
        List<String> dateList = result.stream().map(DayCountVO::getDate).collect(Collectors.toList());
        return new ArticleRespVO(dateList, countList);
    }

    public RecordRespVO record() {
        List<DayCountVO> result = fixCount(geoPublishRecordMapper.countRecords());
        result.sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));
        List<Long> countList = result.stream().map(DayCountVO::getCount).collect(Collectors.toList());
        List<String> dateList = result.stream().map(DayCountVO::getDate).collect(Collectors.toList());
        return new RecordRespVO(dateList, countList);
    }


    public List<DayCountVO> fixCount(List<DayCountVO> rawResults) {

        // 将原始数据转换为Map，便于查找
        Map<String, Long> dataMap = new HashMap<>();
        for (DayCountVO vo : rawResults) {
            dataMap.put(vo.getDate(), vo.getCount()); // 直接使用数据库返回的 MM-dd 格式
        }

        // 生成过去5天的完整日期列表
        List<DayCountVO> filledResults = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = 4; i >= 0; i--) {  // 从5天前到今天
            LocalDate date = LocalDate.now().minusDays(i);
            String dateString = date.format(formatter);

            // 如果该日期有数据则使用原始数据，否则填充为0
            Long count = dataMap.getOrDefault(dateString, 0L);
            filledResults.add(new DayCountVO(dateString, count));
        }

        return filledResults;
    }

}
