package com.cfcc.modules.docnum.entity;

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
 * @Description: 文号管理表
 * @Author: jeecg-boot
 * @Date:   2019-11-18
 * @Version: V1.0
 */
@Data
@TableName("oa_docnum_manage")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="oa_docnum_manage对象", description="文号管理表")
public class DocNumManage {
    
	/**主键id*/
	@Excel(name = "主键id", width = 15)
    @ApiModelProperty(value = "主键id")
	private java.lang.Integer iId;
	/**文号id*/
	@Excel(name = "文号id", width = 15)
    @ApiModelProperty(value = "文号id")
	private java.lang.Integer iDocnumId;
	/**年度*/
	@Excel(name = "年度", width = 15)
    @ApiModelProperty(value = "年度")
	private java.lang.String sYear;
	/**编号*/
	@Excel(name = "编号", width = 15)
    @ApiModelProperty(value = "编号")
	private java.lang.Integer iDocNum;
	/**业务数据表*/
	@Excel(name = "业务数据表", width = 15)
    @ApiModelProperty(value = "业务数据表")
	private java.lang.String sBusdataTable;
	/**业务数据id，删除文号用-1，正常记录id,未编号null*/
	@Excel(name = "业务数据id，删除文号用-1，正常记录id,未编号null", width = 15)
    @ApiModelProperty(value = "业务数据id，删除文号用-1，正常记录id,未编号null")
	private java.lang.Integer iBusdataId;
	/**发送对象：1 上报 2 下发*/
	@Excel(name = "发送对象：1 上报 2 下发", width = 15)
    @ApiModelProperty(value = "发送对象：1 上报 2 下发")
	private java.lang.Integer iSendObj;
}
