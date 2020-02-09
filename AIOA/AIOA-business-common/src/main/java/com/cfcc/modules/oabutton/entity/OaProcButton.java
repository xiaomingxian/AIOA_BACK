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
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 发布类按钮管理
 * @Author: jeecg-boot
 * @Date:   2019-10-26
 * @Version: V1.0
 */
@Data
@TableName("oa_proc_button")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="oa_proc_button对象", description="发布类按钮管理")
public class OaProcButton {
    
	/**主键id*/
	@Excel(name = "主键id", width = 15)
    @ApiModelProperty(value = "主键id")
	@TableId(value = "i_id", type = IdType.AUTO)
	private java.lang.Integer iId;
	/**流程定义KEY*/
	@Excel(name = "流程定义KEY", width = 15)
    @ApiModelProperty(value = "流程定义KEY")
	private java.lang.String procDefKey;
	/**流程按钮配置名称（概要说明此套配置特点）*/
	@Excel(name = "流程按钮配置名称（概要说明此套配置特点）", width = 15)
    @ApiModelProperty(value = "流程按钮配置名称（概要说明此套配置特点）")
	private java.lang.String sButtonSetName;
}
