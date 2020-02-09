package com.cfcc.modules.oabutton.entity;

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
import org.springframework.data.annotation.TypeAlias;

/**
 * @Description: 意见配置按钮
 * @Author: jeecg-boot
 * @Date:   2019-10-27
 * @Version: V1.0
 */
@Data
@TableName("oa_proc_opinion")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="oa_proc_opinion对象", description="意见配置按钮")
public class OaProcOpinion {
    
	/**主键id*/
	@Excel(name = "主键id", width = 15)
    @ApiModelProperty(value = "主键id")
	@TableId(value = "i_id", type = IdType.AUTO)
	private java.lang.Integer iId;
	/**流程意见关联表（概要说明此套配置特点）*/
	@Excel(name = "流程意见关联表（概要说明此套配置特点）", width = 15)
    @ApiModelProperty(value = "流程意见关联表（概要说明此套配置特点）")
	private java.lang.String sProcOpinionName;
	/**流程定义KEY*/
	@Excel(name = "流程定义KEY", width = 15)
    @ApiModelProperty(value = "流程定义KEY")
	@TableField(value = "PROC_DEF_KEY_")
	private java.lang.String procDefKey;
}
