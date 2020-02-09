package com.cfcc.modules.oaBus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.oaBus.entity.BusPage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description: 业务页面表
 * @Author: jeecg-boot
 * @Date: 2019-10-14
 * @Version: V1.0
 */
public interface BusPageMapper extends BaseMapper<BusPage> {

    int countBusPage(BusPage busPage);

    /**
     * 查询一条数据
     *
     * @param id
     * @return
     */
    BusPage getBusPageById(int id);

    List<BusPage> queryBusPage(@Param("pageNo") int pageNo, @Param("pageSize") Integer pageSize, @Param("busPage") BusPage busPage);

    int addBusPage(BusPage busPage);

    int updateBusPageById(BusPage busPage);

    void deleteBusPageById(String id);

    @Select("SELECT act_show actShow FROM `oa_bus_page` where s_page_path=#{pageRef};")
    String queryActShowByPageRef(@Param("pageRef") String pageRef);
}
