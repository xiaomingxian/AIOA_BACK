package com.cfcc.modules.oaBus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.oaBus.entity.BusModel;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description: 业务模块表（业务分类表）
 * @Author: jeecg-boot
 * @Date: 2019-10-12
 * @Version: V1.0
 */
public interface BusModelMapper extends BaseMapper<BusModel> {


    BusModel getBusModelById(Integer iId);
//  BusModel getBusModelById(Integer iId);

    int updateBusModelByIdDao(BusModel busModel);

    int removeBusModelByIdDao(String id);

    List<BusModel> queryBusModel(@Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize, @Param("busModel") BusModel busModel);

    int queryBusModelCount(BusModel busModel);

    List<BusModel> getModelListDao();

    List<BusModel> findList();

    @Select("SELECT m.* FROM `oa_bus_model_permit` b LEFT JOIN oa_bus_model m on b.i_bus_model_id=m.i_id where b.s_permit_type=0 and b.s_display=1 and m.i_id is not null ")
    List<BusModel> allPeopleSee();


    @Select("SELECT m.* FROM `oa_bus_model_permit` b LEFT JOIN oa_bus_model m on b.i_bus_model_id=m.i_id   " +
            "where  b.s_display=1 and  b.s_permit_type=#{type} and b.i_type_id=#{id} and m.i_id is not null;")
    List<BusModel> queryByUserMsg(@Param("type") String type, @Param("id") Object id);

    List<BusModel> selectDocType();

    List<BusModel> findListNotInPermit();

}
