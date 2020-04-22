package com.cfcc.modules.data.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.modules.oaBus.entity.OaBusdata;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface DataAnalysisMapper extends BaseMapper<OaBusdata> {
    //统计我的数据的数据量  --LYJ
    List<Map<String, Object>> findByTableAndMy(@Param("table")String table, @Param("bdata")OaBusdata oaBusdata);
    //我的办结率   --LYJ
    Map<String, Object> MyRate(@Param("table")String table, @Param("bdata") OaBusdata oaBusdata);


    String getDepartId(String sCreateBy);

    //查询表里面的表字段
    List<Map<String, Object>> selectColums(@Param("table")String table,@Param("bdata") OaBusdata oaBusdata);

    List<Map<String, Object>> selectDeptNames(@Param("table")String table, @Param("bdata") OaBusdata oaBusdata);

    List<Map<String, Object>> selectMyCreateDepart(@Param("table")String table, @Param("bdata")OaBusdata oaBusdata);

    Map<String, Object> PeerNum(@Param("table")String table, @Param("bdata") OaBusdata oaBusdata);

    Map<String, Object> HandlingRate(@Param("table")String table, @Param("bdata") OaBusdata oaBusdata);

    Map<String, Object> Handling(@Param("table")String table, @Param("bdata") OaBusdata oaBusdata);

    Map<String,Object> getAvg(@Param("table")String table, @Param("bdata") OaBusdata oaBusdata);
}
