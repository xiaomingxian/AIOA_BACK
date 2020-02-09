package com.cfcc.modules.oaTeamwork.entity;

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
 * @Description: 个人协同办公业务配置分类
 * @Author: jeecg-boot
 * @Date:   2019-12-30
 * @Version: V1.0
 */
@Data
@TableName("oa_teamwork")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="oa_teamwork对象", description="个人协同办公业务配置分类")
public class oaTeamwork {
    
	/**主键id*/
	@Excel(name = "主键id", width = 15)
    @ApiModelProperty(value = "主键id")
	private Integer iId;
	/**协同业务名称*/
	@Excel(name = "协同业务名称", width = 15)
    @ApiModelProperty(value = "协同业务名称")
	private String sTeamworkName;
	/**描述*/
	@Excel(name = "描述", width = 15)
    @ApiModelProperty(value = "描述")
	private String description;
	/**创建者id*/
	@Excel(name = "创建者id", width = 15)
    @ApiModelProperty(value = "创建者id")
	private String sCreateBy;
	/**创建者部门id*/
	@Excel(name = "创建者部门id", width = 15)
    @ApiModelProperty(value = "创建者部门id")
	private String sCreateDeptid;
	/**创建者机构id*/
	@Excel(name = "创建者机构id", width = 15)
    @ApiModelProperty(value = "创建者机构id")
	private String sCreateUnitid;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date dCreateTime;
	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private Date dUpdateTime;
	/**版本号(和oa_teamwork_set版本号一致）*/
	@Excel(name = "版本号(和oa_teamwork_set版本号一致）", width = 15)
    @ApiModelProperty(value = "版本号(和oa_teamwork_set版本号一致）")
	private Integer iVersion;

	@TableField(exist = false)
	private java.lang.String TeamworkName; //步骤功能

	@TableField(exist = false)
	private java.lang.Integer busFunctionId; //步骤功能



}
