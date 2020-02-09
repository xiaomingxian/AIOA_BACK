package com.cfcc.modules.oaBus.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.entity.BusFunctionPermit;
import com.cfcc.modules.oaBus.entity.BusModel;
import com.cfcc.modules.system.entity.SysDepart;
import com.cfcc.modules.system.entity.SysRole;
import com.cfcc.modules.system.entity.SysUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;



/**
 * @Description: 业务类型
 * @Author: jeecg-boot
 * @Date:   2019-10-21
 * @Version: V1.0
 */
public interface BusFunctionPermitMapper extends BaseMapper<BusFunctionPermit> {

    List<SysRole> rolelist();

    List<SysUser> userList();

    int count(Integer ibusId);

    List<BusFunctionPermit> findPage(@Param("pageNo") int pageNO, @Param("pageSize")int pageSize, @Param("ibusId")Integer ibusId);

    int updateByIid(BusFunctionPermit busFunctionPermit);

    int deleteById(String id);

    List<BusFunctionPermit> findList();

    List<String> getTypeId(Integer iBusId);

    BusFunctionPermit findById(Integer iId);

    int queryCount(BusFunctionPermit busFunctionPermit);

    List<BusFunctionPermit> findAllList(@Param("pageNO") int pageNo, @Param("pageSize") Integer pageSize, @Param("busFunctionPermit")BusFunctionPermit busFunctionPermit);

    List<BusFunctionPermit> findListByModelId(String modelid);

    void updateReade(@Param("table") String table,@Param("userId") String userId,@Param("functionId") String functionId,@Param("dataId") String dataId);

    List<String> getroleId(String sysUserId);

    String findTabelName(Integer iBusModelId);

    List<String> getParentId(String iTypeId);

    List<SysRole> findRoleByCode();
}

