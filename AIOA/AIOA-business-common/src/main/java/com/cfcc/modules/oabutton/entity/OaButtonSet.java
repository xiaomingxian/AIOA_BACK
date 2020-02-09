package com.cfcc.modules.oabutton.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 发布类按钮描述
 * @Author: jeecg-boot
 * @Date:   2019-10-26
 * @Version: V1.0
 */
@Data
@TableName("oa_button_set")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="oa_button_set对象", description="发布类按钮描述")
public class OaButtonSet {
    
	/**主键id*/
	@Excel(name = "主键id", width = 15)
    @ApiModelProperty(value = "主键id")
	private java.lang.Integer iId;
	/**流程定义KEY（按钮配置和流程紧耦合，但可以多套按钮配置方案供用户选择）*/
	@Excel(name = "流程定义KEY（按钮配置和流程紧耦合，但可以多套按钮配置方案供用户选择）", width = 15)
    @ApiModelProperty(value = "流程定义KEY（按钮配置和流程紧耦合，但可以多套按钮配置方案供用户选择）")
	@TableField(value = "PROC_DEF_KEY_")
	private java.lang.String procDefKey;
	/**任务环节（按钮权限和任务环节绑定）*/
	@Excel(name = "任务环节（按钮权限和任务环节绑定）", width = 15)
    @ApiModelProperty(value = "任务环节（按钮权限和任务环节绑定）")
	@TableField(value = "TASK_DEF_KEY_")
	private java.lang.String taskDefKey;
	/**流程按钮关联ID*/
	@Excel(name = "流程按钮关联ID", width = 15)
    @ApiModelProperty(value = "流程按钮关联ID")
	private java.lang.Integer iProcButtonId;
	/**
	 * 按钮始终显示的角色
	 */
	@Excel(name = "按钮始终显示的角色（可多选）", width = 15)
	@ApiModelProperty(value = "按钮始终显示的角色（可多选）")
	private String sRoles;
	/**按钮ID（oa_button表）*/
	@Excel(name = "按钮ID（oa_button表）", width = 15)
    @ApiModelProperty(value = "按钮ID（oa_button表）")
	private java.lang.Integer iButtonId;
	/**序号*/
	@Excel(name = "序号", width = 15)
    @ApiModelProperty(value = "序号")
	private java.lang.Integer iOrder;
	/**权限类型（0、不控制；1、控制）*/
	@Excel(name = "权限类型（0、不控制；1、控制）", width = 15)
    @ApiModelProperty(value = "权限类型（0、不控制；1、控制）")
	private java.lang.Integer iPermitType;
	/**创建人（拟稿人）*/
	@Excel(name = "创建人（拟稿人）", width = 15)
    @ApiModelProperty(value = "创建人（拟稿人）")
	private java.lang.Integer iIsCreater;
	/**是否读者身份显示*/
	@Excel(name = "是否读者身份显示", width = 15)
    @ApiModelProperty(value = "是否读者身份显示")
	private java.lang.Integer iIsReader;
	/**是否已办用户*/
	@Excel(name = "是否已办用户", width = 15)
    @ApiModelProperty(value = "是否已办用户")
	private java.lang.Integer iIsLastsender;
	/**是否当前处理人显示*/
	@Excel(name = "是否当前处理人显示", width = 15)
    @ApiModelProperty(value = "是否当前处理人显示")
	private java.lang.Integer iIsTransactors;
	/**是否默认按钮配置（1是、0否）*/
	@Excel(name = "是否默认按钮配置（1是、0否）", width = 15)
    @ApiModelProperty(value = "是否默认按钮配置（1是、0否）")
	private java.lang.Integer iIsDefault;
	/**是否有关联任务实例产生（此处选择任务ID）*/
	@Excel(name = "是否有关联任务实例产生（此处选择任务ID）", width = 15)
    @ApiModelProperty(value = "是否有关联任务实例产生（此处选择任务ID）")
	@TableField(value = "i_TASK_DEF_KEY_")
	private java.lang.String iTaskDefKey;
	/**角色*/
	@Excel(name = "角色", width = 15)
    @ApiModelProperty(value = "角色")
	@TableField(value = "s_manager_role_id")
	private java.lang.String sManagerRoleId;
}
