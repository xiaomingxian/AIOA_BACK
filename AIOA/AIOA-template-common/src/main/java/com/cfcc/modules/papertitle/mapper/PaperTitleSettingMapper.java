package com.cfcc.modules.papertitle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.papertitle.entity.PaperTitleSetting;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 稿纸头配置
 * @Author: jeecg-boot
 * @Date:   2019-10-14
 * @Version: V1.0
 */
public interface PaperTitleSettingMapper extends BaseMapper<PaperTitleSetting> {
    /**
     * 根据id删除
     * @param id
     * @return
     */
    boolean deletePaperTitleByIID(@Param("id") String id);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    PaperTitleSetting selectPaperById(@Param("id") Integer id);

    /**
     * 根据id修改
     * @param paperTitleSetting
     * @return
     */
    boolean updatePaperById(PaperTitleSetting paperTitleSetting);

    /**
     *条件分页查询
     * @param paperTitleSetting
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<PaperTitleSetting> selectPaperList(@Param("entity")PaperTitleSetting paperTitleSetting,@Param("pageNo")Integer pageNo,@Param("pageSize")Integer pageSize);

    /**
     * 数据统计
     * @param paperTitleSetting
     * @return
     */
    int selectPaperTotal(PaperTitleSetting paperTitleSetting);

    /**
     * 稿纸头下拉选列表
     * @return
     */
    List<PaperTitleSetting> paperTitleList();

    /**
     * 修改文头
     * @param paperTitleSetting
     * @return
     */
    boolean updateTitleById(PaperTitleSetting paperTitleSetting);
}
