package com.cfcc.common.util.workflow;

import com.cfcc.common.exception.AIOAException;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

public class VarsWithBus {

    /**
     * 获取流程终存入的与业务相关的数据
     * <p>
     * 1.文件标题
     * 2.文件字号
     * 3.拟稿人
     * 4.主办部门
     * 5.任务类型
     * 备注：
     * 主办部门：发文的拟稿部门/收文的主办部门
     * 文件字号：发文的文件字号/收文的来文字号
     */
    public static String getBusMsg(Map<String, Object> map) {


        String actShow = (String) map.get("act_show");
        if (StringUtils.isBlank(actShow))throw  new AIOAException("前端页面缺少act_show配置");
        String[] cols = actShow.split(",");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cols.length; i++) {
            if (i == 0) {
                sb.append(map.get(cols[i]));
            } else {
                //判断modelid是几位数
                String col = cols[i];
                String v = map.get(col) + "";
                if ("ilevel".equalsIgnoreCase(col)) {
                    if (v.length() < 2) {
                        v = 0 + v;
                    }
                }
                //新建任务时没有id
                if ("i_id".equalsIgnoreCase(col) && map.get(col) == null) {
                    v = "i_id";
                }
                //标题
                if ("s_title".equalsIgnoreCase(col)) {
                    v = map.get(col) == null ? "" : map.get(col).toString();
                    v = "[s_title:" + v + "]";
                }


                //文号
                if ("s_file_num".equalsIgnoreCase(col)) {
                    v = map.get(col) == null ? "" : map.get(col).toString();
                    v = "{file_num:" + v + "}";
                }
                //拟稿人
                if ("s_create_name".equalsIgnoreCase(col)) {
                    v = map.get(col) == null ? "" : map.get(col).toString();
                    v = "$create_name:" + v + "$";
                }

                //主办部门
                if ("mainDept".equalsIgnoreCase(col)) {
                    v = map.get(col) == null ? "" : map.get(col).toString();
                    v = "@mainDept:" + v + "@";
                }
                //是否重要
                if ("i_is_important".equalsIgnoreCase(col)) {
                    v = map.get(col) == null ? "" : map.get(col).toString();
                    v = "^important:" + v + "^";
                }

                //业务名称
                if ("functionName".equalsIgnoreCase(col)) {
                    v = map.get(col) == null ? "" : map.get(col).toString();
                    v = "!functionName:" + v + "!";
                }

                sb.append(",").append(v);
            }
        }
        return sb.toString();
    }
}
