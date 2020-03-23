package com.cfcc.modules.oaBus.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfcc.common.constant.CacheConstant;
import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.entity.BusFunctionPermit;
import com.cfcc.modules.oaBus.entity.BusModel;
import com.cfcc.modules.oaBus.entity.BusModelPermit;
import com.cfcc.modules.oaBus.mapper.BusFunctionMapper;
import com.cfcc.modules.oaBus.mapper.BusFunctionPermitMapper;
import com.cfcc.modules.oaBus.service.IBusFunctionPermitService;
import com.cfcc.modules.system.entity.SysDepart;
import com.cfcc.modules.system.entity.SysRole;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.system.mapper.SysDepartMapper;
import com.cfcc.modules.system.mapper.SysRoleMapper;
import com.cfcc.modules.system.mapper.SysUserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 业务类型
 * @Author: jeecg-boot
 * @Date:   2019-10-21
 * @Version: V1.0
 */
@Service
public class BusFunctionPermitServiceImpl extends ServiceImpl<BusFunctionPermitMapper, BusFunctionPermit> implements IBusFunctionPermitService {
    @Autowired
    BusFunctionPermitMapper busFunctionPermitMapper;

    @Autowired
    BusFunctionMapper busFunctionMapper;

    @Autowired
    SysRoleMapper sysRoleMapper ;

    @Autowired
    SysDepartMapper sysDepartMapper ;

    @Autowired
    SysUserMapper sysUserMapper ;

    @Override
    public List<SysRole> roleList() {
        return busFunctionPermitMapper.rolelist();
    }

    @Override
    public List<SysUser> userList() {
        return busFunctionPermitMapper.userList();
    }

    @Override
    public IPage<BusFunctionPermit> findPage(Integer ibusId,Integer pageNo, Integer pageSize) {
        BusFunctionPermit busFunctionPermit=new BusFunctionPermit();
        BeanUtils.copyProperties(busFunctionPermit,busFunctionPermit);
        int total = busFunctionPermitMapper.count(ibusId);
        List<BusFunctionPermit> functionPermitList =  busFunctionPermitMapper.findPage((pageNo-1)*pageSize,pageSize,ibusId);
        for(int i = 0; i < functionPermitList.size() ; i ++){
            BusFunction busFunction = busFunctionMapper.selectByIid(functionPermitList.get(i).getIBusId());
            functionPermitList.get(i).setBusFunctionName(busFunction.getSName());
            if( "1".equals(functionPermitList.get(i).getSPermitType())){      // 角色
                SysRole sysRole = sysRoleMapper.selectById(functionPermitList.get(i).getITypeId());
                if(! (sysRole == null)){
                    functionPermitList.get(i).setItypeName(sysRole.getRoleName());
                }
            }
            else if( "2".equals(functionPermitList.get(i).getSPermitType())){      // 部门
                SysDepart sysDepart = sysDepartMapper.selectById( functionPermitList.get(i).getITypeId());
                if(! (sysDepart == null)){
                    functionPermitList.get(i).setItypeName(sysDepart.getDepartName());
                    functionPermitList.get(i).setParentName(sysDepartMapper.selectById( sysDepart.getParentId()).getDepartName());
                }
            }
            else if("3".equals(functionPermitList.get(i).getSPermitType()) ){      // 人员
                SysUser sysUser = sysUserMapper.selectById(functionPermitList.get(i).getITypeId());
                if(! (sysUser == null)){
                    functionPermitList.get(i).setItypeName(sysUser.getRealname());
                }

            }
        }
        IPage<BusFunctionPermit> pageList = new Page<BusFunctionPermit>();
        pageList.setRecords(functionPermitList);
        pageList.setTotal(total);
        pageList.setSize(pageSize) ;
        pageList.setCurrent(pageNo) ;
        return pageList;
    }
    @Override
    @CacheEvict(value=CacheConstant.FUNCTION_PERMIT_CACHE, allEntries=true)
    public boolean updateBYIid(BusFunctionPermit busFunctionPermit,String schemal) {
        try {
            busFunctionPermitMapper.updateByIid(busFunctionPermit);
            return true;

        }catch (Exception e){
            return false;
        }
    }

