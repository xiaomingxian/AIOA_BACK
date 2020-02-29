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
 * @Description: 个人协同办公业务配置明细
 * @Author: jeecg-boot
 * @Date:   2019-12-30
 * @Version: V1.0
 */
@Data
@TableName("oa_teamwork_set")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="oa_teamwork_set对象", description="个人协同办公业务配置明细")
public class oaTeamworkSet {
    
	/**主键id*/
	@Excel(name = "主键id", width = 15)
    @ApiModelProperty(value = "主键id")
	private java.lang.Integer iId;
	/**流程按钮关联ID*/
	@Excel(name = "流程按钮关联ID", width = 15)
    @ApiModelProperty(value = "流程按钮关联ID")
	private java.lang.Integer iTeamworkId;
	/**步骤序号(新增自动加1）*/
	@Excel(name = "步骤序号(新增自动加1）", width = 15)
    @ApiModelProperty(value = "步骤序号(新增自动加1）")
	private java.lang.Integer iOrder;
	/**描述*/
	@Excel(name = "描述", width = 15)
    @ApiModelProperty(value = "描述")
	private java.lang.String description;
	/**业务模块id*/
	@Excel(name = "业务模块id", width = 15)
    @ApiModelProperty(value = "业务模块id")
	private java.lang.Integer iBusModelId;
	/**业务功能id*/
	@Excel(name = "业务功能id", width = 15)
    @ApiModelProperty(value = "业务功能id")
	private java.lang.Integer iBusFunctionId;
	/**创建者id*/
	@Excel(name = "创建者id", width = 15)
    @ApiModelProperty(value = "创建者id")
	private java.lang.String sCreateBy;
	/**创建者部门id*/
	@Excel(name = "创建者部门id", width = 15)
    @ApiModelProperty(value = "创建者部门id")
	private java.lang.String sCreateDeptid;
	/**创建者机构id*/
	@Excel(name = "创建者机构id", width = 15)
    @ApiModelProperty(value = "创建者机构id")
	private java.lang.String sCreateUnitid;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private java.util.Date dCreateTime;
	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private java.util.Date dUpdateTime;
	/**版本号(暂不考虑）*/
	@Excel(name = "版本号(暂不考虑）", width = 15)
    @ApiModelProperty(value = "版本号(暂不考虑）")
	private java.lang.Integer iVersion;
	@TableField(exist = false)
	private java.lang.Integer MaxOrder;
	@TableField(exist = false)
	private java.lang.String busModelName;
	@TableField(exist = false)
	private java.lang.String busFunctionName;
	@TableField(exist = false)
	private java.lang.String iTeamworkName;
	@TableField(exist = false)
	private java.lang.String OrderName; //步骤名字
	@TableField(exist = false)
	private java.lang.String OrderFunctionId; //步骤名字
	@TableField(exist = false)
	private java.lang.String OrderModelId; //步骤名字
	@TableField(exist = false)
	private java.lang.String tableName; //表
	@TableField(exist = false)
	private java.lang.String sTitle;

}
