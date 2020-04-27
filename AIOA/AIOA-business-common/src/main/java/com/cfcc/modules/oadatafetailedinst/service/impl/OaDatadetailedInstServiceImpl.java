package com.cfcc.modules.oadatafetailedinst.service.impl;

import com.cfcc.modules.oaBus.mapper.OaBusDynamicTableMapper;
import com.cfcc.modules.oadatafetailedinst.entity.OaDatadetailedInst;
import com.cfcc.modules.oadatafetailedinst.mapper.OaDatadetailedInstMapper;
import com.cfcc.modules.oadatafetailedinst.service.IOaDatadetailedInstService;
import com.cfcc.modules.system.entity.SysDepart;
import com.cfcc.modules.workflow.mapper.DepartWithTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Date;
import java.util.HashMap;
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

    @Autowired
    private DepartWithTaskMapper departWithTaskMapper;

    @Override
    public Map<String, Object> findByTableId(Integer iTableId,String sCreateName) {
        return oaDatadetailedInstMapper.findByTableId(iTableId,sCreateName);
    }
    @Override
    public int addorupdataDetailed( OaDatadetailedInst OaDatadetailedInst) {
        oaDatadetailedInstMapper.insertDataInst(OaDatadetailedInst);
        int id=OaDatadetailedInst.getIId();
        return id;
    }

    @Override
    public List<OaDatadetailedInst> seletdetailedInstList(String sTable,Integer iTableId,String sCreateBy, String sCreateDeptid) {
        List<OaDatadetailedInst> list= oaDatadetailedInstMapper.seletdetailedInstList(sTable,iTableId,sCreateBy,sCreateDeptid);
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
    public  List<Map<String, Object>> findPret(String parentId) {
        List<Map<String, Object>> depart = oaDatadetailedInstMapper.getDept(parentId);
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

    @Override
    public boolean updatadetailedInst(Map<String, Object> map) {
        map.put("table","oa_datadetailed_inst");
        Date date=new Date();
        map.put("d_create_time",date);
        if ((int)map.get("cmd")==1){
            map.put("i_is_display",1);
        }
        map.remove("cmd");
        int num=dynamicTableMapper.updateData(map);
        boolean flag=false;
        if (num!=0){
            flag=true;
        }
        return flag;
    }

    @Override
    public int deteledetailedInst(Integer iId) {
        int num=oaDatadetailedInstMapper.deteledetailedInst(iId);
        return num;
    }

    @Override
    public int addDetailed(Map<String, Object> map) {
        map.put("table","oa_datadetailed_inst");
        Date date=new Date();
        map.put("d_create_time",date);
        map.put("i_is_display",0);
        dynamicTableMapper.insertData(map);
        Number number= (Number) map.get("i_id");
        return number.intValue();
    }

    @Override
    public boolean updataDetailedIsStats(Integer iId,Integer num) {
        Map<String, Object> map = new HashMap<>();
        map.put("table","oa_datadetailed_inst");
        map.put("i_id",iId);
        if (num==0){
            map.put("i_is_2",0);
        }else {
            map.put("i_is_2",1);
        }
        int nums=dynamicTableMapper.updateData(map);
        boolean flag=true;
        if (nums==0){
            flag=false;
        }
        return flag;
    }

    @Override
    public List<String> findFunctionIds(Integer modelId) {
        return oaDatadetailedInstMapper.findFunctionIds(modelId);
    }

    @Override
    public Map<String, Object> lineLeaderRate(String table, Integer busModelId ,Integer busFunctionId,String parentId) {
        return oaDatadetailedInstMapper.lineLeaderRate(table,busModelId,busFunctionId,parentId);
    }

    @Override
    public Map<String, Object> Rate(String table, Integer busModelId,String parentId) {
        return oaDatadetailedInstMapper.rate(table,busModelId,parentId);
    }

    @Override
    public String  findById(Integer departId) {
        return oaDatadetailedInstMapper.findByid(departId);
    }

    @Override
    public Map<String, Integer> deptDone(List<String> fids) {
        List<String> procInsntIds=null;
        if (fids.size() > 0) {
            procInsntIds = departWithTaskMapper.queryProcInstIdsByFunction(fids);
        }
        return oaDatadetailedInstMapper.deptDone(procInsntIds);
    }

    @Override
    public List<Map<String, Object>> findExtensionsNum(Integer busModelId, String table,String parentId) {
        return oaDatadetailedInstMapper.findExtensionsNum(busModelId,table,parentId);
    }

    @Override
    public Map<String, Object> findFinish(Integer year, String busFunctionId, String modelId, String parentId,String important) {
        return oaDatadetailedInstMapper.findFinish(year,busFunctionId,modelId,parentId,important);
    }

    @Override
    public Map<String, Object> findInprocess(Integer year, String busFunctionId, String modelId, String parentId,String important) {
        return oaDatadetailedInstMapper.findInprocess(year,busFunctionId,modelId,parentId,important);
    }

    @Override
    public Map<String, Object> findOverdue(Integer year, String busFunctionId, String modelId, String parentId,String important) {
        return oaDatadetailedInstMapper.findOverdue(year,busFunctionId,modelId,parentId,important);
    }

    @Override
    public Map<String,Object> findOverDay(String modelId, String parentId,String state,String day1,String day2) {
        return oaDatadetailedInstMapper.findOverDay(modelId,parentId,state,day1,day2);
    }

    @Override
    public Map<String,Object> findAdvanceDay(String modelId, String parentId,String state,String day1,String day2) {
        return oaDatadetailedInstMapper.findAdvanceDay(modelId,parentId,state,day1,day2);
    }
}
