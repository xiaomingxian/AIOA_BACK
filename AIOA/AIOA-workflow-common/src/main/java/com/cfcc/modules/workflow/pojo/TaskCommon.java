package com.cfcc.modules.workflow.pojo;

import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

@Data
public class TaskCommon implements Serializable {

    String busMsg;

    //缓急
    public String getHuanJi() {
        if (busMsg==null)return "";
        if (busMsg.split(",").length < 1) return "";

        String s = busMsg.split(",")[0];
        return getNotNUll(s);
    }

    //模块
    public String getModelId() {
        if (busMsg==null)return "";

        if (busMsg.split(",").length < 2) return "";

        String s = busMsg.split(",")[1];
        return getNotNUll(s);
    }


    //文号
    public String getWenHao() {
        if (busMsg==null)return "";

        if (busMsg.split(",").length < 3) return "";

        String s = busMsg.split(",")[2];
        return getNotNUll(s).replaceAll("\\{file_num:", "").replaceAll("\\}", "");
    }

    //标题
    public String getTitle() {
        if (busMsg==null)return "";

        if (busMsg.split(",").length < 4) return "";

        String huanJi = getHuanJi();
        if ("1".equals(huanJi)) huanJi = "[特急]";
        else if ("2".equals(huanJi)) huanJi = "[紧急]";
        else if ("3".equals(huanJi)) huanJi = "[加急]";
        else if ("4".equals(huanJi)) huanJi = "[一般]";
        else huanJi = "";
        String s = busMsg.split(",")[3];

        String title = getNotNUll(s).replaceAll("\\[", "").
                replaceAll("\\]", "").replaceAll("s_title:", "");

        String funName = "";
        if (StringUtils.isNotBlank(getFunName())) {
            funName = "[" + getFunName() + "]";
        }
        return huanJi + funName + title;
    }

    //拟稿人
    public String getDrafter() {
        if (busMsg==null)return "";

        if (busMsg.split(",").length < 5) return "";

        String s = busMsg.split(",")[4];
        return getNotNUll(s).replaceAll("\\$create_name:", "").replaceAll("\\$", "");
    }

    //拟稿人id
    public String getDrafterId() {
        if (busMsg==null)return "";

        if (busMsg.split(",").length < 6) return "";

        String s = busMsg.split(",")[5];
        return getNotNUll(s);
    }


    //业务id
    public String getTableId() {
        if (busMsg==null)return "";

        if (busMsg.split(",").length < 7) return "";

        String s = busMsg.split(",")[6];
        return getNotNUll(s);
    }


    //业务表
    public String getTable() {
        if (busMsg==null)return "";

        if (busMsg.split(",").length < 8) return "";

        String s = busMsg.split(",")[7];
        return getNotNUll(s);
    }

    //页面地址
    public String getPageRef() {
        if (busMsg==null)return "";

        if (busMsg.split(",").length < 9) return "";

        String s = busMsg.split(",")[8];
        return getNotNUll(s);
    }

    //业务和流程关联配置表（流程KEY、按钮、意见） 主键
    public String getBusAndActId() {
        if (busMsg==null)return "";

        if (busMsg.split(",").length < 10) return "";
        String s = busMsg.split(",")[9];
        return getNotNUll(s);
    }

    //functionid
    public String getFunctionId() {
        if (busMsg==null)return "";

        if (busMsg.split(",").length < 11) return "";
        String s = busMsg.split(",")[10];
        return getNotNUll(s);
    }

    public String getFunName() {
        if (busMsg==null)return "";

        if (busMsg.split(",").length < 12) return "";
        String s = busMsg.split(",")[11];
        return getNotNUll(s).replaceAll("!functionName:", "").replaceAll("!", "");
    }

    public String getImportant() {
        if (busMsg==null)return "";

        if (busMsg.split(",").length < 13) return "";

        String s = busMsg.split(",")[12];
        return getNotNUll(s).replaceAll("\\^important:", "").replaceAll("\\^", "");
    }


    public String getMainDept() {
        if (busMsg==null)return "";

        if (busMsg.split(",").length < 14) return "";

        String s = busMsg.split(",")[13];
        return getNotNUll(s).replaceAll("@mainDept:", "").replaceAll("@", "");
    }


    private String getNotNUll(String s) {
        if (s == null || (s != null && "null".equals(s))) s = "";
        return s;
    }

}
