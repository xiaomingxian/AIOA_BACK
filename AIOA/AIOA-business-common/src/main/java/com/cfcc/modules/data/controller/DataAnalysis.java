package com.cfcc.modules.data.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.common.ExcelUtil.ExcelUtil;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.common.system.query.QueryGenerator;
import com.cfcc.common.util.oConvertUtils;
import com.cfcc.modules.data.service.DataAnalysisService;
import com.cfcc.modules.oaBus.entity.*;
import com.cfcc.modules.oaBus.service.IOaBusdataService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Api(tags = "数据分析")
@RestController
@RequestMapping("/data/dataAnalysis")
public class DataAnalysis {

    @Autowired
    private DataAnalysisService dataAnalysisService;

    @Autowired
    private IOaBusdataService iOaBusdataService;
    @Value("${system.runDate}")
    private String runDate;

    /**
     * 查询年份
     *
     * @return
     */
    @AutoLog(value = "查询年份")
    @GetMapping(value = "/analysis")
    @ResponseBody
    public Map<String, Object> queryFunSelByModelId() {
        Map<String, Object> result = new HashMap<>();
        int year = LocalDate.now().getYear();
        int res = year - Integer.parseInt(runDate);
        List<Map<String,String>> list = new ArrayList<>();
        for (int i = 0; i <= res; i++) {
            Map<String,String> map = new HashMap<>();
            map.put("year",(Integer.parseInt(runDate) + i) + "");
            list.add(map);
        }
        Collections.reverse(list);

        result.put("year", list);
        return result;
    }

