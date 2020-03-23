package com.cfcc.modules.oaBus.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.entity.BusFunctionPermit;
import com.cfcc.modules.oaBus.entity.BusModel;
import com.cfcc.modules.system.entity.SysDepart;
import com.cfcc.modules.system.entity.SysRole;
import com.cfcc.modules.system.entity.SysUser;

import java.util.List;

/**
 * @Description: 业务类型
 * @Author: jeecg-boot
 * @Date:   2019-10-21
 * @Version: V1.0
 */
public interface IBusFunctionPermitService extends IService<BusFunctionPermit> {

    List<SysRole> roleList();

    List<SysUser> userList();

    IPage<BusFunctionPermit> findPage(Integer pageNo, Integer pageSize, Integer ibusId);

    boolean updateBYIid(BusFunctionPermit busFunctionPermit,String schema);
    

    boolean deleteById(String id,String schema);

    List<BusFunctionPermit> findList(String schema);

    List<String> getTypeId(Integer iBusId);

    BusFunctionPermit findById(Integer iId);

    IPage<BusFunctionPermit> findAllList(BusFunctionPermit busFunctionPermit, Integer pageNo, Integer pageSize);

    List<BusFunctionPermit> findListByModelId(String modelid);


    void updateReade(String table,String userId, String functionId, String dataId);

    List<String> getroleId(String sysUserId);

    String findTabelName(Integer iBusModelId);

    List<String> getParentId(String iTypeId);

    List<SysRole> findRoleByCode();

    int save1(BusFunctionPermit busFunctionPermit,String schema);
}
