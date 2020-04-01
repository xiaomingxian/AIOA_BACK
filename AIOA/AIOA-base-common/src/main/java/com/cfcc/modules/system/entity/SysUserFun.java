package com.cfcc.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 用户代理人设置
 * @Author: jeecg-boot
 * @Date:  2019-04-17
 * @Version: V1.0
 */
@Data
@TableName("sys_user_fun")
public class SysUserFun implements Serializable {
    private static final long serialVersionUID = 1L;

	/**序号*/
	@TableId(value = "i_id",type = IdType.AUTO)
	@Excel(name = "主键id", width = 32)
	private String iId;
	/**用户id*/
	@TableField(value = "s_user_id")
	@Excel(name = "用户id", width = 32)
	private java.lang.String sUserId;
	/**业务模块id*/
	@TableField(value = "i_bus_model_id")
	@Excel(name = "业务模块id", width = 11)
	private java.lang.Integer iBusModelId;
	/**业务功能id*/
	@TableField(value = "i_bus_function_id")
	@Excel(name = "业务功能id", width = 11)
	private java.lang.Integer iBusFunctionId;
	/**状态*/
	@TableField(value = "status")
	@Excel(name = "状态", width = 1)
	private java.lang.Integer status;
}