    @AutoLog(value = "我的数据")
    @GetMapping(value = "/myAnalysis")
    @ResponseBody
    public List<Map<String, Object>> myAnalysis(OaBusdata oaBusdata, @RequestParam(name = "modelId", required = false) Integer modelId) {
        String table = iOaBusdataService.queryTableName(modelId);
        List<Map<String, Object>> sortList = null;
        List<Map<String, Object>> byTableAndMy = dataAnalysisService.findByTableAndMy(table, oaBusdata);
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            Boolean b = true;
            for (int j = 0; j < byTableAndMy.size(); j++) {
                if (i == (Integer) byTableAndMy.get(j).get("i_create_month")) {
                    b = false;
                }
            }
            if (b) {
                list.add(i);
            }
        }
        list.forEach(i -> {
            Map<String, Object> map = new HashMap<>();
            map.put("i_create_month", i);
            map.put("num", 0);
            byTableAndMy.add(map);
        });
        sortList = byTableAndMy.stream()
                .sorted((m1, m2) -> {
                    if ((Integer) m1.get("i_create_month") >= (Integer) m2.get("i_create_month")) {
                        return 1;
                    } else {
                        return -1;
                    }
                })
                .collect(Collectors.toList());
        return sortList;
    }

    @AutoLog(value = "办结率")
    @GetMapping(value = "/Rate")
    @ResponseBody
    public List<Map<String, Object>> MyRate(OaBusdata oaBusdata, @RequestParam(name = "modelId", required = false) Integer modelId) {
        String table = iOaBusdataService.queryTableName(modelId);
        List<Map<String, Object>> rate = dataAnalysisService.MyRate(table, oaBusdata);
        if(rate == null){
            Map<String,Object> map = new HashMap<>() ;
            map.put("rate",0);
            rate.add(map);
        }
        return rate;
    }
    @AutoLog(value = "超过平均值的月份")
    @GetMapping(value = "/MonthAverage")
    @ResponseBody
    public List<Map<String, Object>> MonthAverage(OaBusdata oaBusdata, @RequestParam(name = "modelId", required = false) Integer modelId) {
        String table = iOaBusdataService.queryTableName(modelId);
        List<Map<String,Object>> tableList=new ArrayList<>();
        List<Map<String, Object>> sortList = null;
        List<Map<String, Object>> byTableAndMy = dataAnalysisService.findByTableAndMy(table, oaBusdata);
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            Boolean b = true;
            for (int j = 0; j < byTableAndMy.size(); j++) {
                if (i == (Integer) byTableAndMy.get(j).get("i_create_month")) {
                    b = false;
                }
            }
            if (b) {
                list.add(i);
            }
        }
        list.forEach(i -> {
            Map<String, Object> map = new HashMap<>();
            map.put("i_create_month", i);
            map.put("num", 0);
            byTableAndMy.add(map);
        });
        sortList = byTableAndMy.stream()
                .sorted((m1, m2) -> {
                    if ((Integer) m1.get("i_create_month") >= (Integer) m2.get("i_create_month")) {
                        return 1;
                    } else {
                        return -1;
                    }
                })
                .collect(Collectors.toList());
        double avg= dataAnalysisService.getAvg(table);
        for(int i=1;i<sortList.size();i++){
           Long month = Long.valueOf(sortList.get(i).get("num").toString());
            if(month>=avg){
                Map<String, Object> map = new HashMap<>();
                map.put("i_create_month", i+1);
                map.put("num", month);
                tableList.add(map);
            }else{
                continue;
            }
        }

        return tableList;
    }
    @AutoLog(value = "同行办理数量的百分比")
    @GetMapping(value = "/PeerNum")
    @ResponseBody
    public List<Map<String, Object>> PeerNum(OaBusdata oaBusdata, @RequestParam(name = "modelId", required = false) Integer modelId) {
        String table = iOaBusdataService.queryTableName(modelId);
        List<Map<String, Object>> rate = dataAnalysisService.PeerNum(table, oaBusdata);
      /*  List<Object> list1= new ArrayList();
        for(int i=0;i<rate.size();i++){
            list1.add( rate.get(i));
        }*/
        if(rate == null){
            Map<String,Object> map = new HashMap<>() ;
            map.put("year",oaBusdata.getICreateYear());
            map.put("rate",0);
            rate.add(map);
        }
        return rate;
    }
    @AutoLog(value = "办理率")
    @GetMapping(value = "/HandlingRate")
    @ResponseBody
    public List<Map<String, Object>> HandlingRate(OaBusdata oaBusdata, @RequestParam(name = "modelId", required = false) Integer modelId) {
        String table = iOaBusdataService.queryTableName(modelId);
        List<Map<String, Object>> rate = dataAnalysisService.HandlingRate(table, oaBusdata);
        if(rate == null){
            Map<String,Object> map = new HashMap<>() ;
            map.put("year",oaBusdata.getICreateYear());
            map.put("rate",0);
            rate.add(map);
        }else{
            Map<String,Object> map = new HashMap<>() ;
            map.put("year",oaBusdata.getICreateYear());
            rate.add(map);
        }

        return rate;
    }

    @AutoLog(value = "总共的公文数")
    @GetMapping(value = "/Handling")
    @ResponseBody
    public Map<String, Object> Handling(OaBusdata oaBusdata, @RequestParam(name = "modelId", required = false) Integer modelId) {
        String table = iOaBusdataService.queryTableName(modelId);
        Map<String, Object> rate = dataAnalysisService.Handling(table, oaBusdata);
        if(rate == null){
            rate.put("year",oaBusdata.getICreateYear());
            rate.put("rate",0);
        }
        return rate;
    }


    @AutoLog(value = "我的主办部门")
    @GetMapping(value = "/MyDepart")
    public List<Map<String, Object>> MyDepart(OaBusdata oaBusdata, @RequestParam(name = "modelId", required = false) Integer modelId) {
        String table = iOaBusdataService.queryTableName(modelId);
        List<Map<String, Object>> selectColums = dataAnalysisService.selectColums(table);
        List<Map<String, Object>> DepartList = null;
        for (Map<String, Object> map : selectColums) {
            Set set = map.keySet();//所有字段的名
            if (set.contains("s_main_dept_names")) {
                // List<Map<String, Object>> DepartList= iOaBusdataService.selectMyDepart((String) map.get("s_main_dept_names"));
                DepartList = dataAnalysisService.selectDeptNames(table, oaBusdata);

            } else {
                DepartList = dataAnalysisService.selectMyCreateDepart(table, oaBusdata);


            }
        }
        return DepartList;
    }


