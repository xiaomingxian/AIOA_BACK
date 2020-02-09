package com.cfcc.modules.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 备份情况
 * @Author: jeecg-boot
 * @Date:   2019-11-28
 * @Version: V1.0
 */
@Data
@TableName("qrtz_back_up")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="qrtz_back_up对象", description="备份情况")
public class QrtzBackUp {
    
	/**主键id*/
	@Excel(name = "主键id", width = 15)
    @ApiModelProperty(value = "主键id")
	private java.lang.Integer iId;
	/**备份数据类型 1：数据库 2：附件*/
	@Excel(name = "备份数据类型 1：数据库 2：附件", width = 15)
    @ApiModelProperty(value = "备份数据类型 1：数据库 2：附件")
	private java.lang.Integer iBackType;
	/**备份文件名称*/
	@Excel(name = "备份文件名称", width = 15)
    @ApiModelProperty(value = "备份文件名称")
	private java.lang.String sName;
	/**文件路径*/
	@Excel(name = "文件路径", width = 15)
    @ApiModelProperty(value = "文件路径")
	private java.lang.String sBackPath;
	/**文件大小*/
	@Excel(name = "文件大小", width = 15)
    @ApiModelProperty(value = "文件大小")
	private java.lang.String sFileSize;
	/**开始时间*/
	@Excel(name = "开始时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
	private java.util.Date dStartTime;
	/**结束时间*/
	@Excel(name = "结束时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
	private java.util.Date dEndTime;

	@Excel(name = "结束时间", width = 20, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "创建时间")
	private Date dCreateDay;
}
