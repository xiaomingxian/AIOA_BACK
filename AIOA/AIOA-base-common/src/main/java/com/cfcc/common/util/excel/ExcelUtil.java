package com.cfcc.common.util.excel;

import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import java.util.List;

public class ExcelUtil extends JeecgEntityExcelView {

    private volatile static ExcelUtil excelUtil;

    private ExcelUtil() {
    }

    private static ExcelUtil getExcelUtil() {
        if (excelUtil == null) {
            synchronized (ExcelUtil.class) {
                if (excelUtil == null) {
                    excelUtil = new ExcelUtil();
                }
            }
        }
        return excelUtil;
    }

    private  String fileName;


    static ExcelUtil setParams(List<String> columns) {
        ExcelUtil excelUtil = getExcelUtil();
        //excelUtil

        return null;
    }

}