//    @RequestMapping(value = "/exportXls")
//    public ModelAndView exportXls(HttpServletRequest request, @RequestParam(name = "modelId", required = false)Integer modelId, Integer type) {
//        String table = iOaBusdataService.queryTableName(modelId);
//        List<OaBusdata> lisyo= new ArrayList<>();
//        // Step.1 组装查询条件
//        QueryWrapper<OaBusdata> queryWrapper = null;
//        try {
//            String paramsStr = request.getParameter("paramsStr");
//            if (oConvertUtils.isNotEmpty(paramsStr)) {
//                String deString = URLDecoder.decode(paramsStr, "UTF-8");
//                OaBusdata oaBusdata = JSON.parseObject(deString, OaBusdata.class);
//                queryWrapper = QueryGenerator.initQueryWrapper(oaBusdata, request.getParameterMap());
//            }
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        List<OaBusdata> pageList = iOaBusdataService.list(queryWrapper);
//     *//*   if(type == 1){
//          //  List<Map<String, Object>>  byTableAndMy = iOaBusdataService.findByTableAndMy(table, oaBusdata);
//            for (Map<String, Object>  list :byTableAndMy) {
//                Object i_create_month = list.get("i_create_month");
//                OaBusdata oaBusdata1 = new OaBusdata() ;
//                oaBusdata1.setICreateMonth((Integer) list.get("i_create_month")) ;
//                oaBusdata1.setICreateMonth(Integer.parseInt( list.get("i_create_month")+"")) ;
//                //lisyo.add(i_create_month);
//                Object num = list.get("num");
//                oaBusdata1.setIBigint1(Integer.parseInt( list.get("num")+"")) ;
//                lisyo.add(oaBusdata1);
//            }
//        }
//        else{
//            List<Map<String, Object>>  byTableAndMy = iOaBusdataService.selectDeptNames(table, oaBusdata);
//            for (Map<String, Object>  list :byTableAndMy) {
//                OaBusdata oaBusdata1 = new OaBusdata() ;
//                //Object i_create_month = list.get("s_main_dept_names");
//                oaBusdata1.setSMainUnitNames((String) list.get("s_main_dept_names")) ;
//                Object num = list.get("num");
//                oaBusdata1.setIBigint1(Integer.parseInt( list.get("num")+"")) ;
//
//                lisyo.add(oaBusdata1);
//            }
//        }*//*
//        //Step.2 AutoPoi 导出Excel
//        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
//
//        //导出文件名称
//        mv.addObject(NormalExcelConstants.FILE_NAME, "业务模板列表");
//        mv.addObject(NormalExcelConstants.CLASS, OaBusdata.class);
//        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("业务模板列表数据", "导出人:Jeecg", "导出信息"));
//        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
//        return mv;
//    }

    /**
     * 导出报表
     *
     * @return
     */
    @RequestMapping(value = "/exportXls")
    @ResponseBody
    public void export(HttpServletRequest request, HttpServletResponse response, OaBusdata oaBusdata, @RequestParam(name = "modelId", required = false) Integer modelId, Integer type) throws Exception {
        String table = iOaBusdataService.queryTableName(modelId);
        List<Integer> list = new ArrayList<>();
        List<Map<String, Object>> sortList = null;
        if (type == 1) {
            //获取数据
            List<Map<String, Object>> byTableAndMy = dataAnalysisService.findByTableAndMy(table, oaBusdata);
            //标题
            String[] title = {"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"};
            //excel文件名
            String fileName = oaBusdata.getICreateYear() + "年统计信息表" + System.currentTimeMillis() + ".xlsx";
            //sheet名
            String sheetName = "统计表";
            String content[][] = new String[2][title.length];

            content[0] = title;
            for (int i = 0; i < byTableAndMy.size(); i++) {
                Map<String, Object> map = byTableAndMy.get(i);
                String i_create_month = map.get("i_create_month") + "";
                Object num = map.get("num");
                content[1][i] = num.toString();
            }

            System.out.println(fileName + "////////////////");


            //创建HSSFWorkbook
            HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook(sheetName, title, content, null);
//            响应到客户端
            try {
                this.setResponseHeader(response, fileName);
                OutputStream os = response.getOutputStream();
//                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//                response.setContentType("application/msexcel");
//                response.setHeader("content-disposition", "attachment;filename=" + fileName + ".xlsx");
//
////                response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
//                response.setCharacterEncoding("GBK");

                wb.write(os);
                os.flush();
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //发送响应流方法
    public void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            try {
                fileName = new String(fileName.getBytes(), "ISO8859-1");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
//            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setContentType("application/msexcel;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
//            response.setCharacterEncoding("utf8");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
