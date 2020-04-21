package com.cfcc.modules.oadatafetailedinst.service.impl;

import com.cfcc.modules.oaBus.mapper.OaBusDynamicTableMapper;
import com.cfcc.modules.oadatafetailedinst.entity.OaDatadetailedInst;
import com.cfcc.modules.oadatafetailedinst.mapper.OaDatadetailedInstMapper;
import com.cfcc.modules.oadatafetailedinst.service.IOaDatadetailedInstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: 明细存储
 * @Author: jeecg-boot
 * @Date:   2020-04-16
 * @Version: V1.0
 */
@Service
public class OaDatadetailedInstServiceImpl extends ServiceImpl<OaDatadetailedInstMapper, OaDatadetailedInst> implements IOaDatadetailedInstService {
    @Autowired
    private OaDatadetailedInstMapper oaDatadetailedInstMapper;

    @Autowired
    private OaBusDynamicTableMapper dynamicTableMapper;

    @Override
    public Map<String, Object> findByTableId(Integer iTableId,String sCreateName) {
        return oaDatadetailedInstMapper.findByTableId(iTableId,sCreateName);
    }
    @Override
    public int addorupdataDetailed( Map<String,Object> map) {

//        int num=oaDatadetailedInstMapper.insert(oaDatadetailedInst);
        map.put("table","oa_datadetailed_inst");
        Date date=new Date();
        map.put("d_create_time",date);
        map.put("d_datetime1",date);
        int num=dynamicTableMapper.insertData(map);
        return num;
    }

    @Override
    public List<OaDatadetailedInst> seletdetailedInstList(String sCreateBy, String sCreateDeptid) {
        List<OaDatadetailedInst> list= oaDatadetailedInstMapper.seletdetailedInstList(sCreateBy,sCreateDeptid);
        return list;
    }

    @Override
    public List<OaDatadetailedInst> seletSharedetailedInstList(String sTable, Integer iTableId) {
        List<OaDatadetailedInst> list= oaDatadetailedInstMapper.seletSharedetailedInstList(sTable,iTableId);
        return list;
    }

    @Override
    public List<OaDatadetailedInst> seletAlldetailedInstList(String sTable, Integer iTableId, String sCreateBy, String sCreateDeptid) {
        List<OaDatadetailedInst> list= oaDatadetailedInstMapper.seletAlldetailedInstList(sTable,iTableId,sCreateBy,sCreateDeptid);
        return list;
    }

    @Override
    public Integer findOpions(Integer tableid, String userName) {
        Integer opions = oaDatadetailedInstMapper.findOpions(tableid, userName);
        if(opions == null){
            return 0 ;
        }
        return opions;
    }


    @Override
    public Integer findIsFile(Integer tableid,String userName,String deptName) {
        String tableName = "oa_busdata11";
        Integer isFile = oaDatadetailedInstMapper.findIsFile(tableName,tableid,userName,deptName);
        if(isFile == null){
            return  0;
        }
        return isFile;
    }

    @Override
    public Map<String, Object> findDate(Integer tableid) {
        return oaDatadetailedInstMapper.findDate(tableid);
    }

    @Override
    public Integer getBanjieBydept(String itableId) {
        Integer banjieBydept = oaDatadetailedInstMapper.getBanjieBydept(itableId);
        if(banjieBydept == null ){
            return  0;
        }
        return banjieBydept;
    }

    @Override
    public Integer getDateCount(String createTime, String dateTime,String sCreateName) {
        return oaDatadetailedInstMapper.getDateCount(createTime,dateTime,sCreateName);
    }

    @Override
    public Map<String, Object> findPret(String parentId) {
        Map<String,Object>  depart = oaDatadetailedInstMapper.getDept(parentId);
        return depart;
    }

    @Override
    public List<Map<String, Object>> findorganizeNum(String table, String userId, int year, String parentId) {
        List<Map<String, Object>> organizeNum = oaDatadetailedInstMapper.findorganizeNum(table,userId,year,parentId);
        return organizeNum;
    }

    @Override
    public List<Map<String, Object>> findTypeNum(String table,String userId,int year,String parentId) {
        List<Map<String, Object>> hostNum = oaDatadetailedInstMapper.findTypeNum(table,userId,year,parentId);
       return hostNum;
    }
}
