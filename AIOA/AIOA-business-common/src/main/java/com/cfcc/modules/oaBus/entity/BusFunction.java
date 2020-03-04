package com.cfcc.modules.oaBus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: 业务配置表
 * @Author: jeecg-boot
 * @Date: 2019-10-15
 * @Version: V1.0
 */
@Data
@TableName("oa_bus_function")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "oa_bus_function对象", description = "业务配置表")
public class BusFunction implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */

    @Excel(name = "主键id", width = 15)
    @ApiModelProperty(value = "主键id")
    @TableId(value = "i_id", type = IdType.AUTO)
    private java.lang.Integer iId;
    /**
     * 业务模块id
     */
    @Excel(name = "业务模块id", width = 15)
    @ApiModelProperty(value = "业务模块id")
    private java.lang.Integer iBusModelId;
    /**
     * 业务类型名称
     */
    @Excel(name = "业务类型名称", width = 15)
    @ApiModelProperty(value = "业务类型名称")
    private java.lang.String sName;
    /**
     * 详情页面id
     */
    @Excel(name = "详情页面id", width = 15)
    @ApiModelProperty(value = "详情页面id")
    private java.lang.Integer iPageId;
    /**
     * 页面头部左侧传入参数
     */
    @Excel(name = "页面头部左侧传入参数", width = 15)
    @ApiModelProperty(value = "页面头部左侧传入参数")
    private java.lang.String sBusLeftParameter;
    /**
     * 是否插入机构
     */
    @Excel(name = "是否插入机构", width = 15)
    @ApiModelProperty(value = "是否插入机构")
    private java.lang.Integer iIsUnit;
    /**
     * 业务优先级 （打破模块限制，所有业务功能排序，维护时进行校验，不能重复。两位数字表示，如01,11,21）
     */
    @Excel(name = "业务优先级", width = 15)
    @ApiModelProperty(value = "业务优先级")
    private String sLevel;
    /**
     * 是否插入部门
     */
    @Excel(name = "是否插入部门", width = 15)
    @ApiModelProperty(value = "是否插入部门")
    private java.lang.Integer iIsDept;
    /**
     * 页面头部右侧传入参数
     */
    @Excel(name = "页面头部右侧传入参数", width = 15)
    @ApiModelProperty(value = "页面头部右侧传入参数")
    private java.lang.String sBusRightParameter;
    /**
     * 是否有编辑器(1、0)
     */
    @Excel(name = "是否有编辑器(1、0)", width = 15)
    @ApiModelProperty(value = "是否有编辑器(1、0)")
    private java.lang.Integer iIsEditor;
    /**
     * 是否包含发送范围(1、0)
     */
    @Excel(name = "是否包含发送范围(1、0)", width = 15)
    @ApiModelProperty(value = "是否包含发送范围(1、0)")
    private java.lang.Integer iIsLimits;
    /**
     * 是否纳入全文检索(1、0)
     */
    @Excel(name = "是否纳入全文检索(1、0)", width = 15)
    @ApiModelProperty(value = "是否纳入全文检索(1、0)")
    private java.lang.Integer iIsEs;
    /**
     * 是否是流程审批(1、0)
     */
    @Excel(name = "是否是流程审批(1、0)", width = 15)
    @ApiModelProperty(value = "是否是流程审批(1、0)")
    private java.lang.Integer iIsProc;
    /**
     * 工作流配置id
     */
    @Excel(name = "工作流配置id", width = 15)
    @ApiModelProperty(value = "工作流配置id")
    private java.lang.Integer iProcSetId;
    /**
     * 创建者
     */
    @Excel(name = "创建者", width = 15)
    @ApiModelProperty(value = "创建者")
    private java.lang.String sCreateBy;
    /**
     * 创建时间
     */
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date dCreateTime;
    /**
     * 修改者
     */
    @Excel(name = "修改者", width = 15)
    @ApiModelProperty(value = "修改者")
    private java.lang.String sUpdateBy;
    /**
     * 修改时间
     */
    @Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private java.util.Date dUpdateTime;

    @ApiModelProperty(value = "对应的存储表名")
    @TableField(exist = false)
    private String sBusdataTable;

    @ApiModelProperty(value = "对应的存储表名")
    @TableField(exist = false)
    private String unitId;

}
