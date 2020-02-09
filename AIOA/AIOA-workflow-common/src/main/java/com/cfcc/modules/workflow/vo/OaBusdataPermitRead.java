package com.cfcc.modules.workflow.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

@Data
public class OaBusdataPermitRead {
    /**主键id*/
    @Excel(name = "主键id", width = 15)
    @ApiModelProperty(value = "主键id")
    private java.lang.Integer iId;
    /**机构id*/
    @Excel(name = "机构id", width = 15)
    @ApiModelProperty(value = "机构id")
    private java.lang.String unitId;
    /**部门id*/
    @Excel(name = "部门id", width = 15)
    @ApiModelProperty(value = "部门id")
    private java.lang.String deptId;
    /**用户id*/
    @Excel(name = "用户id", width = 15)
    @ApiModelProperty(value = "用户id")
    private java.lang.String sUserId;
    /**用户名*/
    @Excel(name = "用户名", width = 15)
    @ApiModelProperty(value = "用户名")
    private java.lang.String userName;
    /**用户部门*/
    @Excel(name = "用户部门", width = 15)
    @ApiModelProperty(value = "用户部门")
    private java.lang.String deptName;
    /**是否已读*/
    @Excel(name = "是否已读", width = 15)
    @ApiModelProperty(value = "是否已读")
    private java.lang.Integer isRead;

}
