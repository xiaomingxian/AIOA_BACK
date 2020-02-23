package com.cfcc.modules.oaTeamwork.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 个人协同办公业务实例
 * @Author: jeecg-boot
 * @Date:   2020-01-02
 * @Version: V1.0
 */
@Data
@TableName("oa_teamwork_inst")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="oa_teamwork_inst对象", description="个人协同办公业务实例")
public class oaTeamworkInst {
    
	/**主键id*/
	@Excel(name = "主键id", width = 15)
    @ApiModelProperty(value = "主键id")
	private java.lang.Integer iId;
	/**个人协同办公业务配置分类表ID*/
	@Excel(name = "个人协同办公业务配置分类表ID", width = 15)
    @ApiModelProperty(value = "个人协同办公业务配置分类表ID")
	private java.lang.Integer iTeamworkId;
	/**个人协同办公业务配置明细ID*/
	@Excel(name = "个人协同办公业务配置明细ID", width = 15)
    @ApiModelProperty(value = "个人协同办公业务配置明细ID")
	private java.lang.Integer iTeamworkSetId;
	/**步骤序号(与teamworkset配置一致）*/
	@Excel(name = "步骤序号(与teamworkset配置一致）", width = 15)
    @ApiModelProperty(value = "步骤序号(与teamworkset配置一致）")
	private java.lang.Integer iOrder;
	/**业务模块id*/
	@Excel(name = "业务模块id", width = 15)
    @ApiModelProperty(value = "业务模块id")
	private java.lang.Integer iBusModelId;
	/**业务功能id*/
	@Excel(name = "业务功能id", width = 15)
    @ApiModelProperty(value = "业务功能id")
	private java.lang.Integer iBusFunctionId;
	/**版本号(暂不考虑）*/
	@Excel(name = "版本号(暂不考虑）", width = 15)
    @ApiModelProperty(value = "版本号(暂不考虑）")
	private java.lang.Integer iVersion;
	/**业务数据id*/
	@Excel(name = "业务数据id", width = 15)
    @ApiModelProperty(value = "业务数据id")
	private java.lang.Integer iBusdataId;

	@TableField(exist = false)
	private java.lang.String iTeamworkName; //个人协同办公业务配置分类名称
	@TableField(exist = false)
	private java.lang.String busModelName;
	@TableField(exist = false)
	private java.lang.String busFunctionName;
	@TableField(exist = false)
	private java.lang.String TeamworkName;
	@TableField(exist = false)
	private java.lang.String dataIds; //个人协同办公业务配置分类名称
	@TableField(exist = false)
	private java.lang.String busFunctionIds;
	@TableField(exist = false)
	private java.lang.String busModelIds;
	@TableField(exist = false)
	private java.lang.String Orders;
	@TableField(exist = false)
	private java.lang.String LastOrder;
	@TableField(exist = false)
	private java.lang.Integer busDataId; //表
	@TableField(exist = false)
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date dCreateTime;
	private boolean tableOrder;


}
