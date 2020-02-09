package com.cfcc.modules.papertitle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.oaBus.entity.OaFile;
import com.cfcc.modules.papertitle.entity.OaTemplate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 模板管理
 * @Author: jeecg-boot
 * @Date:   2019-10-15
 * @Version: V1.0
 */
public interface OaTemplateMapper extends BaseMapper<OaTemplate> {

    /**
     * 根据id删除
     * @param id
     * @return
     */
    boolean deleteTemplateByIID(@Param("id") String id);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    OaTemplate selectTemplateById(@Param("id") Integer id);

    /**
     * 根据id修改
     * @param oaTemplate
     * @return
     */
    boolean updateTemplateById(OaTemplate oaTemplate);

    /**
     *条件分页查询
     * @param oaTemplate
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<OaTemplate> selectTemplateList(@Param("temp")OaTemplate oaTemplate, @Param("pageNo")Integer pageNo, @Param("pageSize")Integer pageSize);

    /**
     * 数据统计
     * @param oaTemplate
     * @return
     */
    int selectTemplateTotal(OaTemplate oaTemplate);

    /**
     *
     * @param type
     * @return
     */
    List<OaTemplate> templateList(@Param("type") Integer type);

    /**
     * 新增附件返回id
     * @param oaFile
     * @return
     */
    int insertOaFile(OaFile oaFile);

    /**
     * 根据附件id查询
     * @param iFileId
     * @return
     */
    OaFile getFileNameById(@Param("iFileId")String iFileId);
}
