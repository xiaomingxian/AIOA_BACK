package com.cfcc.modules.oabutton.vo;

import com.cfcc.modules.oabutton.entity.OaOpinionSet;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;

import java.util.List;

/**
 * @Description: 意见配置
 * @Date:   2019-10-27
 * @Version: V1.0
 */
@Data
public class OaProcOpinionPage {

    /**主键id*/
    @Excel(name = "主键id", width = 15)
    @ApiModelProperty(value = "主键id")
    private java.lang.Integer iId;
    /**流程意见关联表（概要说明此套配置特点）*/
    @Excel(name = "流程意见关联表（概要说明此套配置特点）", width = 15)
    @ApiModelProperty(value = "流程意见关联表（概要说明此套配置特点）")
    private java.lang.String sProcOpinionName;
    /**流程定义KEY*/
    @Excel(name = "流程定义KEY", width = 15)
    @ApiModelProperty(value = "流程定义KEY")
    private java.lang.String procDefKey;
    @ExcelCollection(name = "意见配置按钮")
    protected List<OaOpinionSet> oaOpinionSetList;
}
