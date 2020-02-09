package com.cfcc.modules.oabutton.entity;

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
import org.springframework.data.annotation.TypeAlias;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 意见配置按钮
 * @Author: jeecg-boot
 * @Date:   2019-10-27
 * @Version: V1.0
 */
@Data
@TableName("oa_opinion_set")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="oa_opinion_set对象", description="意见配置按钮")
public class OaOpinionSet {
    
	/**主键id*/
	@Excel(name = "主键id", width = 15)
    @ApiModelProperty(value = "主键id")
	private java.lang.Integer iId;
	/**流程意见关联ID*/
	@Excel(name = "流程意见关联ID", width = 15)
    @ApiModelProperty(value = "流程意见关联ID")
	@TableField(value = "i_proc_opinion_id")
	private java.lang.Integer iProcOpinionId;
	/**业务流程配置ID*/
	@TableField(value = "i_proc_set_id")
	@Excel(name = "业务流程配置ID", width = 15)
    @ApiModelProperty(value = "业务流程配置ID")
	private java.lang.Integer iProcSetId;
	/**此环节意见框名称*/
	@TableField(value = "i_task_opinion_name")
	@Excel(name = "此环节意见框名称", width = 15)
    @ApiModelProperty(value = "此环节意见框名称")
	private java.lang.String iTaskOpinionName;
	/**此环节意见框位置*/
	@TableField(value = "i_task_opinion_order")
	@Excel(name = "此环节意见框位置", width = 15)
    @ApiModelProperty(value = "此环节意见框位置")
	private java.lang.Integer iTaskOpinionOrder;
	/**流程定义KEY（按钮配置和流程紧耦合，但可以多套按钮配置方案供用户选择）*/
	@TableField(value = "PROC_DEF_KEY_")
	@Excel(name = "流程定义KEY（按钮配置和流程紧耦合，但可以多套按钮配置方案供用户选择）", width = 15)
    @ApiModelProperty(value = "流程定义KEY（按钮配置和流程紧耦合，但可以多套按钮配置方案供用户选择）")
	private java.lang.String procDefKey;
	/**任务环节（按钮权限和任务环节绑定）*/
	@Excel(name = "任务环节（按钮权限和任务环节绑定）", width = 15)
    @ApiModelProperty(value = "任务环节（按钮权限和任务环节绑定）")
	@TableField(value = "TASK_DEF_KEY_")
	private java.lang.String taskDefKey;

	/**类型（意见框位置）*/
	@Excel(name = "类型", width = 15)
	@ApiModelProperty(value = "类型")
	@TableField(value = "TYPE_")
	private java.lang.String type;

}
