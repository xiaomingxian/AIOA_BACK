package com.cfcc.modules.docnum.entity;

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

@Data
@TableName("oa_busdata10")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="oa_busdata1对象", description="业务数据表")
public class DocNumExport {

    /**序号*/
    @Excel(name = "序号", width = 15)
    @ApiModelProperty(value = "序号")
    private Integer iId;

    /**序号*/
    @Excel(name = "业务名称", width = 15)
    @ApiModelProperty(value = "业务功能")
    private java.lang.String sLeftParameter;

    /**文件字号*/
    @TableField(value = "s_file_num")
    @Excel(name = "文件字号", width = 20)
    @ApiModelProperty(value = "文件字号")
    private java.lang.String sFileNum;

    /**创建时间-年*/
    @TableField(value = "i_create_year")
    @Excel(name = "年份", width = 15)
    @ApiModelProperty(value = "创建时间-年")
    private java.lang.Integer iCreateYear;

    /**标题*/
    @TableField(value = "s_title")
    @Excel(name = "标题", width = 15)
    @ApiModelProperty(value = "标题")
    private java.lang.String sTitle;

    /**创建部门名称*/
    @TableField(value = "s_create_dept")
    @Excel(name = "主办部门", width = 15)
    @ApiModelProperty(value = "主办部门")
    private java.lang.String sCreateDept;

    /**创建者姓名*/
    @TableField(value = "s_create_name")
    @Excel(name = "拟稿人", width = 15)
    @ApiModelProperty(value = "拟稿人")
    private java.lang.String sCreateName;

    /**创建时间*/
    @TableField(value = "d_create_time")
    @Excel(name = "拟稿时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date dCreateTime;

}
