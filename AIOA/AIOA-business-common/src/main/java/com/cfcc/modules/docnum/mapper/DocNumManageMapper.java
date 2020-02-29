package com.cfcc.modules.docnum.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.docnum.entity.DocNumManage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 文号管理表
 * @Author: jeecg-boot
 * @Date: 2019-11-18
 * @Version: V1.0
 */
public interface DocNumManageMapper extends BaseMapper<DocNumManage> {

    /**
     * 登记文号
     *
     * @param docNumManage
     * @return
     */
    int addDocNum(DocNumManage docNumManage);

    /**
     * 选择使用
     *
     * @param docNumManage
     * @return
     */
    boolean updateDocNumStatus(DocNumManage docNumManage);

    /**
     * 查询空号列表
     *
     * @param docNumManage
     * @return
     */
    List<DocNumManage> getNumList(@Param("doc") DocNumManage docNumManage,
                                  @Param("pageNo") Integer pageNo,
                                  @Param("pageSize") Integer pageSize);

    /**
     * 统计列表数量
     *
     * @param docNumManage
     * @return
     */
    int getNumListTotal(DocNumManage docNumManage);

    DocNumManage queryMaxDocNum(DocNumManage docNumManage);

    int updateMaxNum(DocNumManage docNumManage);

    List<DocNumManage> checkDocNum(DocNumManage docNumManage);

    List<DocNumManage> getBusdataDocNum(DocNumManage docNumManage);


}
