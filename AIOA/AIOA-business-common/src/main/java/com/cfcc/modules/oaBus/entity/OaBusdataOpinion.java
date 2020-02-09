package com.cfcc.modules.oaBus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @Description: 业务数据意见表
 * @Author: jeecg-boot
 * @Date:   2019-11-18
 * @Version: V1.0
 */
@Data
@TableName("oa_busdata20_opinion")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="oa_busdata20_opinion对象", description="业务数据意见表")
public class OaBusdataOpinion {
    
	/**主键id*/
	@Excel(name = "主键id", width = 15)
    @ApiModelProperty(value = "主键id")
	@TableId(value = "i_id",type = IdType.AUTO)
	private java.lang.Integer iId;
	/**意见配置id*/
	@Excel(name = "意见配置id", width = 15)
    @ApiModelProperty(value = "意见配置id")
	private java.lang.Integer iOpinionSetId;
	/**业务功能配置ID*/
	@Excel(name = "业务功能配置ID", width = 15)
    @ApiModelProperty(value = "业务功能配置ID")
	private java.lang.Integer iBusFunctionId;
	/**业务数据id*/
	@Excel(name = "业务数据id", width = 15)
    @ApiModelProperty(value = "业务数据id")
	private java.lang.Integer iBusdataId;
	/**填写姓名*/
	@Excel(name = "填写姓名", width = 15)
    @ApiModelProperty(value = "填写姓名")
	private java.lang.String sName;
	/**意见填写人ID*/
	@Excel(name = "意见填写人ID", width = 15)
    @ApiModelProperty(value = "意见填写人ID")
	private java.lang.String iUserId;
	/**意见签署时间*/
	@Excel(name = "意见签署时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "意见签署时间")
	private java.util.Date dSign;
	/**意见*/
	@Excel(name = "意见", width = 15)
    @ApiModelProperty(value = "意见")
	private java.lang.String sOpinion;
	/**意见审批类型 1 签发 2 行外会签3 总部内会签 。。。。*/
	@Excel(name = "意见审批类型 1 签发 2 行外会签3 总部内会签 。。。。", width = 15)
    @ApiModelProperty(value = "意见审批类型 1 签发 2 行外会签3 总部内会签 。。。。")
	private java.lang.String sOpinionType;
	/**流程任务ID*/
	@Excel(name = "流程任务ID", width = 15)
    @ApiModelProperty(value = "流程任务ID")
	private java.lang.String sTaskId;
	/**流程任务名称*/
	@Excel(name = "流程任务名称", width = 15)
    @ApiModelProperty(value = "流程任务名称")
	private java.lang.String sTaskName;
	/**是否公开 1是 0 否*/
	@Excel(name = "是否公开 1是 0 否", width = 15)
    @ApiModelProperty(value = "是否公开 1是 0 否")
	private java.lang.String sOvert;
	/**序号(表单展现意见时用)*/
	@Excel(name = "序号(表单展现意见时用)", width = 15)
    @ApiModelProperty(value = "序号(表单展现意见时用)")
	private java.lang.Integer iOrder;
	/**sTpye*/
	@Excel(name = "sTpye", width = 15)
    @ApiModelProperty(value = "sTpye")
	private java.lang.String sTpye;
	/**用户部门ID*/
	@Excel(name = "用户部门ID", width = 15)
    @ApiModelProperty(value = "用户部门ID")
	private java.lang.String sDeptId;
	/**用户部门名称*/
	@Excel(name = "用户部门名称", width = 15)
    @ApiModelProperty(value = "用户部门名称")
	private java.lang.String sDeptName;
	/**流程任务定义ID*/
	@Excel(name = "流程任务定义ID", width = 15)
    @ApiModelProperty(value = "流程任务定义ID")
	private java.lang.String sTaskdefKey;

	/**
	 * 意见名称
	 */
	private String opinionName;
}
