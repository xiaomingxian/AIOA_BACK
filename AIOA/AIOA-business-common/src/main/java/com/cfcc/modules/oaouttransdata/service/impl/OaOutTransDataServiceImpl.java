package com.cfcc.modules.oaouttransdata.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.entity.BusPageDetail;
import com.cfcc.modules.oaBus.mapper.OaBusdataMapper;
import com.cfcc.modules.oaBus.service.*;
import com.cfcc.modules.oaBus.service.impl.BusPageServiceImpl;
import com.cfcc.modules.oaouttransdata.entity.OaOutTransData;
import com.cfcc.modules.oaouttransdata.mapper.OaOutTransDataMapper;
import com.cfcc.modules.oaouttransdata.service.IOaOutTransDataService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 归档送公文传输
 * @Author: jeecg-boot
 * @Date: 2019-10-18
 * @Version: V1.0
 */
@Service
public class OaOutTransDataServiceImpl extends ServiceImpl<OaOutTransDataMapper, OaOutTransData> implements IOaOutTransDataService {
    @Autowired
    private OaOutTransDataMapper oaOutTransDataMapper;
    @Autowired
    OaBusdataMapper oaBusdataMapper;
    @Autowired
    private IBusPageDetailService busPageDetailService;
    @Autowired
    private IBusFunctionService ibusFunctionService;
    @Autowired
    private IOaBusdataService iOaBusdataService;
    @Autowired
    private OaBusDynamicTableService dynamicTableService;
    @Override
    public void transInsert(List<BusPageDetail> pageDetails, Integer iId) {
        if (pageDetails != null && pageDetails.size() > 0) {
            // oa_busdata~表名
            String busdataTab = pageDetails.get(0).getSBusdataTable();
            // oa_busdata~表中的字段
            String busdataCol = "";
            // oa_out_trans_data表中的字段
            String transCol = "";
            for (BusPageDetail pageDetail : pageDetails) {
                busdataCol += pageDetail.getSTableColumn() + ",";
                transCol += pageDetail.getSSendKey() + ",";
            }
            busdataCol = StringUtils.isBlank(transCol) ? "" : busdataCol.substring(0, busdataCol.length() - 1);
            transCol = StringUtils.isBlank(transCol) ? "" : transCol.substring(0, transCol.length() - 1);
            if (StringUtils.isNotBlank(busdataTab) && StringUtils.isNotBlank(busdataTab) && StringUtils.isNotBlank(busdataTab)) {
                oaOutTransDataMapper.transInsert(busdataTab, busdataCol, transCol, iId);
                oaBusdataMapper.updateSendState(busdataTab, iId);
            }
        }
    }

    @Override
    public Boolean InsertTransData(Map<String, Object> map, String stable, Integer tableid) {
        Boolean flag=true;
        try {
            //根据业务功能id查业务含义数据
            Integer funcationid=(Integer) map.get("i_bus_function_id");
            List<BusPageDetail> busPageDetails= busPageDetailService.getListByFunID(funcationid+"");
            //获取业务名称
            BusFunction busFunction=ibusFunctionService.getOneByFunId(funcationid+"");
            String funcationName=busFunction.getSName();
            Map<String,Object> busDataMap=new HashMap<>();
            //详情业务名
            busDataMap.put("STYPE",funcationName);
            //收发文类型
            if ("oa_busdata10".equals(stable)){
                busDataMap.put("SUPPERTYPE","1");
            }else if ("oa_busdata20".equals(stable)){
                busDataMap.put("SUPPERTYPE","2");
            }
            //文件字号值
            String sfileno="";
            //中间数据状态
            busDataMap.put("SSTATE",'0');
            //遍历业务含义对象
            for (int i=0;i<busPageDetails.size();i++){
                BusPageDetail busPageDetail=busPageDetails.get(i);
                //字段名
                String sTableColumn=busPageDetail.getSTableColumn();
                //公文传输字段值不为空
                if (busPageDetail.getSSendKey() != null){
                    String column= map.get(sTableColumn)+"";
                    //判断字段含义中是否关联字典
                    if (busPageDetail.getSDictId() != null ){
                        String sDictId=busPageDetail.getSDictId();
                        //字典中item_value值
                        String itemValue= map.get(sTableColumn)+"";
                        column= iOaBusdataService.getDictText(sDictId,itemValue);
                    }
                    //主办部门
                    if ("SDEPARTMENT".equals(busPageDetail.getSSendKey())){
                        busDataMap.put("SMAINDEPT",column);
                    }
                    //拟稿时间
                    if ("TCOMPOSE".equals(busPageDetail.getSSendKey())){
                        column=column.substring(0,10).replace("-","/");
                    }
                    //签发时间
                    if ("DISSUEDATE".equals(busPageDetail.getSSendKey())){
                        column=column.substring(0,10).replace("-","/");
                    }
                    //文件字号值
                    if ("SFILENO".equals(busPageDetail.getSSendKey())){
                        sfileno= map.get(sTableColumn)+"";
                        busDataMap.put("SCATEGORY",sfileno.substring(0,sfileno.lastIndexOf("〔")));
                    }
                    busDataMap.put(busPageDetail.getSSendKey(),column);
                }
                //档案系统对应字段值不为空
                if (busPageDetail.getSArchivesKey() != null){
                    String column= map.get(sTableColumn)+"";
                    //判断字段含义中是否关联字典
                    if (busPageDetail.getSDictId() != null ){
                        String sDictId=busPageDetail.getSDictId();
                        //字典中item_value值
                        String itemValue= map.get(sTableColumn)+"";
                        column= iOaBusdataService.getDictText(sDictId,itemValue);
                    }
                    if ("SDEPARTMENT".equals(busPageDetail.getSArchivesKey())){
                        busDataMap.put("SMAINDEPT",column);
                    }
                    if ("TCOMPOSE".equals(busPageDetail.getSArchivesKey())){
                        column=column.substring(0,10).replace("-","/");
                    }
                    if ("DISSUEDATE".equals(busPageDetail.getSArchivesKey())){
                        column=column.substring(0,10).replace("-","/");
                    }
                    //文件字号值
                    if ("SFILENO".equals(busPageDetail.getSArchivesKey())){
                        sfileno= map.get(sTableColumn)+"";
                        busDataMap.put("SCATEGORY",sfileno.substring(0,sfileno.lastIndexOf("〔")));
                    }
                    busDataMap.put(busPageDetail.getSArchivesKey(),column);
                }
            }
            busDataMap.put("table","oa_out_trans_data");
            dynamicTableService.insertData(busDataMap);
        }catch (Error error){
            flag=false;
        }
        return flag;
    }
}