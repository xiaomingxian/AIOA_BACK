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
 * @Description: 业务页面表
 * @Author: jeecg-boot
 * @Date:   2019-10-14
 * @Version: V1.0
 */
@Data
@TableName("oa_bus_page")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="oa_bus_page对象", description="业务页面表")
public class BusPage {
    
	/**主键id*/
	@Excel(name = "主键id", width = 15)
    @ApiModelProperty(value = "主键id")
	@TableId(value = "i_id",type = IdType.AUTO)
	private java.lang.Integer iId;
	/**详情页面路径（项目文件存放目录）*/
	@Excel(name = "详情页面路径（项目文件存放目录）", width = 15)
    @ApiModelProperty(value = "详情页面路径（项目文件存放目录）")
	private java.lang.String sPagePath;
	/**附件表id*/
	@Excel(name = "附件表id", width = 15)
    @ApiModelProperty(value = "附件表id")
	private java.lang.Integer iFileId;
	/**页面名称*/
	@Excel(name = "页面名称", width = 15)
    @ApiModelProperty(value = "页面名称")
	private java.lang.String sPageName;
	/**业务归类（1.信息发布、2.公文审批、3.事务审批、4.业务审批）*/
	@Excel(name = "业务归类（1.信息发布、2.公文审批、3.事务审批、4.业务审批）", width = 15)
    @ApiModelProperty(value = "业务归类（1.信息发布、2.公文审批、3.事务审批、4.业务审批）")
	private String iPapeType;
	/**页面模板概要说明*/
	@Excel(name = "页面模板概要说明", width = 15)
    @ApiModelProperty(value = "页面模板概要说明")
	private java.lang.String sPageRemarks;
	/**是否可以管理视频文件(1、0)*/
	@Excel(name = "是否可以管理视频文件(1、0)", width = 15)
    @ApiModelProperty(value = "是否可以管理视频文件(1、0)")
	private java.lang.Integer iIsVideo;
	/**是否包含主送、抄送、传阅(1、0)*/
	@Excel(name = "是否包含主送、抄送、传阅(1、0)", width = 15)
    @ApiModelProperty(value = "是否包含主送、抄送、传阅(1、0)")
	private java.lang.Integer iIsSend;
	/**是否包含抄报(1、0)*/
	@Excel(name = "是否包含抄报(1、0)", width = 15)
    @ApiModelProperty(value = "是否包含抄报(1、0)")
	private java.lang.Integer iIsCopy;
	/**是否是VIP模板(1、0)*/
	@Excel(name = "是否是VIP模板(1、0)", width = 15)
    @ApiModelProperty(value = "是否是VIP模板(1、0)")
	private java.lang.Integer iIsVip;
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

	/**工作流展示*/
	@Excel(name = "工作流展示", width = 15)
	@ApiModelProperty(value = "工作流展示")
	private java.lang.String actShow;
}
