package com.cfcc.modules.oabutton.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.oabutton.entity.OaProcOpinion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 意见配置按钮
 * @Author: jeecg-boot
 * @Date:   2019-10-27
 * @Version: V1.0
 */
public interface IOaProcOpinionService extends IService<OaProcOpinion> {

    /**
     * 重写查询方法，按条件查询
     * @param pageNo
     * @param pageSize
     * @param oaProcOpinion
     * @return
     */
    IPage<OaProcOpinion> getPage(Integer pageNo, Integer pageSize, OaProcOpinion oaProcOpinion);
    //通过id查询
    OaProcOpinion queryById(Integer iId);
    //编辑
    boolean updateOaProcOpinionById(OaProcOpinion oaProcOpinion);
    //根据id删除
    void deleteOaProcOpinionByID(String id);

    int insertOaProcOpinion(OaProcOpinion oaProcOpinion);
//根据名称查询数量
    int queryBysProcOpinionName(String sProcOpinionName);

    List<OaProcOpinion> queryByKey(String key);
}
