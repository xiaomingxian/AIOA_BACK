package com.cfcc.modules.oadatafetailedinst.service;

import com.cfcc.modules.oadatafetailedinst.entity.OaDatadetailedInst;
import com.baomidou.mybatisplus.extension.service.IService;

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

    Map<String, Object> findPret(String parentId);

    List<Map<String, Object>> findorganizeNum(String table, String userId, int year, String parentId);

    boolean updatadetailedInst(Map<String, Object> map);

    int deteledetailedInst(Integer iId);

    int addDetailed(Map<String, Object> map);

    boolean updataDetailedIsStats(Integer iId);

    List<String> findFunctionIds(Integer modelId);

    Map<String, Object> lineLeaderRate(String table, Integer busModelId,Integer busFunctionId);

    Map<String, Object> Rate(String table, Integer busModelId);
}
