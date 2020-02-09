package com.cfcc.modules.papertitle.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description: 模板管理
 * @Author: jeecg-boot
 * @Date:   2019-10-15
 * @Version: V1.0
 */
@Data
@TableName("oa_template")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="oa_template对象", description="模板管理")
public class OaTemplate {
    
	/**主键id*/
	@Excel(name = "主键id", width = 15)
    @ApiModelProperty(value = "主键id")
	private java.lang.Integer iId;
	/**模板类型:1.正文上报;2.正文下发;3.办文单;*/
	@Excel(name = "模板类型:1.正文上报;2.正文下发;3.办文单;", width = 15)
    @ApiModelProperty(value = "模板类型:1.正文上报;2.正文下发;3.办文单;")
	private java.lang.Integer iType;
	/**模版名称*/
	@Excel(name = "模版名称", width = 15)
    @ApiModelProperty(value = "模版名称")
	private java.lang.String sName;
	/**模版附件id*/
	@Excel(name = "模版附件id", width = 15)
    @ApiModelProperty(value = "模版附件id")
	private java.lang.Integer iFileId;
	/**红头拼接规则id*/
	@Excel(name = "红头拼接规则id", width = 15)
    @ApiModelProperty(value = "红头拼接规则id")
	private java.lang.Integer iTitleRuleId;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
	private java.lang.String sRemarks;
	/**创建者*/
	@Excel(name = "创建者", width = 15)
    @ApiModelProperty(value = "创建者")
	private java.lang.String sCreateBy;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private java.util.Date dCreateTime;
	/**修改者*/
	@Excel(name = "修改者", width = 15)
    @ApiModelProperty(value = "修改者")
	private java.lang.String sUpdateBy;
	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private java.util.Date dUpdateTime;

}
