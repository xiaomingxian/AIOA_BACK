package com.cfcc.modules.oadatafetailedinst.service;

import com.cfcc.modules.oadatafetailedinst.entity.OaDatadetailedInst;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.system.entity.SysDepart;

import java.util.List;
import java.util.Map;

/**
 * @Description: 明细存储
 * @Author: jeecg-boot
 * @Date:   2020-04-16
 * @Version: V1.0
 */
public interface IOaDatadetailedInstService extends IService<OaDatadetailedInst> {

    Map<String, Object> findByTableId(Integer iTableId,String sCreateName);

    int addorupdataDetailed( OaDatadetailedInst OaDatadetailedInst);

    List<OaDatadetailedInst> seletdetailedInstList(String sTable,Integer iTableId,String sCreateBy, String sCreateDeptid);

    List<OaDatadetailedInst> seletSharedetailedInstList(String sTable, Integer iTableId);

    List<OaDatadetailedInst> seletAlldetailedInstList(String sTable, Integer iTableId, String sCreateBy, String sCreateDeptid);

    Integer findOpions(Integer tableid, String userName);

    Integer findIsFile(Integer tableid,String userName,String deptName);

    //Integer findIsFile(Integer tableid);

    Map<String, Object> findDate(Integer tableid);

    Integer getBanjieBydept(String itableId);

    Integer getDateCount(String createTime, String dateTime,String sCreateName);

    List<Map<String, Object>> findTypeNum(String table,String userId,int year,String parentId);

    List<Map<String, Object>> findPret(String parentId);

    List<Map<String, Object>> findorganizeNum(String table, String userId, int year, String parentId);

    boolean updatadetailedInst(Map<String, Object> map);

    int deteledetailedInst(Integer iId);

    int addDetailed(Map<String, Object> map);

    boolean updataDetailedIsStats(Integer iId,Integer num);

    List<String> findFunctionIds(Integer modelId);

    Map<String, Object> lineLeaderRate(String table, Integer busModelId,Integer busFunctionId,String parentId);

    Map<String, Object> Rate(String table, Integer busModelId,String parentId);

    String  findById(Integer departId);

    Map<String, Integer> deptDone(List<String> fids);

    List<Map<String, Object>> findExtensionsNum(Integer busModelId, String table,String parentId);

    Map<String, Object> findFinish(Integer year, String busFunctionId, String modelId, String parentId,String important);

    Map<String, Object> findInprocess(Integer year, String busFunctionId, String modelId, String parentId,String important);

    Map<String, Object> findOverdue(Integer year, String busFunctionId, String modelId, String parentId,String important);

    Map<String,Object> findOverDay(String modelId, String parentId, String state,String day1,String day2);

    Map<String,Object> findAdvanceDay(String modelId, String parentId, String state,String day1,String day2);
}
