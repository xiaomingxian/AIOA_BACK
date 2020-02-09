package com.cfcc.modules.oabutton.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.oabutton.entity.OaButtonSet;
import com.cfcc.modules.oabutton.entity.OaOpinionSet;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description: 意见配置按钮
 * @Author: jeecg-boot
 * @Date:   2019-10-27
 * @Version: V1.0
 */
public interface IOaOpinionSetService extends IService<OaOpinionSet> {

    /**
     * 重写查询方法，按条件查询
     * @param pageNo
     * @param pageSize
     * @param oaOpinionSet
     * @return
     */
    IPage<OaOpinionSet> getPage(Integer pageNo, Integer pageSize, OaOpinionSet oaOpinionSet);
    //根据id查询
    OaOpinionSet queryById(Integer iId);
    //根据任务Key查询
    OaOpinionSet queryByTaskKey(Map<String,Object> map);
    //根据类型查询
    List<OaOpinionSet> queryByType(String type, Integer iProcOpinionId);
    //编辑
    boolean updateOaButtonById(OaOpinionSet oaOpinionSet);
    //通过id删除
    void deleteOaOpinionSetByID(String id);
//    流程意见set列表
    List<OaOpinionSet> queryListByOpinionId(Integer iProcOpinionId);
    //根据序号校验
    List<OaOpinionSet> queryByOrderAndKey(Map<String,Object> map);
// 流程意见配置添加
    void insertOaButtonSet(OaOpinionSet oaOpinionSet);
}
