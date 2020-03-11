package com.cfcc.modules.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfcc.modules.system.entity.SysUserDepart;
import com.cfcc.modules.system.entity.sysUserManagedepts;
import com.cfcc.modules.system.mapper.SysUserDepartMapper;
import com.cfcc.modules.system.mapper.SysUserManagedeptsMapper;
import com.cfcc.modules.system.service.ISysUserManagedeptsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 分管领导部门管理
 * @Author: jeecg-boot
 * @Date:   2019-12-28
 * @Version: V1.0
 */
@Service
public class SysUserManagedeptsServiceImpl extends ServiceImpl<SysUserManagedeptsMapper, sysUserManagedepts> implements ISysUserManagedeptsService {

    @Autowired
    private SysUserManagedeptsMapper sysUserManagedeptsMapper;

    @Autowired
    private SysUserDepartMapper sysUserDepartMapper;

    public void deleteListById(List<String> departMapId){
        if (departMapId != null){
            sysUserManagedeptsMapper.deleteListById(departMapId);
        }
    }

    public IPage<Map<String,Object>> getByUserIdAndDeptName(String userId,String deptName){
        IPage<Map<String,Object>> pageList = new Page<Map<String,Object>>();
        List<String> deptIdList = sysUserManagedeptsMapper.getByUserIdAndDeptName(deptName);
        List<Map<String,Object>> userList = sysUserManagedeptsMapper.findByUserIdAndDepartId(userId,deptIdList );
        if (userList.size() >= 1){
            String userName = sysUserManagedeptsMapper.findByUserId(userId);
            for (Map<String, Object> user : userList) {
                String dept_id =  user.get("s_dept_id").toString();
                Map<String,Object> deptList = sysUserManagedeptsMapper.findDepartNameByDeptId(dept_id);
                if (!deptList.get("parentId").equals("1")){
                    String parentName = sysUserManagedeptsMapper.finParentName(deptList.get("parentId")+"");
                    user.put("parentName", parentName);
                }else{
                    user.put("parentName", "");
                }
                user.put("deptName", deptList.get("departName"));
                user.put("userId",userId );
                user.put("username", userName);
            }
            pageList.setRecords(userList);
        }
        return pageList;
    }

    public Boolean removeByIdAndDeptId(String userId,String deptId){
        int i = sysUserManagedeptsMapper.removeByIdAndDeptId(userId,deptId);
        /*if (i != 1){
            return false;
        }*/
        return true;
    }

    @Override
    public IPage<Map<String,Object>> findUserManageDeptsByUserName(String username, Integer pageNo, Integer pageSize){
        IPage<Map<String,Object>> pageList = new Page<Map<String,Object>>();
        List<Map<String,Object>> userList = new ArrayList<>();
        Long total = sysUserManagedeptsMapper.findTotal(username);
        String userId = sysUserManagedeptsMapper.findUserId(username);
        userList = sysUserManagedeptsMapper.findUserManagedepts(userId,(pageNo - 1) * pageSize, pageSize);
        if (userList.size() >= 1){
            for (Map<String, Object> user : userList) {
                String sDeptId = user.get("deptId") + "";
                Map<String,Object> deptList = sysUserManagedeptsMapper.findDepartNameByDeptId(sDeptId);
                if (!deptList.get("parentId").equals("1")){
                    String parentName = sysUserManagedeptsMapper.finParentName(deptList.get("parentId")+"");
                    user.put("parentName", parentName);
                }else{
                    user.put("parentName", "");
                }
                user.put("deptName", deptList.get("departName"));
                user.put("username", username);
            }
        }else {
            Map<String,Object> map = new HashMap<>();
            map.put("userId", userId);
            userList.add(map);
        }
//        List<Map<String,Object>> userList = sysUserManagedeptsMapper.findUserIdByUserName(username,(pageNo - 1) * pageSize, pageSize);
        pageList.setRecords(userList);
        pageList.setTotal(total);
        pageList.setSize(pageSize);
        pageList.setCurrent(pageNo);
        return pageList;
    }

    @Override
    public void deleteUserManageDepartsListByUserId(String id) {
        sysUserManagedeptsMapper.deleteUserManageDepartsListByUserId(id);
    }

    @Override
    public Boolean saveDepartIdByUserId(Map<String,Object> map){
        String userId = map.get("userId") + "";
        List<String> departIdList = (List<String>) map.get("departId");
        for (String departId : departIdList) {
            Long count = sysUserManagedeptsMapper.findDepartIdByUserId(userId,departId);
            if (count >= 1){
                continue;
            }
            sysUserManagedeptsMapper.saveDepartIdByUserId(userId,departId );
        }
        return true;
    }
}
