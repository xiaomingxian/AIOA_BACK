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

/**
 * @Description: 模板管理
 * @Author: jeecg-boot
 * @Date:   2019-09-25
 * @Version: V1.0
 */
@Data
@TableName("oa_tempmanage")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="oa_tempmanage对象", description="模板管理")
public class SysModule {
    
	/**iid*/
	@Excel(name = "iid", width = 15)
    @ApiModelProperty(value = "iid")
	private java.lang.Integer iid;
	/**模板名称*/
	@Excel(name = "模板名称", width = 15)
    @ApiModelProperty(value = "模板名称")
	private java.lang.String stempname;
	/**发送对象 1 上报 2 下发*/
	@Excel(name = "发送对象 1 上报 2 下发", width = 15)
    @ApiModelProperty(value = "发送对象 1 上报 2 下发")
	private java.lang.String stemptype;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
	private java.lang.Integer iuserid;
	/**创建时间*/
	@Excel(name = "创建时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
	private java.util.Date dcreatdate;
	/**模板存放路径*/
	@Excel(name = "模板存放路径", width = 15)
    @ApiModelProperty(value = "模板存放路径")
	private java.lang.String stemppath;
	/**描述模板基本信息。如模板的适应场景等*/
	@Excel(name = "描述模板基本信息。如模板的适应场景等", width = 15)
    @ApiModelProperty(value = "描述模板基本信息。如模板的适应场景等")
	private java.lang.String stempdescribe;
}
