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

import java.util.Set;

/**
 * @Description: 日程管理表
 * @Author: jeecg-boot
 * @Date: 2019-11-21
 * @Version: V1.0
 */
@Data
@TableName("oa_calendar")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "oa_calendar对象", description = "日程管理表")
public class oaCalendar {

    /**
     * 主键id
     */
    @Excel(name = "主键id", width = 15)
    @ApiModelProperty(value = "主键id")
    @TableField(value = "i_id")
    @TableId(value = "i_id", type = IdType.AUTO)
    private java.lang.Integer iId;
    /**
     * 日程内容
     */
    @Excel(name = "日程内容", width = 15)
    @ApiModelProperty(value = "日程内容")
    private java.lang.String sTitle;
    /**
     * 日程对象（默认自己，可多选，只记录名称）
     */
    @Excel(name = "日程对象", width = 15)
    @ApiModelProperty(value = "日程对象（默认自己，可多选，只记录名称）")
    private java.lang.String sUserNames;
    /**
     * 日程发生地点
     */
    @Excel(name = "日程发生地点", width = 15)
    @ApiModelProperty(value = "日程发生地点")
    private java.lang.String sAddress;
    //导出表格数字转化成汉字
    @Excel(name = "是否领导", width = 15)
    @TableField(exist = false)
    private java.lang.String leader;
    @Excel(name = "是否顶置", width = 15)
    @TableField(exist = false)
    private java.lang.String top;
    @Excel(name = "公开类型", width = 15)
    @TableField(exist = false)
    private java.lang.String open;
    @Excel(name = "消息提示", width = 15)
    @TableField(exist = false)
    private java.lang.String message;
    @TableField(value = "task_id")
    private java.lang.String taskId;
    @TableField(value = "task_user_id")
    private java.lang.String taskUserId;

    @TableField(value = "i_is_state")
    private java.lang.String state;

    /**
     * 是否置顶
     */
    @ApiModelProperty(value = "是否置顶")
    private java.lang.Integer iIsTop;
    /**
     * 是否领导日程
     */
    @ApiModelProperty(value = "是否领导日程")
    private java.lang.Integer iIsLeader;
    /**
     * 即时消息提醒类型:1.10分钟前 2.30分钟前;3.1小时前;4.2小时前;
     */
    @ApiModelProperty(value = "即时消息提醒类型:1.10分钟前 2.30分钟前;3.1小时前;4.2小时前;")
    private java.lang.Integer iRemindType;
    /**
     * 公开类型:1.全行 2.分管;3.部门内;
     */
    @ApiModelProperty(value = "公开类型:1.全行 2.分管;3.部门内;")
    private java.lang.Integer iOpenType;
    /**
     * 开始时间
     */
    @Excel(name = "开始时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    private java.util.Date dStartTime;
    /**
     * 结束时间
     */
    @Excel(name = "结束时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
    private java.util.Date dEndTime;
    /**
     * 创建者
     */
    @Excel(name = "创建者", width = 15)
    @ApiModelProperty(value = "创建者")
    private java.lang.String sCreateBy;
    /**
     * 创建时间
     */
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date dCreateTime;
    /**
     * 业务模块id
     */
    @Excel(name = "业务模块id", width = 15)
    @ApiModelProperty(value = "业务模块id")
    private java.lang.Integer iBusModelId;
    /**
     * 业务功能id
     */
    @Excel(name = "业务功能id", width = 15)
    @ApiModelProperty(value = "业务功能id")
    private java.lang.Integer iBusFunctionId;
    /**
     * 业务实例数据id
     */
    @Excel(name = "业务实例数据id", width = 15)
    @ApiModelProperty(value = "业务实例数据id")
    private java.lang.Integer iFunDataId;

    @TableField(exist = false)
    private String sUserName;

    @TableField(exist = false)
    private Set sUserNameid;

    @TableField(exist = false)
    private java.lang.String Strdate;

    @TableField(exist = false)
    private java.lang.String Enddate;

    @TableField(exist = false)
    private java.lang.String date;

    @TableField(exist = false)
    private java.lang.String username;

    @TableField(exist = false)
    private java.lang.Integer more;


}
