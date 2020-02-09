package com.cfcc.modules.oabutton.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 按钮管理
 * @Author: jeecg-boot
 * @Date: 2019-10-24
 * @Version: V1.0
 */
@Data
@TableName("oa_button")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "oa_button对象", description = "按钮管理")
public class OaButton {


    private String sMethod;

    /**
     * 主键id
     */
    @Excel(name = "主键id", width = 15)
    @ApiModelProperty(value = "主键id")
    private java.lang.Integer iId;
    /**
     * 页面按钮id
     */
    @Excel(name = "页面按钮id", width = 15)
    @ApiModelProperty(value = "页面按钮id")
    private java.lang.String sBtnId;
    /**
     * 页面按钮name
     */
    @Excel(name = "页面按钮name", width = 15)
    @ApiModelProperty(value = "页面按钮name")
    private java.lang.String sBtnName;
    /**
     * 页面按钮value
     */
    @Excel(name = "页面按钮value", width = 15)
    @ApiModelProperty(value = "页面按钮value")
    private java.lang.String sBtnValue;
    /**
     * 互斥按钮id
     */
    @Excel(name = "互斥按钮id", width = 15)
    @ApiModelProperty(value = "互斥按钮id")
    private java.lang.String sExcbuttonId;
    /**
     * 互斥状态字段（如正文排版、封发日期）
     */
    @Excel(name = "互斥状态字段（如正文排版、封发日期）", width = 15)
    @ApiModelProperty(value = "互斥状态字段（如正文排版、封发日期）")
    private java.lang.String sExcfield;
    /**
     * 是否刷新页面
     */
    @Excel(name = "是否刷新页面", width = 15)
    @ApiModelProperty(value = "是否刷新页面")
    private java.lang.Integer iIsRefresh;
    /**
     * 是否需要校验按钮对应的业务操作（下一任务（保存）时，校验该按钮业务操作）
     */
    @Excel(name = "是否需要校验按钮对应的业务操作（下一任务（保存）时，校验该按钮业务操作）", width = 15)
    @ApiModelProperty(value = "是否需要校验按钮对应的业务操作（下一任务（保存）时，校验该按钮业务操作）")
    private java.lang.Integer iIsCheckbus;
    /**
     * 是否是维护按钮
     */
    @Excel(name = "是否是维护按钮", width = 15)
    @ApiModelProperty(value = "是否是维护按钮")
    private Boolean iIsDefend;
    /**
     * 是否操作ntko控件
     */
    @Excel(name = "是否操作ntko控件", width = 15)
    @ApiModelProperty(value = "是否操作ntko控件")
    private java.lang.Integer iIsNtko;
    /**
     * 是否允许ntko打印
     */
    @Excel(name = "是否允许ntko打印", width = 15)
    @ApiModelProperty(value = "是否允许ntko打印")
    private java.lang.Integer iIsPrint;
    /**
     * 是否允许ntko打印预览
     */
    @Excel(name = "是否允许ntko打印预览", width = 15)
    @ApiModelProperty(value = "是否允许ntko打印预览")
    private java.lang.Integer iIsPrintpreview;
    /**
     * 是否允许ntko另存
     */
    @Excel(name = "是否允许ntko另存", width = 15)
    @ApiModelProperty(value = "是否允许ntko另存")
    private java.lang.Integer iIsSaveas;
    /**
     * 是否允许ntko保存
     */
    @Excel(name = "是否允许ntko保存", width = 15)
    @ApiModelProperty(value = "是否允许ntko保存")
    private java.lang.Integer iIsSave;
    /**
     * 是否允许ntko新建
     */
    @Excel(name = "是否允许ntko新建", width = 15)
    @ApiModelProperty(value = "是否允许ntko新建")
    private java.lang.Integer iIsNew;
    /**
     * 是否允许ntko禁止文件菜单的关闭项
     */
    @Excel(name = "是否允许ntko禁止文件菜单的关闭项", width = 15)
    @ApiModelProperty(value = "是否允许ntko禁止文件菜单的关闭项")
    private java.lang.Integer iIsClose;
    /**
     * 是否允许ntko打开
     */
    @Excel(name = "是否允许ntko打开", width = 15)
    @ApiModelProperty(value = "是否允许ntko打开")
    private java.lang.Integer iIsOpen;
    /**
     * 是否允许ntko编辑
     */
    @Excel(name = "是否允许ntko编辑", width = 15)
    @ApiModelProperty(value = "是否允许ntko编辑")
    private java.lang.Integer iIsEdit;
    /**
     * 是否允许ntko拷贝
     */
    @Excel(name = "是否允许ntko拷贝", width = 15)
    @ApiModelProperty(value = "是否允许ntko拷贝")
    private java.lang.Integer iIsCopy;
    /**
     * 是否ntko保存痕迹
     */
    @Excel(name = "是否ntko保存痕迹", width = 15)
    @ApiModelProperty(value = "是否ntko保存痕迹")
    private java.lang.Integer iIsSaverevision;
    /**
     * 是否允许ntko显示痕迹
     */
    @Excel(name = "是否允许ntko显示痕迹", width = 15)
    @ApiModelProperty(value = "是否允许ntko显示痕迹")
    private java.lang.Integer iIsShowrevision;
    /**
     * 是否ntko套打
     */
    @Excel(name = "是否ntko套打", width = 15)
    @ApiModelProperty(value = "是否ntko套打")
    private java.lang.Integer iIsAddread;

    @Excel(name = "是否是基础页面", width = 15)
    @ApiModelProperty(value = "是否是基础页面")
    private Boolean iIsBase;
}
