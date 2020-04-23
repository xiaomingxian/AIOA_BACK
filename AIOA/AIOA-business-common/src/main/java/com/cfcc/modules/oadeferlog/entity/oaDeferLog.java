package com.cfcc.modules.oadeferlog.entity;

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
 * @Description: 任务类事务延期记录
 * @Author: jeecg-boot
 * @Date:   2020-04-23
 * @Version: V1.0
 */
@Data
@TableName("oa_defer_log")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="oa_defer_log对象", description="任务类事务延期记录")
public class oaDeferLog {
    
	/**iId*/
	@Excel(name = "iId", width = 15)
    @ApiModelProperty(value = "iId")
	private java.lang.Integer iId;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private java.util.Date dCreateTime;
	/**创建者姓名*/
	@Excel(name = "创建者姓名", width = 15)
    @ApiModelProperty(value = "创建者姓名")
	private java.lang.String sCreateName;
	/**创建者id*/
	@Excel(name = "创建者id", width = 15)
    @ApiModelProperty(value = "创建者id")
	private java.lang.String sCreateBy;
	/**附件所属数据所在表*/
	@Excel(name = "附件所属数据所在表", width = 15)
    @ApiModelProperty(value = "附件所属数据所在表")
	private java.lang.String sTable;
	/**附件所属数据id*/
	@Excel(name = "附件所属数据id", width = 15)
    @ApiModelProperty(value = "附件所属数据id")
	private java.lang.Integer iTableId;
	/**业务模块id*/
	@Excel(name = "业务模块id", width = 15)
    @ApiModelProperty(value = "业务模块id")
	private java.lang.Integer iBusModelId;
	/**业务功能id*/
	@Excel(name = "业务功能id", width = 15)
    @ApiModelProperty(value = "业务功能id")
	private java.lang.Integer iBusFunctionId;
	/**原来时间*/
	@Excel(name = "原来时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "原来时间")
	private java.util.Date dDatetime1;
	/**新的时间*/
	@Excel(name = "新的时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "新的时间")
	private java.util.Date dDatetime2;
	/**原来日期*/
	@Excel(name = "原来日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "原来日期")
	private java.util.Date dDate1;
	/**新的日期*/
	@Excel(name = "新的日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "新的日期")
	private java.util.Date dDate2;
	/**创建部门名称*/
	@Excel(name = "创建部门名称", width = 15)
    @ApiModelProperty(value = "创建部门名称")
	private java.lang.String sCreateDept;
	/**创建者部门id*/
	@Excel(name = "创建者部门id", width = 15)
    @ApiModelProperty(value = "创建者部门id")
	private java.lang.String sCreateDeptid;
}
