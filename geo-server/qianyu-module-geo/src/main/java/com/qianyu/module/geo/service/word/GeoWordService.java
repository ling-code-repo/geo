package com.qianyu.module.geo.service.word;

import java.util.*;
import javax.validation.*;
import com.qianyu.module.geo.controller.admin.word.vo.*;
import com.qianyu.module.geo.dal.dataobject.word.GeoWordDO;
import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.framework.common.pojo.PageParam;

/**
 * 蒸馏词 Service 接口
 *
 * @author 系统管理员
 */
public interface GeoWordService {

    /**
     * 创建蒸馏词
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createWord(@Valid GeoWordSaveReqVO createReqVO);

    /**
     * 更新蒸馏词
     *
     * @param updateReqVO 更新信息
     */
    void updateWord(@Valid GeoWordSaveReqVO updateReqVO);

    /**
    * 批量删除蒸馏词
    *
    * @param ids 编号
    */
    void deleteWordListByIds(List<Long> ids);

    /**
     * 获得蒸馏词
     *
     * @param id 编号
     * @return 蒸馏词
     */
    GeoWordDO getWord(Long id);

    /**
     * 获得蒸馏词分页
     *
     * @param pageReqVO 分页查询
     * @return 蒸馏词分页
     */
    PageResult<GeoWordDO> getWordPage(GeoWordPageReqVO pageReqVO);

    List<GeoWordDO> selectList(Collection<Long> ids);

    List<String> distillWord(@Valid GeoWordDistillReqVO vo);
}