    @Override
    @CacheEvict(value=CacheConstant.FUNCTION_PERMIT_CACHE, allEntries=true)
    public boolean deleteById(String id,String schema) {
        try {
            busFunctionPermitMapper.deleteById(id);
            return true;

        }catch (Exception e){
            return false;
        }
    }



    @Override
    @Cacheable(value = CacheConstant.FUNCTION_PERMIT_CACHE)
    public List<BusFunctionPermit> findList(String schema) {
        List<BusFunctionPermit>  busFuncitonPermitList=busFunctionPermitMapper.findList();
        return busFuncitonPermitList;
    }

    @Override
    public List<String> getTypeId(Integer iBusId) {
        return busFunctionPermitMapper.getTypeId(iBusId);
    }

    @Override
    public BusFunctionPermit findById(Integer iId) {
        BusFunctionPermit busFunctionPermit = busFunctionPermitMapper.findById(iId);

        return busFunctionPermit;
    }
    @CacheEvict(value=CacheConstant.FUNCTION_PERMIT_CACHE, allEntries=true)
    @Override
    public int save1(BusFunctionPermit busFunctionPermit,String schema) {
        int insert = busFunctionPermitMapper.insert(busFunctionPermit);
        return insert;
    }
    @Override
    public IPage<BusFunctionPermit> findAllList(BusFunctionPermit busFunctionPermit, Integer pageNo, Integer pageSize) {
        int total = busFunctionPermitMapper.queryCount(busFunctionPermit);
        List<BusFunctionPermit> functionPermitList =  busFunctionPermitMapper.findAllList((pageNo-1)*pageSize,pageSize,busFunctionPermit);
        for(int i = 0; i < functionPermitList.size() ; i ++){
            BusFunction busFunction = busFunctionMapper.selectByIid(functionPermitList.get(i).getIBusId());
            functionPermitList.get(i).setBusFunctionName(busFunction.getSName());
            if( "1".equals(functionPermitList.get(i).getSPermitType())){      // 角色
                SysRole sysRole = sysRoleMapper.selectById(functionPermitList.get(i).getITypeId());
                if(! (sysRole == null)){
                    functionPermitList.get(i).setItypeName(sysRole.getRoleName());
                }
            }
            else if( "2".equals(functionPermitList.get(i).getSPermitType())){      // 部门

                SysDepart sysDepart = sysDepartMapper.selectById(functionPermitList.get(i).getITypeId());
                if(! (sysDepart == null)){
                    functionPermitList.get(i).setParentName(sysDepartMapper.selectById( sysDepart.getParentId()).getDepartName());
                    functionPermitList.get(i).setItypeName(sysDepart.getDepartName());
                }
            }
            else if("3".equals(functionPermitList.get(i).getSPermitType()) ){      // 人员
                SysUser sysUser = sysUserMapper.selectById(functionPermitList.get(i).getITypeId());
                if(! (sysUser == null)){
                    functionPermitList.get(i).setItypeName(sysUser.getRealname());
                }

            }
        }
        IPage<BusFunctionPermit> pageList = new Page<BusFunctionPermit>();
        pageList.setRecords(functionPermitList);
        pageList.setTotal(total);
        pageList.setSize(pageSize) ;
        pageList.setCurrent(pageNo) ;
        return pageList;
    }

    @Override
    public List<BusFunctionPermit> findListByModelId(String modelid) {
        return busFunctionPermitMapper.findListByModelId(modelid);
    }

    @Override
    public void updateReade(String table,String userId, String functionId, String dataId) {
        busFunctionPermitMapper. updateReade( table,userId,  functionId,  dataId);
    }

    @Override
    public List<String> getroleId(String sysUserId) {
        return busFunctionPermitMapper.getroleId(sysUserId);
    }

    @Override
    public String findTabelName(Integer iBusModelId) {
        return busFunctionPermitMapper.findTabelName(iBusModelId);
    }

    @Override
    public List<String> getParentId(String iTypeId) {
        return busFunctionPermitMapper.getParentId(iTypeId);
    }

    @Override
    public List<SysRole> findRoleByCode() {
        return busFunctionPermitMapper.findRoleByCode();
    }


}
