package com.cfcc.modules.papertitle.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.oaBus.entity.OaFile;
import com.cfcc.modules.papertitle.entity.OaTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: 模板管理
 * @Author: jeecg-boot
 * @Date:   2019-10-15
 * @Version: V1.0
 */
public interface IOaTemplateService extends IService<OaTemplate> {
    /**
     * 根据id查询
     * @param id
     * @return
     */
    OaTemplate queryById(Integer id);

    /**
     * 根据id修改
     * @param oaTemplate
     * @return
     */
    boolean updateTemplateById(OaTemplate oaTemplate);
    /**
     * 根据iid删除
     * @param id
     * @return
     */
    boolean deleteTemplateTitleByIID(String id);

    /**
     * 分页查询列表
     * @param oaTemplate
     * @param pageNo
     * @param pageSize
     * @return
     */
    IPage<OaTemplate> queryTemplateList(OaTemplate oaTemplate, Integer pageNo, Integer pageSize);

    /**
     * 查询模板列表
     * @param type
     * @return
     */
    List<OaTemplate> templateList(Integer type);

    /**
     * 根据附件id查询
     * @param iFileId
     * @return
     */
    OaFile getFileNameById(String iFileId);

    /**
     * 批量上传
     * @param files
     * @param sTable
     * @param iTableId
     * @param sFileType
     * @param request
     * @param response
     * @return
     */
    List<OaFile> batchUploads(MultipartFile[] files, String sTable, Integer iTableId, String sFileType, HttpServletRequest request, HttpServletResponse response);


    /**
     * 只上传文件
     * @param files
     * @param request
     * @param response
     * @return
     */
    List<OaFile> uploadFiles(MultipartFile[] files, HttpServletRequest request, HttpServletResponse response);


}
