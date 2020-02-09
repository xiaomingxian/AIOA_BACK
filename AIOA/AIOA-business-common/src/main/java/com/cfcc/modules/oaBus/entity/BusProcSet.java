package com.cfcc.modules.oaBus.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 业务和流程关联配置表
 * @Author: jeecg-boot
 * @Date:   2019-11-05
 * @Version: V1.0
 */
@Data
@TableName("oa_bus_proc_set")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="oa_bus_proc_set对象", description="业务和流程关联配置表")
public class BusProcSet {
    
	/**主键id*/
	@Excel(name = "主键id", width = 15)
    @ApiModelProperty(value = "主键id")
	@TableId(value = "i_id",type = IdType.AUTO)
	private java.lang.Integer iId;
	/**业务模块id*/
	@Excel(name = "业务模块id", width = 15)
    @ApiModelProperty(value = "业务模块id")
	private java.lang.Integer iBusModelId;
	/**业务配置ID*/
	@Excel(name = "业务配置ID", width = 15)
    @ApiModelProperty(value = "业务配置ID")
	private java.lang.Integer iBusFunctionId;
	/**流程按钮配置ID*/
	@Excel(name = "流程按钮配置ID", width = 15)
    @ApiModelProperty(value = "流程按钮配置ID")
	@TableField(updateStrategy = FieldStrategy.IGNORED)
	private java.lang.Integer iProcButtonId;
	/**流程意见配置ID*/
	@Excel(name = "流程意见配置ID", width = 15)
    @ApiModelProperty(value = "流程意见配置ID")
	@TableField(updateStrategy = FieldStrategy.IGNORED)
	private java.lang.Integer iProcOpinionId;
	/**流程定义KEY*/
	@Excel(name = "流程定义KEY", width = 15)
    @ApiModelProperty(value = "流程定义KEY")
	@TableField(value = "proc_def_key_" ,updateStrategy = FieldStrategy.IGNORED)
	private java.lang.String procDefKey;


	/**版本号*/
	@Excel(name = "版本号", width = 15)
    @ApiModelProperty(value = "版本号")
	private java.lang.Integer iVersion;

}
