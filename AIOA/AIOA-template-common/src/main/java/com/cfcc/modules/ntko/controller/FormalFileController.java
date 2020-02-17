package com.cfcc.modules.ntko.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.util.FileUtils;
import com.cfcc.modules.docnum.entity.DocNumManage;
import com.cfcc.modules.ntko.service.DocumentMangeService;
import com.cfcc.modules.oaBus.entity.BusPageDetail;
import com.cfcc.modules.oaBus.entity.OaBusdata;
import com.cfcc.modules.oaBus.entity.OaFile;
import com.cfcc.modules.oaBus.service.IBusPageDetailService;
import com.cfcc.modules.oaBus.service.IOaBusdataService;
import com.cfcc.modules.oaBus.service.IOaFileService;
import com.cfcc.modules.oaBus.service.OaBusDynamicTableService;
import com.cfcc.modules.papertitle.entity.OaTemplate;
import com.cfcc.modules.docnum.service.IDocNumSetService;
import com.cfcc.modules.papertitle.service.IOaTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.management.RuntimeErrorException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/ntko/formalfile")
public class FormalFileController {

    @Autowired
    private DocumentMangeService documentMangeService;

    @Autowired
    private IDocNumSetService iDocNumSetService;

    @Autowired
    private IOaTemplateService iOaTemplateService;

    @Autowired
    private IOaFileService iOaFileService;

    @Autowired
    private IOaBusdataService iOaBusdataService;

    @Autowired
    private IOaFileService oaFileService;

    @Autowired
    private IBusPageDetailService busPageDetailService;

    @Autowired
    private IOaBusdataService oaBusdataService;

    @Autowired
    private OaBusDynamicTableService oaBusDynamicTableService;

    /**
     * 根据文号编号的id查询模板信息及业务数据信息
     *
     * @param id       文号id
     * @param stable   业务详情表名
     * @param tableid  业务详情数据表id
     * @param fileType 文件类型
     * @return
     */

    @GetMapping(value = "/queryFormalFileById")
    public Result<Map> queryFormalFileById(@RequestParam(name = "id", required = true) String id,
                                           @RequestParam(name = "stable", required = true) String stable,
                                           @RequestParam(value = "tableid", required = true) String tableid,
                                           @RequestParam(value = "fileType", required = true) String fileType) {
        Result<Map> result = new Result<Map>();
        try {
            //获取文件名
            OaFile oaFile = new OaFile();
            oaFile.setSTable(stable);
            oaFile.setITableId(Integer.parseInt(tableid));
            oaFile.setSFileType(fileType);
            QueryWrapper<OaFile> c = new QueryWrapper<>();
            c.setEntity(oaFile);
            OaFile ad = oaFileService.getOne(c);
            String zwFileName = ad.getSFileName();
            //根据文号id查模板名
            DocNumManage docNumManage = documentMangeService.queryById(Integer.parseInt(id));
            Integer tmplateId = iDocNumSetService.queryByIdAndSendObj(docNumManage);
            OaTemplate oaTemplate = iOaTemplateService.queryById(tmplateId);
            oaFile = iOaFileService.queryById(oaTemplate.getIFileId());
            String fileName = oaFile.getSFileName();
            //根据业务表名和id查业务数据
            Map<String, Object> map = iOaBusdataService.getBusDataById(stable, Integer.parseInt(tableid));
            //根据业务功能id查业务含义数据
            Integer funcationid = (Integer) map.get("i_bus_function_id");
            //修改是否排版
            if (map.size() != 0) {
                OaBusdata oaBusdata = new OaBusdata();
                oaBusdata.setIId(Integer.parseInt(tableid));
                oaBusdata.setIIsTypeset(0);
                oaBusdataService.updateById(oaBusdata);
            }
            List<BusPageDetail> busPageDetails = busPageDetailService.getListByFunID(funcationid + "");
            //遍历业务含义对象
            for (int i = 0; i < busPageDetails.size(); i++) {
                BusPageDetail busPageDetail = busPageDetails.get(i);
                //字段名
                String sTableColumn = busPageDetail.getSTableColumn();
                //判断字段含义中是否关联字典
                if (busPageDetail.getSDictId() != null) {
                    String sDictId = busPageDetail.getSDictId();
                    //字典中item_value值
                    String itemValue = map.get(sTableColumn) + "";
                    String iSafetyLevelTest = iOaBusdataService.getDictText(sDictId, itemValue);
                    map.put(sTableColumn, iSafetyLevelTest);
                }
                //判断自定义含义对应word书签字段
                if (busPageDetail.getSMarkKey() != null) {
                    String value = iOaBusdataService.getBusdataValueByIdAndFiled(stable, Integer.parseInt(tableid), sTableColumn);
                    if (value != null) {
                        map.put(busPageDetail.getSMarkKey(), value);
                    }
                }
            }
            Calendar calendar = Calendar.getInstance();
            map.put("i_now_year", calendar.get(Calendar.YEAR));
            map.put("i_now_month", calendar.get(Calendar.MONTH));
            map.put("i_now_day", calendar.get(Calendar.DATE));
            String nowTime = calendar.get(Calendar.YEAR) + "年" + calendar.get(Calendar.MONTH + 1) + "月" + calendar.get(Calendar.DATE) + "日";
            map.put("CurDate", nowTime);
            map.put("fileName", fileName);
            map.put("zwFileName", zwFileName);
            result.setResult(map);
        } catch (RuntimeErrorException e) {
            log.error("系统正在升级，请联系管理员");
        }
        return result;
    }

