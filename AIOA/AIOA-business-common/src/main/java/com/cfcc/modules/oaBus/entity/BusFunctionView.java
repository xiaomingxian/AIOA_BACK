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

/**
 * @Description: 业务功能数据查看权限表
 * @Author: jeecg-boot
 * @Date:   2019-10-21
 * @Version: V1.0
 */
@Data
@TableName("oa_bus_function_view")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="oa_bus_function_view对象", description="业务功能数据查看权限表")
public class BusFunctionView {
    
	/**主键id*/
	@Excel(name = "主键id", width = 15)
    @ApiModelProperty(value = "主键id")
	@TableId(value = "i_id",type = IdType.AUTO)
	private java.lang.Integer iId;
	/**业务功能配置id*/
	@Excel(name = "业务功能配置id", width = 15)
    @ApiModelProperty(value = "业务功能配置id")
	private java.lang.Integer iBusFunctionId;
	/**查看权限类型（1、角色、2、角色+机构3、角色+部门4、角色+分管部门）*/
	@Excel(name = "查看权限类型（1、角色、2、角色+机构3、角色+部门4、角色+分管部门）", width = 15)
    @ApiModelProperty(value = "查看权限类型（1、角色、2、角色+机构3、角色+部门4、角色+分管部门）")
	private java.lang.Integer iType;
	/**角色ID*/
	@Excel(name = "角色ID", width = 15)
    @ApiModelProperty(value = "角色ID")
	private String iRoleId;
	/**角色Name*/
	@TableField(exist = false)
	private String sRoleName;

}
