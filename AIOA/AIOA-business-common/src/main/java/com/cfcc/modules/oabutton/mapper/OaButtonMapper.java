package com.cfcc.modules.oabutton.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.oabutton.entity.OaButton;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 按钮管理
 * @Author: jeecg-boot
 * @Date:   2019-10-24
 * @Version: V1.0
 */
public interface OaButtonMapper extends BaseMapper<OaButton> {

    int queryButtonCount(OaButton oaButton);


    List<OaButton> queryButton(@Param("button")OaButton oaButton, @Param("pageNo")Integer pageNo, @Param("pageSize")Integer pageSize);

    List<OaButton> queryById(@Param("id") Integer id,@Param("sBtnName") String sBtnName);

    void deleteOaButtonByID(@Param("id")String id);

    boolean updateOaButtonById(OaButton oaButton);

    void insertoaButton(OaButton oaButton);

    List<OaButton> buttonList();

    List<OaButton> findList();

}