    /**
     * 保存办文单
     *
     * @param id      文号管理id
     * @param stable  业务表名
     * @param tableid 业务id
     * @return
     */
    @GetMapping(value = "/queryById")
    public Result<Map> queryById(@RequestParam(name = "id", required = true) Integer id,
                                 @RequestParam(name = "stable", required = true) String stable,
                                 @RequestParam(value = "tableid", required = true) Integer tableid) {
        Result<Map> result = new Result<Map>();
        try {
            DocNumManage docNumManage = documentMangeService.queryById(id);
            Integer tmplateId = iDocNumSetService.queryByTemplateId(docNumManage);
            OaTemplate oaTemplate = iOaTemplateService.queryById(tmplateId);
            OaFile oaFile = iOaFileService.queryById(oaTemplate.getIFileId());
            String fileName = oaFile.getSFileName();
            Map<String, Object> map = iOaBusdataService.getBusDataById(stable, tableid);
            //修改是否保存办文单
            if (map.size() != 0) {
                OaBusdata oaBusdata = new OaBusdata();
                oaBusdata.setIId(tableid);
                oaBusdata.setIIsApprove(0);
                oaBusdataService.updateById(oaBusdata);

            }
            //查询附件列表中的文件名
            List<OaFile> oaFileList = iOaFileService.getOaFileList(stable, Integer.toString(tableid));
            String AccessoryFileName = "";
            for (int i = 0; i < oaFileList.size(); i++) {
                if ("7".equals(oaFileList.get(i))) {
                    AccessoryFileName += oaFileList.get(i).getSFileName().lastIndexOf(".", 1) + "\n";
                }
            }
            map.put("TAttachment", AccessoryFileName);
            //根据业务功能id查业务含义数据
            Integer funcationid = (Integer) map.get("i_bus_function_id");
            Map<String, Object> OptionMap = oaBusDynamicTableService.queryOptionsByBusDataIdAndFuncationId(stable, tableid, funcationid);
            //判断意见类型并添加书签map中
            for (int i = 0; i < OptionMap.size(); i++) {
                int j = i + 1;
                if (OptionMap.get("opinion" + j) != "") {
                    map.put("opinion" + j, OptionMap.get("opinion" + j));
                }
            }
            List<BusPageDetail> busPageDetails = busPageDetailService.getListByFunID(funcationid + "");
            //遍历业务含义对象
            for (int i = 0; i < busPageDetails.size(); i++) {
                BusPageDetail busPageDetail = busPageDetails.get(i);
                //字段名
                String sTableColumn = busPageDetail.getSTableColumn();
                //判断字段含义中是否关联字典
                if (busPageDetail.getSDictId() != null) {
                    String sDictId = busPageDetail.getSDictId();
                    //字典中item_value值
                    String itemValue = map.get(sTableColumn) + "";
                    String iSafetyLevelTest = iOaBusdataService.getDictText(sDictId, itemValue);
                    map.put(sTableColumn, iSafetyLevelTest);
                }
                //判断自定义含义对应word书签字段
                if (busPageDetail.getSMarkKey() != null) {
                    String value = iOaBusdataService.getBusdataValueByIdAndFiled(stable, tableid, sTableColumn);
                    map.put(busPageDetail.getSMarkKey(), value);
                }
            }
            Calendar calendar = Calendar.getInstance();
            map.put("i_now_year", calendar.get(Calendar.YEAR));
            map.put("i_now_month", calendar.get(Calendar.MONTH));
            map.put("i_now_day", calendar.get(Calendar.DATE));
            String nowTime = calendar.get(Calendar.YEAR) + "年" + calendar.get(Calendar.MONTH + 1) + "月" + calendar.get(Calendar.DATE) + "日";
            map.put("CurDate", nowTime);
            map.put("fileName", fileName);
            result.setResult(map);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("办文单业务文件名查询失败");
        }
        return result;
    }


    /**
     * 查询业务数据中办文单，排版状态
     *
     * @param stable  业务表名
     * @param tableid 业务id
     * @return
     */
    @GetMapping(value = "/queryStateById")
    public Result<Map> queryStateById(@RequestParam(name = "stable", required = true) String stable,
                                      @RequestParam(value = "tableid", required = true) Integer tableid) {
        Result<Map> result = new Result<Map>();
        Map<String, Object> map = iOaBusdataService.queryStateById(stable, tableid);
        if (map.size() == 0) {
            result.setSuccess(false);
        } else {
            result.setSuccess(true);
            result.setResult(map);
        }
        return result;

    }
}
