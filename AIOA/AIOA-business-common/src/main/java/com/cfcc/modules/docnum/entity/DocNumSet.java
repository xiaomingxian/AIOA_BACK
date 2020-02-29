package com.cfcc.modules.docnum.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import net.bytebuddy.asm.Advice;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description: 文号配置
 * @Author: jeecg-boot
 * @Date:   2019-10-16
 * @Version: V1.0
 */
@Data
@TableName("oa_docnum")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="oa_docnum对象", description="文号配置")
public class DocNumSet {

	/**主键id*/
	@Excel(name = "主键id", width = 15)
    @ApiModelProperty(value = "主键id")
	private Integer iId;
	/**业务功能id*/
	@Excel(name = "业务功能id", width = 15)
    @ApiModelProperty(value = "业务功能id")
	private Integer iBusFunctionId;
	/**序号*/
	@Excel(name = "序号", width = 15)
    @ApiModelProperty(value = "序号")
	private Integer iOrder;
	/**文号名称*/
	@Excel(name = "文号名称", width = 15)
    @ApiModelProperty(value = "文号名称")
	private String sName;
	/**编号规则*/
	@Excel(name = "编号规则", width = 15)
    @ApiModelProperty(value = "编号规则")
	private String sDocRule;
	/**当前文号*/
	@Excel(name = "当前文号", width = 15)
    @ApiModelProperty(value = "当前文号")
	private Integer iDocNum;
	/**上报模版id*/
	@Excel(name = "上报模版id", width = 15)
    @ApiModelProperty(value = "上报模版id")
	private Integer iUtemplateId;
	/**下发模版id*/
	@Excel(name = "下发模版id", width = 15)
    @ApiModelProperty(value = "下发模版id")
	private Integer iDtemplateId;
	/**办文单模板id*/
	@Excel(name = "办文单模板id", width = 15)
    @ApiModelProperty(value = "办文单模板id")
	private Integer iAtemplateId;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
	private String sRemarks;
	/**创建者*/
	@Excel(name = "创建者", width = 15)
    @ApiModelProperty(value = "创建者")
	private String sCreateBy;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private java.util.Date dCreateTime;
	/**修改者*/
	@Excel(name = "修改者", width = 15)
    @ApiModelProperty(value = "修改者")
	private String sUpdateBy;
	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private java.util.Date dUpdateTime;

	@Excel(name = "所属模块", width = 20)
	@ApiModelProperty(value = "所属模块")
	private Integer iBusModelId;

	@Excel(name = "部门列表", width = 20)
	@ApiModelProperty(value = "部门列表")
	@TableField(exist = false)
	private String selecteddeparts;

	@Excel(name = "所属模块名称", width = 20)
	@ApiModelProperty(value = "所属模块名称")
	@TableField(exist = false)
	private String mName;

	@Excel(name = "所属业务名称", width = 20)
	@ApiModelProperty(value = "所属模块名称")
	@TableField(exist = false)
	private String fName;
}
