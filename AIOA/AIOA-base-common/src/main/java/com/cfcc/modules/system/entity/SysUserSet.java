package com.cfcc.modules.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * @Description: 权限设置
 * @Author: jeecg-boot
 * @Date:   2019-10-17
 * @Version: V1.0
 */
@Data
@TableName("sys_user_set")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sys_user_set对象", description="权限设置")
public class SysUserSet implements Serializable {
    
	/**iId*/
	@Excel(name = "iId", width = 15)
    @ApiModelProperty(value = "iId")
	private java.lang.Integer iId;
	/**用户id*/
	@Excel(name = "用户id", width = 15)
	@ApiModelProperty(value = "用户id")
	private java.lang.String sUserId;
	/**字体大小*/
	@Excel(name = "字体大小", width = 15)
	@ApiModelProperty(value = "字体大小")
	private java.lang.Integer iFontSize;
	/**是否AI阅读*/
	@Excel(name = "是否AI阅读", width = 15)
    @ApiModelProperty(value = "是否AI阅读")
	private java.lang.Integer iIsAi;
	/**待办工作是否即时消息提醒*/
	@Excel(name = "待办工作是否即时消息提醒", width = 15)
    @ApiModelProperty(value = "待办工作是否即时消息提醒")
	private java.lang.Integer iIsMessages;
	/**是否折叠展示数据*/
	@Excel(name = "是否折叠展示数据", width = 15)
    @ApiModelProperty(value = "是否折叠展示数据")
	@JsonIgnoreProperties
	private java.lang.Integer iIsFold;
	/**是否默认首页风格是日程办公*/
	@Excel(name = "是否默认首页风格是日程办公", width = 15)
    @ApiModelProperty(value = "是否默认首页风格是日程办公")
	private java.lang.Integer iIsCalendar;
	/**是否默认首页风格是日程办公*/
	@Excel(name = "待办转入日程最小天数", width = 15)
	@ApiModelProperty(value = "待办转入日程最小天数")
	private java.lang.Integer iCalendarDay;
	/**首页区域1对应业务模块*/
	@Excel(name = "首页区域1对应业务模块", width = 15)
	@ApiModelProperty(value = "首页区域1对应业务模块")
	private java.lang.Integer iBus1Id;
	/**首页区域3对应业务模块*/
	@Excel(name = "首页区域2对应业务模块", width = 15)
	@ApiModelProperty(value = "首页区域2对应业务模块")
	private java.lang.Integer iBus2Id;
	/**首页区域3对应业务模块*/
	@Excel(name = "首页区域3对应业务模块", width = 15)
	@ApiModelProperty(value = "首页区域3对应业务模块")
	private java.lang.Integer iBus3Id;
	/**首页区域4对应业务模块*/
	@Excel(name = "首页区域4对应业务模块", width = 15)
	@ApiModelProperty(value = "首页区域4对应业务模块")
	private java.lang.Integer iBus4Id;
	@TableField(exist = false)
	private java.lang.String ip; //用户IP


}
