package com.cfcc.modules.oaBus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * @Description: 业务模块表（业务分类表）
 * @Author: jeecg-boot
 * @Date:   2019-10-12
 * @Version: V1.0
 */
@Data
@TableName("oa_bus_model")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="oa_bus_model对象", description="业务模块表（业务分类表）")
public class BusModel implements Serializable {
    
	/**主键id*/
	@Excel(name = "主键id", width = 15)
    @ApiModelProperty(value = "主键id")
	@TableField(value = "i_id")
	@TableId(value = "i_id",type = IdType.AUTO)
	private java.lang.Integer iId;
	/**序号*/
	@Excel(name = "序号", width = 15)
    @ApiModelProperty(value = "序号")
	private java.lang.Integer iOrder;

	/**业务模块名称*/
	@Excel(name = "业务模块名称", width = 15)
	@ApiModelProperty(value = "业务模块名称")
	private java.lang.String sName;
	/**业务模块英文名称*/
	@Excel(name = "业务模块英文名称", width = 15)
	@ApiModelProperty(value = "业务模块英文名称")
	private java.lang.String sEnName;
	/**业务数据表*/
	@Excel(name = "业务数据表", width = 15)
    @ApiModelProperty(value = "业务数据表")
	private java.lang.String sBusdataTable;

	/**是否单选框展示所属业务功能（页签方式）*/
	@Excel(name = "是否单选框展示所属业务功能（页签方式）", width = 15)
	@ApiModelProperty(value = "是否单选框展示所属业务功能（页签方式）")
	private java.lang.Integer iIsRadio;



}
