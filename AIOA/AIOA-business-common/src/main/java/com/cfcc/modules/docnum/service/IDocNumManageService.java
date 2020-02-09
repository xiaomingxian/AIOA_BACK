package com.cfcc.modules.docnum.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.docnum.entity.DocNumManage;

/**
 * @Description: 文号管理表
 * @Author: jeecg-boot
 * @Date:   2019-11-18
 * @Version: V1.0
 */
public interface IDocNumManageService extends IService<DocNumManage> {
    /**
     * 查询空号列表
     * @param docNumManage
     * @return
     */
    IPage<DocNumManage> getNumList(DocNumManage docNumManage, Integer pageNo, Integer pageSize);


    /**
     * 选择使用文号
     * @param docNumManage
     * @return
     */
    boolean updateDocNumStatus(DocNumManage docNumManage);

    int addDocNum(DocNumManage docNumManage);

}
