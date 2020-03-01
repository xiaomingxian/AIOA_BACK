package com.cfcc.modules.oaBus.entity;

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
 * @Description: 传输日志表
 * @Author: jeecg-boot
 * @Date:   2020-03-01
 * @Version: V1.0
 */
@Data
@TableName("oa_out_log")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="oa_out_log对象", description="传输日志表")
public class OaOutLog {
    
	/**iId*/
	@Excel(name = "iId", width = 15)
    @ApiModelProperty(value = "iId")
	private Integer iId;
	/**发送者id*/
	@Excel(name = "发送者id", width = 15)
    @ApiModelProperty(value = "发送者id")
	private String sSendBy;
	/**发送公文--名称*/
	@Excel(name = "发送公文--名称", width = 15)
    @ApiModelProperty(value = "发送公文--名称")
	private String sSendName;
	/**业务模块id*/
	@Excel(name = "业务模块id", width = 15)
    @ApiModelProperty(value = "业务模块id")
	private Integer iBusModelId;
	/**业务功能id*/
	@Excel(name = "业务功能id", width = 15)
    @ApiModelProperty(value = "业务功能id")
	private Integer iBusFunctionId;
	/**业务数据表*/
	@Excel(name = "业务数据表", width = 15)
    @ApiModelProperty(value = "业务数据表")
	private String sBusdataTable;
	/**业务数据id*/
	@Excel(name = "业务数据id", width = 15)
    @ApiModelProperty(value = "业务数据id")
	private Integer iBusdataId;
	/** s_rec_unitid类型：1为机构id；2为数据字典itemid*/
	@Excel(name = " s_rec_unitid类型：1为机构id；2为数据字典itemid", width = 15)
    @ApiModelProperty(value = " s_rec_unitid类型：1为机构id；2为数据字典itemid")
	private Integer iType;
	/**接收者机构对应的数据字典id（机构id）*/
	@Excel(name = "接收者机构对应的数据字典id（机构id）", width = 15)
    @ApiModelProperty(value = "接收者机构对应的数据字典id（机构id）")
	private String sRecUnitid;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date dCreateTime;
}
