package com.cfcc.modules.oaBus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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

import java.awt.*;

/**
 * @Description: 附件表
 * @Author: jeecg-boot
 * @Date:   2019-10-21
 * @Version: V1.0
 */
@Data
@TableName("oa_file")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="oa_file对象", description="附件表")
public class OaFile {
    
	/**主键id*/
	@Excel(name = "主键id", width = 15)
    @ApiModelProperty(value = "主键id")
	@TableId(value = "i_id",type = IdType.AUTO)
	private java.lang.Integer iId;
	/**附件所属数据所在表*/
	@Excel(name = "附件所属数据所在表", width = 15)
    @ApiModelProperty(value = "附件所属数据所在表")
	private java.lang.String sTable;
	/**附件所属数据id*/
	@Excel(name = "附件所属数据id", width = 15)
    @ApiModelProperty(value = "附件所属数据id")
	private java.lang.Integer iTableId;
	/**附件类型（1 底稿 2 正文 3办文单4 附件 5背景材料 6办文依据）*/
	@Excel(name = "附件类型（1 底稿 2 正文 3办文单4 附件 5背景材料 6办文依据）", width = 15)
    @ApiModelProperty(value = "附件类型（1 底稿 2 正文 3办文单4 附件 5背景材料 6办文依据）")
	private java.lang.String sFileType;
	/**序号*/
	@Excel(name = "序号", width = 15)
    @ApiModelProperty(value = "序号")
	private java.lang.Integer iOrder;
	/**附件名*/
	@Excel(name = "附件名", width = 15)
    @ApiModelProperty(value = "附件名")
	private java.lang.String sFileName;
	/**附件存储路径*/
	@Excel(name = "附件存储路径", width = 15)
    @ApiModelProperty(value = "附件存储路径")
	private java.lang.String sFilePath;
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

	/*文件内容*/
	@Excel(name = "文件内容")
	@ApiModelProperty(value = "文件内容")
	@TableField(exist = false)
	private java.lang.String sContent;

	/*文件内容*/
	@Excel(name = "文件标题")
	@ApiModelProperty(value = "文件标题")
	@TableField(exist = false)
	private java.lang.String sTitle;
}
