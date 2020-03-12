package com.cfcc.modules.oaBus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.entity.BusModelPermit;
import com.cfcc.modules.system.entity.SysRole;
import com.cfcc.modules.system.entity.SysUserRole;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description: 业务模板
 * @Author: jeecg-boot
 * @Date:   2019-10-21
 * @Version: V1.0
 */
public interface BusModelPermitMapper extends BaseMapper<BusModelPermit> {

    int updateByIid(BusModelPermit busModelPermit);

    int deleteById(String id);

    BusModelPermit findById(Integer iId);

    int count(@Param("ibusmodelId")Integer ibusmodelId);

    List<BusModelPermit> findPage(@Param("pageNo") int pageNo,@Param("pageSize") Integer pageSize,@Param("ibusmodelId")Integer ibusmodelId);

    List<BusModelPermit> findList();

    List<String> getTypeId(Integer modelId);

    int queryCount(BusModelPermit busModelPermit);

    List<BusModelPermit> findAllList(@Param("pageNo") int pageNo,@Param("pageSize") Integer pageSize,@Param("busModelPermit") BusModelPermit busModelPermit);

    List<SysRole> findRoleId();


    List<BusFunction> findByModelId(String id);
}
