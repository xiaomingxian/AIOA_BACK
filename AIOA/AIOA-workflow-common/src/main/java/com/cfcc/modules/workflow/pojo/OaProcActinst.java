package com.cfcc.modules.workflow.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 流程节点配置
 * @Author: jeecg-boot
 * @Date: 2019-11-01
 * @Version: V1.0
 */
@Data
@TableName("oa_proc_actinst")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "oa_proc_actinst对象", description = "流程节点配置")
public class OaProcActinst extends Model<OaProcActinst> {

    /**
     * 主键id
     */
    @Excel(name = "主键id", width = 15)
    @ApiModelProperty(value = "主键id")
    @TableId(value = "i_id", type = IdType.AUTO)
    private java.lang.Integer iId;
    /**
     * 流程定义KEY
     */
    @Excel(name = "流程定义KEY", width = 15)
    @ApiModelProperty(value = "流程定义KEY")
    private java.lang.String procDefKey;
    /**
     * 流程定义的活动节点ID
     */
    @Excel(name = "流程定义的活动节点ID", width = 15)
    @ApiModelProperty(value = "流程定义的活动节点ID")
    private java.lang.String actId;
    /**
     * 流程定义的活动节点NAME
     */
    @Excel(name = "流程定义的活动节点NAME", width = 15)
    @ApiModelProperty(value = "流程定义的活动节点NAME")
    private java.lang.String actName;
    /**
     * 流程定义的活动节点TYPE
     */
    @Excel(name = "流程定义的活动节点TYPE", width = 15)
    @ApiModelProperty(value = "流程定义的活动节点TYPE")
    private java.lang.String actType;
    /**
     * 候选人角色id
     */
    @Excel(name = "候选人角色id", width = 15)
    @ApiModelProperty(value = "候选人角色id")
    private java.lang.String candidates;
    /**
     * 是否多选
     */
    @Excel(name = "是否多选", width = 15)
    @ApiModelProperty(value = "是否多选")

    private boolean isMultAssignee;
    /**
     * 候选人选择范围
     */
    @Excel(name = "候选人选择范围", width = 15)
    @ApiModelProperty(value = "候选人选择范围")
    private java.lang.String roleScope;
    /**
     * 用户或角色
     */
    @Excel(name = "用户或角色", width = 15)
    @ApiModelProperty(value = "用户或角色")
    private java.lang.String userOrRole;

    /**
     * 部门选择
     */
    @Excel(name = "部门选择", width = 15)
    @ApiModelProperty(value = "如果是部门选择都选哪些部门")
    private java.lang.String depts;

    /**
     * 是否填写完意见就完成任务
     */
    @Excel(name = "是否填写完意见就完成任务", width = 15)
    @ApiModelProperty(value = "是否填写完意见就完成任务")
    private boolean isCompleteTask;

    @TableField(value = "is_can_add")
    private Boolean isCanAdd;//是否可追加

    @TableField(value = "is_dept_finish")
    private Boolean isDeptFinish;//是否部门完成

    @TableField(value = "record_currentuser")
    private Boolean recordCurrentuser;//是否记录当前用户


    @TableField(value = "s_signer")
    private Boolean signer;//是否是签发人


    /**
     * createTime
     */
    @Excel(name = "createTime", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "createTime")
    private java.util.Date createTime;
    /**
     * updateTime
     */
    @Excel(name = "updateTime", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "updateTime")
    private java.util.Date updateTime;


    @TableField(value = "record_key")
    private String recordKey;//记录但前用户的key


    @TableField(value = "user_record_val")
    private Boolean userRecordVal;//记录但前用户的key


    @TableField(exist = false)
    private List<String> recordKeys;


    public List<String> getDeptsList() {
        ArrayList<String> deptsAll = new ArrayList<>();
        if (depts != null && depts.contains(",")) {
            deptsAll.addAll(Arrays.asList(depts.split(",")));
        }
        if (depts != null && !depts.contains(",")) {
            deptsAll.add(depts);
        }
        return deptsAll;
    }


    @Override
    protected Serializable pkVal() {
        return this.iId;
    }


}
