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
 * @Description: 稿纸头配置
 * @Author: jeecg-boot
 * @Date:   2019-10-14
 * @Version: V1.0
 */
@Data
@TableName("oa_title_rule")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="oa_title_rule对象", description="稿纸头配置")
public class PaperTitleSetting {
    
	/**主键id*/
	@Excel(name = "主键id", width = 15)
    @ApiModelProperty(value = "主键id")
	private java.lang.Integer iId;
	/**稿纸头标题*/
	@Excel(name = "稿纸头标题", width = 15)
    @ApiModelProperty(value = "稿纸头标题")
	private java.lang.String sTitleName;
	/**是否稿纸头前缀（直接取机构名称）*/
	@Excel(name = "是否稿纸头前缀（直接取机构名称）", width = 15)
    @ApiModelProperty(value = "是否稿纸头前缀（直接取机构名称）")
	private java.lang.Integer iIsUnit;
	/**是否插入部门*/
	@Excel(name = "是否插入部门", width = 15)
    @ApiModelProperty(value = "是否插入部门")
	private java.lang.Integer iIsDept;
	/**稿纸头前缀*/
	@Excel(name = "稿纸头前缀", width = 15)
    @ApiModelProperty(value = "稿纸头前缀")
	private java.lang.String sLeftParameter;
	/**稿纸头中间插入*/
	@Excel(name = "稿纸头中间插入", width = 15)
    @ApiModelProperty(value = "稿纸头中间插入")
	private java.lang.String sMddleParameter;
	/**稿纸头后缀*/
	@Excel(name = "稿纸头后缀", width = 15)
    @ApiModelProperty(value = "稿纸头后缀")
	private java.lang.String sRightParameter;
	/**其他位置固定显示参数*/
	@Excel(name = "其他位置固定显示参数", width = 15)
    @ApiModelProperty(value = "其他位置固定显示参数")
	private java.lang.String sOtherParameter;
	/**是否为默认稿纸头*/
	@Excel(name = "是否为默认稿纸头", width = 15)
    @ApiModelProperty(value = "是否为默认稿纸头")
	private java.lang.Integer iIsDefault;
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
