package com.cfcc.modules.oadatafetailedinst.entity;

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
 * @Description: 明细存储
 * @Author: jeecg-boot
 * @Date:   2020-04-16
 * @Version: V1.0
 */
@Data
@TableName("oa_datadetailed_inst")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="oa_datadetailed_inst对象", description="明细存储")
public class OaDatadetailedInst {
    
	/**主键id*/
	@Excel(name = "主键id", width = 15)
    @ApiModelProperty(value = "主键id")
	private java.lang.Integer iId;
	/**所属数据所在表*/
	@Excel(name = "所属数据所在表", width = 15)
    @ApiModelProperty(value = "所属数据所在表")
	private java.lang.String sTable;
	/**所属数据id*/
	@Excel(name = "所属数据id", width = 15)
    @ApiModelProperty(value = "所属数据id")
	private java.lang.Integer iTableId;
	/**意见*/
	@Excel(name = "意见", width = 15)
    @ApiModelProperty(value = "意见")
	private java.lang.String sOpinion;
	/**公开(1、公开；0为不公开）*/
	@Excel(name = "公开(1、公开；0为不公开）", width = 15)
    @ApiModelProperty(value = "公开(1、公开；0为不公开）")
	private java.lang.Integer iIsOpen;
	/**C_时间1*/
	@Excel(name = "C_时间1", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "C_时间1")
	private java.util.Date dDatetime1;
	/**C_日期1*/
	@Excel(name = "C_日期1", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "C_日期1")
	private java.util.Date dDate1;
	/**C_文本1（50）*/
	@Excel(name = "C_文本1（50）", width = 15)
    @ApiModelProperty(value = "C_文本1（50）")
	private java.lang.String sVarchar1;
	/**C_文本2（50）*/
	@Excel(name = "C_文本2（50）", width = 15)
    @ApiModelProperty(value = "C_文本2（50）")
	private java.lang.String sVarchar2;
	/**C_状态标识1*/
	@Excel(name = "C_状态标识1", width = 15)
    @ApiModelProperty(value = "C_状态标识1")
	private java.lang.Integer iIs1;
	/**C_状态标识2*/
	@Excel(name = "C_状态标识2", width = 15)
    @ApiModelProperty(value = "C_状态标识2")
	private java.lang.Integer iIs2;
	/**创建者姓名*/
	@Excel(name = "创建者姓名", width = 15)
    @ApiModelProperty(value = "创建者姓名")
	private java.lang.String sCreateName;
	/**创建者id*/
	@Excel(name = "创建者id", width = 15)
    @ApiModelProperty(value = "创建者id")
	private java.lang.String sCreateBy;
	/**创建部门名称*/
	@Excel(name = "创建部门名称", width = 15)
    @ApiModelProperty(value = "创建部门名称")
	private java.lang.String sCreateDept;
	/**创建者部门id*/
	@Excel(name = "创建者部门id", width = 15)
    @ApiModelProperty(value = "创建者部门id")
	private java.lang.String sCreateDeptid;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private java.util.Date dCreateTime;
	/**是否为临时文件*/
	@Excel(name = "是否为临时文件", width = 15)
	@ApiModelProperty(value = "是否为临时文件")
	private java.lang.Integer i_is_display;
}
