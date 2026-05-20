package com.qianyu.module.geo.service.file;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.qianyu.module.geo.controller.admin.file.vo.GeoFileCreateReqVO;
import com.qianyu.module.geo.controller.admin.file.vo.GeoFilePageReqVO;
import com.qianyu.module.geo.dal.dataobject.file.GeoFileDO;
import com.qianyu.module.geo.dal.dataobject.file.GeoFileItemDO;
import com.qianyu.module.geo.dal.mysql.file.GeoFileMapper;
import com.qianyu.module.geo.enums.GeoFileCategoryEnum;
import com.qianyu.module.infra.dal.dataobject.file.FileDO;
import com.qianyu.module.infra.service.file.FileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.qianyu.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.qianyu.module.infra.enums.ErrorCodeConstants.FILE_NOT_EXISTS;

/**
 * 企业图库 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class GeoGeoFileServiceImpl extends ServiceImpl<GeoFileMapper, GeoFileDO> implements GeoFileService {

    @Resource
    private FileService fileService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteFileListByIds(List<Long> ids) throws Exception{
        List<GeoFileDO> geoFileDOS = baseMapper.selectByIds(ids);
        Set<Long> fileIds = geoFileDOS.stream().map(GeoFileDO::getFileId).collect(Collectors.toSet());
        fileService.deleteFileList(fileIds);
        // 删除
        baseMapper.deleteByIds(ids);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String createFile(@NotEmpty(message = "文件内容不能为空") byte[] content, GeoFileCategoryEnum category, String name, String directory, String type) {
        FileDO fileDO = fileService.createFile0(content, name, directory, type);
        save(GeoFileDO.builder().fileId(fileDO.getId()).category(category).build());
        return fileDO.getUrl();
    }


    @Override
    public PageResult<GeoFileItemDO> getFilePage(GeoFilePageReqVO pageReqVO) {
        PageResult<GeoFileItemDO> pageResult = baseMapper.selectPage(pageReqVO);
        return pageResult;
    }


    @Override
    public Long createFile(GeoFileCreateReqVO createReqVO) {
        Long fileId = fileService.createFile(createReqVO);
        save(GeoFileDO.builder().fileId(fileId).category(createReqVO.getCategory()).build());
        return fileId;
    }

    @Override
    public List<GeoFileItemDO> selectList() {
        return baseMapper.selectSimpleList();
    }

}