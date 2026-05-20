package com.qianyu.module.geo.service.file;

import java.util.*;
import javax.validation.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qianyu.module.geo.controller.admin.file.vo.GeoFileCreateReqVO;
import com.qianyu.module.geo.controller.admin.file.vo.GeoFilePageReqVO;
import com.qianyu.module.geo.dal.dataobject.file.GeoFileDO;
import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.module.geo.dal.dataobject.file.GeoFileItemDO;
import com.qianyu.module.geo.enums.GeoFileCategoryEnum;

/**
 * 企业图库 Service 接口
 *
 * @author 系统管理员
 */
public interface GeoFileService extends IService<GeoFileDO> {





    /**
    * 批量删除企业图库
    *
    * @param ids 编号
    */
    void deleteFileListByIds(List<Long> ids) throws Exception;


    /**
     * 保存文件，并返回文件的访问路径
     *
     * @param content   文件内容
     * @param category  Geo文件类型
     * @param name      文件名称，允许空
     * @param directory 目录，允许空
     * @param type      文件的 MIME 类型，允许空
     * @return 文件路径
     */
    String createFile(@NotEmpty(message = "文件内容不能为空") byte[] content , GeoFileCategoryEnum category,
                      String name, String directory, String type);

    /**
     * 获得企业图库分页
     *
     * @param pageReqVO 分页查询
     * @return 企业图库分页
     */
    PageResult<GeoFileItemDO> getFilePage(GeoFilePageReqVO pageReqVO);

    /**
     * 创建文件
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createFile(@Valid GeoFileCreateReqVO createReqVO);

    List<GeoFileItemDO> selectList();
}