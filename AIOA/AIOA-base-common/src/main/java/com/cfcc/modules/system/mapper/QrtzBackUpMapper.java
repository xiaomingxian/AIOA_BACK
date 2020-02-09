package com.cfcc.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.system.entity.QrtzBackUp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 备份情况
 * @Author: jeecg-boot
 * @Date: 2019-11-28
 * @Version: V1.0
 */
public interface QrtzBackUpMapper extends BaseMapper<QrtzBackUp> {

    List<QrtzBackUp> selectBackUpList(@Param("back") QrtzBackUp qrtzBackUp, @Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize);

    int selectBackUpTotal(QrtzBackUp qrtzBackUp);

    boolean deleteBackUpByIID(@Param("id") String id);

    QrtzBackUp getBackUpById(@Param("id") String id);
}
