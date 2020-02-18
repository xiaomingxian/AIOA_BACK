package com.cfcc.modules.oaBus.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfcc.common.constant.CacheConstant;
import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.entity.BusModel;
import com.cfcc.modules.oaBus.entity.BusModelPermit;
import com.cfcc.modules.oaBus.mapper.BusModelMapper;
import com.cfcc.modules.oaBus.mapper.BusModelPermitMapper;
import com.cfcc.modules.oaBus.service.IBusModelPermitService;
import com.cfcc.modules.system.entity.SysDepart;
import com.cfcc.modules.system.entity.SysRole;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.system.mapper.SysDepartMapper;
import com.cfcc.modules.system.mapper.SysRoleMapper;
import com.cfcc.modules.system.mapper.SysUserMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 业务模板
 * @Author: jeecg-boot
 * @Date:   2019-10-21
 * @Version: V1.0
 */
@Service
public class BusModelPermitServiceImpl extends ServiceImpl<BusModelPermitMapper, BusModelPermit> implements IBusModelPermitService {

    @Autowired
    BusModelPermitMapper busModelPermitMapper;
    @Autowired
    BusModelMapper busModelMapper;
    @Autowired
    SysRoleMapper sysRoleMapper ;
    @Autowired
    SysDepartMapper sysDepartMapper ;
    @Autowired
    SysUserMapper sysUserMapper ;
    
    @Override
    @CacheEvict(value=CacheConstant.MODEL_PERMIT_CACHE, allEntries=true)
    public boolean updateBYIid(BusModelPermit busModelPermit,String schema) {
        try {
            busModelPermitMapper.updateByIid(busModelPermit);
            return true;

        }catch (Exception e){
            return false;
        }
    }

    @CacheEvict(value=CacheConstant.MODEL_PERMIT_CACHE, allEntries=true)
    public boolean save(BusModelPermit busModelPermit,String schema) {
        return super.save(busModelPermit);
    }

    @Override
    @CacheEvict(value=CacheConstant.MODEL_PERMIT_CACHE, allEntries=true)
    public boolean deleteById(String id,String schema) {
        try {
            busModelPermitMapper.deleteById(id);
            return true;

        }catch (Exception e){
            return false;
        }
    }

    @Override
    public BusModelPermit findById(Integer iId) {

        BusModelPermit busModelPermit = busModelPermitMapper.findById(iId);

        return busModelPermit;
    }

    @Override
    public IPage<BusModelPermit> findPage(Integer ibusmodelId, Integer pageNo, Integer pageSize) {
        BusModelPermit busModelPermit=new BusModelPermit();
        BeanUtils.copyProperties(busModelPermit,busModelPermit);
        int total = busModelPermitMapper.count(ibusmodelId);
        List<BusModelPermit> modelPermitList =  busModelPermitMapper.findPage((pageNo-1)*pageSize,pageSize,ibusmodelId);
        for(int i = 0; i < modelPermitList.size() ; i ++){
            BusModel busModel = busModelMapper.selectById(modelPermitList.get(i).getIBusModelId());
            modelPermitList.get(i).setBusModelName(busModel.getSName());
            if( "1".equals(modelPermitList.get(i).getSPermitType())){      // 角色
                SysRole sysRole = sysRoleMapper.selectById(modelPermitList.get(i).getITypeId());
                if(! (sysRole == null)){
                    modelPermitList.get(i).setItypeName(sysRole.getRoleName());
                }
            }
            else if( "2".equals(modelPermitList.get(i).getSPermitType())){      // 部门
                SysDepart sysDepart = sysDepartMapper.selectById(modelPermitList.get(i).getITypeId());

                if(! (sysDepart == null)){
                    modelPermitList.get(i).setItypeName(sysDepart.getDepartName());
                    modelPermitList.get(i).setParentName(sysDepartMapper.selectById( sysDepart.getParentId()).getDepartName());
                }
            }
            else if("3".equals(modelPermitList.get(i).getSPermitType()) ){      // 人员
                SysUser sysUser = sysUserMapper.selectById(modelPermitList.get(i).getITypeId());
                if(! (sysUser == null)){
                    modelPermitList.get(i).setItypeName(sysUser.getRealname());
                }

            }
        }
        IPage<BusModelPermit> pageList = new Page<BusModelPermit>();
        pageList.setRecords(modelPermitList);
        pageList.setTotal(total);
        pageList.setSize(pageSize) ;
        pageList.setCurrent(pageNo) ;
        return pageList;
    }

    @Override
    @Cacheable(value = CacheConstant.MODEL_PERMIT_CACHE)
    public List<BusModelPermit> findList(String schema) {

        return busModelPermitMapper.findList();
    }

    @Override
    public List<String> getTypeId(Integer modelId) {
        return busModelPermitMapper.getTypeId(modelId);
    }

    @Override
    public IPage<BusModelPermit> findAllList(BusModelPermit busModelPermit, Integer pageNo, Integer pageSize) {
        int total = busModelPermitMapper.queryCount(busModelPermit);
        List<BusModelPermit> busModelPermitList =  busModelPermitMapper.findAllList((pageNo-1)*pageSize,pageSize,busModelPermit);
        for(int i = 0; i < busModelPermitList.size() ; i ++){
            BusModel busModel = busModelMapper.selectById(busModelPermitList.get(i).getIBusModelId());
            busModelPermitList.get(i).setBusModelName(busModel.getSName());
            if( "1".equals(busModelPermitList.get(i).getSPermitType())){      // 角色
                SysRole sysRole = sysRoleMapper.selectById(busModelPermitList.get(i).getITypeId());
                if(! (sysRole == null)){
                    busModelPermitList.get(i).setItypeName(sysRole.getRoleName());
                }
            }
            else if( "2".equals(busModelPermitList.get(i).getSPermitType())){      // 部门
                SysDepart sysDepart = sysDepartMapper.selectById(busModelPermitList.get(i).getITypeId());
                if(! (sysDepart == null)){
                    busModelPermitList.get(i).setParentName(sysDepartMapper.selectById( sysDepart.getParentId()).getDepartName());
                    busModelPermitList.get(i).setItypeName(sysDepart.getDepartName());
                }
            }
            else if("3".equals(busModelPermitList.get(i).getSPermitType()) ){      // 人员
                SysUser sysUser = sysUserMapper.selectById(busModelPermitList.get(i).getITypeId());
                if(! (sysUser == null)){
                    busModelPermitList.get(i).setItypeName(sysUser.getRealname());
                }

            }
        }
        IPage<BusModelPermit> pageList = new Page<BusModelPermit>();
        pageList.setRecords(busModelPermitList);
        pageList.setTotal(total);
        pageList.setSize(pageSize) ;
        pageList.setCurrent(pageNo) ;
        return pageList;
    }


}
