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
 * @Description: 业务功能和机构、部门关联表
 * @Author: jeecg-boot
 * @Date:   2019-10-21
 * @Version: V1.0
 */
@Data
@TableName("oa_bus_function_unit")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="oa_bus_function_unit对象", description="业务功能和机构、部门关联表")
public class BusFunctionUnit {
    
	/**主键ID*/
	@TableId(value = "i_id",type = IdType.AUTO)
	@Excel(name = "主键ID", width = 15)
    @ApiModelProperty(value = "主键ID")
	private java.lang.Integer iId;
	/**业务功能id*/
	@Excel(name = "业务功能id", width = 15)
    @ApiModelProperty(value = "业务功能id")
	private java.lang.Integer iBusFunctionId;
	/**部门id*/
	@Excel(name = "部门id", width = 15)
    @ApiModelProperty(value = "部门id")
	private String sDeptId;
	/**部门名字*/
	@TableField(exist = false)
	private String sDeptName;
	/**机构id*/
	@Excel(name = "机构id", width = 15)
    @ApiModelProperty(value = "机构id")
	private String sUnitId;
	/**部门名字*/
	@TableField(exist = false)
	private String sUnitName;
}
