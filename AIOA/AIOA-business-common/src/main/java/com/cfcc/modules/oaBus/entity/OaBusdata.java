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
import org.springframework.data.annotation.TypeAlias;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description: 业务数据表
 * @Author: jeecg-boot
 * @Date:   2019-10-21
 * @Version: V1.0
 */
@Data
@TableName("oa_busdata10")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="oa_busdata1对象", description="业务数据表")
public class OaBusdata {
    
	/**主键id*/
	@TableId(value = "i_id",type = IdType.AUTO)
	@Excel(name = "主键id", width = 15)
    @ApiModelProperty(value = "主键id")
	private java.lang.Integer iId;
	/**业务模块id*/
	@TableField(value = "i_bus_model_id")
	@Excel(name = "业务模块id", width = 15)
    @ApiModelProperty(value = "业务模块id")
	private java.lang.Integer iBusModelId;
	/**业务功能id*/
	@TableField(value = "i_bus_function_id")
	@Excel(name = "业务功能id", width = 15)
    @ApiModelProperty(value = "业务功能id")
	private java.lang.Integer iBusFunctionId;
	/**流程实例id*/
	@TableField(value = "PROC_INST_ID")
	@Excel(name = "流程实例id", width = 15)
    @ApiModelProperty(value = "流程实例id")
	private java.lang.String procInstId;
	/**主送单位名称*/
	@TableField(value = "s_main_unit_names")
	@Excel(name = "主送单位名称", width = 15)
    @ApiModelProperty(value = "主送单位名称")
	private java.lang.String sMainUnitNames;
	/**抄送单位名称*/
	@TableField(value = "s_cc_unit_names")
	@Excel(name = "抄送单位名称", width = 15)
    @ApiModelProperty(value = "抄送单位名称")
	private java.lang.String sCcUnitNames;
	/**内部发送部门*/
	@TableField(value = "s_inside_deptnames")
	@Excel(name = "内部发送部门", width = 15)
    @ApiModelProperty(value = "内部发送部门")
	private java.lang.String sInsideDeptnames;
	/**抄报单位名称*/
	@TableField(value = "s_report_nuit_names")
	@Excel(name = "抄报单位名称", width = 15)
    @ApiModelProperty(value = "抄报单位名称")
	private java.lang.String sReportNuitNames;
	/**会签部门*/
	@TableField(value = "s_crc_deptnames")
	@Excel(name = "会签部门", width = 15)
    @ApiModelProperty(value = "会签部门")
	private java.lang.String sCrcDeptnames;
	/**标题*/
	@TableField(value = "s_title")
	@Excel(name = "标题", width = 15)
    @ApiModelProperty(value = "标题")
	private java.lang.String sTitle;
	/**左侧参数（页面头部）*/
	@TableField(value = "s_left_parameter")
	@Excel(name = "左侧参数（页面头部）", width = 15)
    @ApiModelProperty(value = "左侧参数（页面头部）")
	private java.lang.String sLeftParameter;
	/**是否插入机构*/
	@TableField(value = "s_unit_name")
	@Excel(name = "是否插入机构", width = 15)
    @ApiModelProperty(value = "是否插入机构")
	private java.lang.String sUnitName;
	/**是否插入部门*/
	@TableField(value = "s_dept_name")
	@Excel(name = "是否插入部门", width = 15)
    @ApiModelProperty(value = "是否插入部门")
	private java.lang.String sDeptName;
	/**插入参数（修改文头）（页面头部）*/
	@TableField(value = "s_middle_parameter")
	@Excel(name = "插入参数（修改文头）（页面头部）", width = 15)
    @ApiModelProperty(value = "插入参数（修改文头）（页面头部）")
	private java.lang.String sMiddleParameter;
	/**右侧参数（页面头部）*/
	@TableField(value = "s_right_parameter")
	@Excel(name = "右侧参数（页面头部）", width = 15)
    @ApiModelProperty(value = "右侧参数（页面头部）")
	private java.lang.String sRightParameter;
	/**来文字号*/
	//@Excel(name = "来文字号", width = 15)
    //@ApiModelProperty(value = "来文字号")
	//private java.lang.String sReceiveNum;
	/**文件字号*/
	@TableField(value = "s_file_num")
	@Excel(name = "文件字号", width = 15)
    @ApiModelProperty(value = "文件字号")
	private java.lang.String sFileNum;
	/**是否发布*/
	//@Excel(name = "是否发布", width = 15)
    //@ApiModelProperty(value = "是否发布")
	//private java.lang.String iIsRelease;
	/**是否办结*/
	@TableField(value = "i_is_state")
	@Excel(name = "是否办结", width = 15)
    @ApiModelProperty(value = "是否办结")
	private java.lang.Integer iIsState;
	/**是否封发日期*/
	@TableField(value = "i_is_sealdate")
	@Excel(name = "是否封发日期", width = 15)
    @ApiModelProperty(value = "是否封发日期")
	private java.lang.Integer iIsSealdate;
	/**是否排版*/
	@TableField(value = "i_is_typeset")
	@Excel(name = "是否排版", width = 15)
    @ApiModelProperty(value = "是否排版")
	private java.lang.Integer iIsTypeset;
	/**是否保存办文单*/
	@TableField(value = "i_is_approve")
	@Excel(name = "是否保存办文单", width = 15)
    @ApiModelProperty(value = "是否保存办文单")
	private java.lang.Integer iIsApprove;
	/**是否归档*/
	@TableField(value = "i_is_archives")
	@Excel(name = "是否归档", width = 15)
    @ApiModelProperty(value = "是否归档")
	private java.lang.Integer iIsArchives;
	/**是否已送全文检索*/
	@TableField(value = "i_is_es")
	@Excel(name = "是否已送全文检索", width = 15)
    @ApiModelProperty(value = "是否已送全文检索")
	private java.lang.Integer iIsEs;
	/**是否送公文传输*/
	@TableField(value = "i_is_send")
	@Excel(name = "是否送公文传输", width = 15)
    @ApiModelProperty(value = "是否送公文传输")
	private java.lang.Integer iIsSend;
	/**是否为临时文件*/
	@TableField(value = "i_is_display")
	@Excel(name = "是否为临时文件", width = 15)
    @ApiModelProperty(value = "是否为临时文件")
	private java.lang.Integer iIsDisplay;
	/**备注*/
	@TableField(value = "s_remarks")
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
	private java.lang.String sRemarks;
	/**创建时间-年*/
	@TableField(value = "i_create_year")
	@Excel(name = "创建时间-年", width = 15)
    @ApiModelProperty(value = "创建时间-年")
	private java.lang.Integer iCreateYear;
	/**创建时间-月*/
	@TableField(value = "i_create_month")
	@Excel(name = "创建时间-月", width = 15)
    @ApiModelProperty(value = "创建时间-月")
	private java.lang.Integer iCreateMonth;
	/**创建时间-日期*/
	@TableField(value = "i_create_day")
	@Excel(name = "创建时间-日期", width = 15)
    @ApiModelProperty(value = "创建时间-日期")
	private java.lang.Integer iCreateDay;
	/**创建者姓名*/
	@TableField(value = "s_create_name")
	@Excel(name = "创建者姓名", width = 15)
    @ApiModelProperty(value = "创建者姓名")
	private java.lang.String sCreateName;
	/**创建者id*/
	@TableField(value = "s_create_by")
	@Excel(name = "创建者id", width = 15)
    @ApiModelProperty(value = "创建者id")
	private java.lang.String sCreateBy;
	/**创建者部门id*/
	@TableField(value = "s_create_deptid")
	@Excel(name = "创建者部门id", width = 15)
    @ApiModelProperty(value = "创建者部门id")
	private java.lang.String sCreateDeptid;
	/**创建者机构id*/
	@TableField(value = "s_create_unitid")
	@Excel(name = "创建者单位id", width = 15)
    @ApiModelProperty(value = "创建者单位id")
	private java.lang.String sCreateUnitid;
	/**创建部门名称*/
	@TableField(value = "s_create_dept")
	@Excel(name = "创建部门名称", width = 15)
    @ApiModelProperty(value = "创建部门名称")
	private java.lang.String sCreateDept;
	/**创建时间*/
	@TableField(value = "d_create_time")
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private java.util.Date dCreateTime;
	/**修改时间*/
	@TableField(value = "d_update_time")
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private java.util.Date dUpdateTime;
	/**iBigint1*/
	@Excel(name = "iBigint1", width = 15)
    @ApiModelProperty(value = "iBigint1")
	private java.lang.Integer iBigint1;
	/**iBigint2*/
	@Excel(name = "iBigint2", width = 15)
    @ApiModelProperty(value = "iBigint2")
	private java.lang.Integer iBigint2;
	/**iBigint3*/
	@Excel(name = "iBigint3", width = 15)
    @ApiModelProperty(value = "iBigint3")
	private java.lang.Integer iBigint3;
	/**iBigint4*/
	@Excel(name = "iBigint4", width = 15)
    @ApiModelProperty(value = "iBigint4")
	private java.lang.Integer iBigint4;
	/**dDatetime1*/
	@Excel(name = "dDatetime1", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "dDatetime1")
	private java.util.Date dDatetime1;
	/**dDatetime2*/
	@Excel(name = "dDatetime2", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "dDatetime2")
	private java.util.Date dDatetime2;
	/**dDatetime3*/
	@Excel(name = "dDatetime3", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "dDatetime3")
	private java.util.Date dDatetime3;
	/**dDatetime4*/
	@Excel(name = "dDatetime4", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "dDatetime4")
	private java.util.Date dDatetime4;
	/**dDatetime5*/
	@Excel(name = "dDatetime5", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "dDatetime5")
	private java.util.Date dDatetime5;
	/**dDatetime6*/
	@Excel(name = "dDatetime6", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "dDatetime6")
	private java.util.Date dDatetime6;
	/**sVarchar1*/
	@Excel(name = "sVarchar1", width = 15)
    @ApiModelProperty(value = "sVarchar1")
	private java.lang.String sVarchar1;
	/**sVarchar2*/
	@Excel(name = "sVarchar2", width = 15)
    @ApiModelProperty(value = "sVarchar2")
	private java.lang.String sVarchar2;
	/**sVarchar3*/
	@Excel(name = "sVarchar3", width = 15)
    @ApiModelProperty(value = "sVarchar3")
	private java.lang.String sVarchar3;
	/**sVarchar4*/
	@Excel(name = "sVarchar4", width = 15)
    @ApiModelProperty(value = "sVarchar4")
	private java.lang.String sVarchar4;
	/**sVarchar5*/
	@Excel(name = "sVarchar5", width = 15)
    @ApiModelProperty(value = "sVarchar5")
	private java.lang.String sVarchar5;
	/**sVarchar6*/
	@Excel(name = "sVarchar6", width = 15)
    @ApiModelProperty(value = "sVarchar6")
	private java.lang.String sVarchar6;
	/**sVarchar7*/
	@Excel(name = "sVarchar7", width = 15)
    @ApiModelProperty(value = "sVarchar7")
	private java.lang.String sVarchar7;
	/**sVarchar8*/
	@Excel(name = "sVarchar8", width = 15)
    @ApiModelProperty(value = "sVarchar8")
	private java.lang.String sVarchar8;
	/**sVarchar9*/
	@Excel(name = "sVarchar9", width = 15)
    @ApiModelProperty(value = "sVarchar9")
	private java.lang.String sVarchar9;
	/**sVarchar10*/
	@Excel(name = "sVarchar10", width = 15)
    @ApiModelProperty(value = "sVarchar10")
	private java.lang.String sVarchar10;
	/**sVarchar11*/
	@Excel(name = "sVarchar11", width = 15)
    @ApiModelProperty(value = "sVarchar11")
	private java.lang.String sVarchar11;
	/**sVarchar12*/
	@Excel(name = "sVarchar12", width = 15)
    @ApiModelProperty(value = "sVarchar12")
	private java.lang.String sVarchar12;
	/**sVarchar13*/
	@Excel(name = "sVarchar13", width = 15)
    @ApiModelProperty(value = "sVarchar13")
	private java.lang.String sVarchar13;
	/**sVarchar14*/
	@Excel(name = "sVarchar14", width = 15)
    @ApiModelProperty(value = "sVarchar14")
	private java.lang.String sVarchar14;
	/**sVarchar15*/
	@Excel(name = "sVarchar15", width = 15)
    @ApiModelProperty(value = "sVarchar15")
	private java.lang.String sVarchar15;
	/**sVarchar16*/
	@Excel(name = "sVarchar16", width = 15)
    @ApiModelProperty(value = "sVarchar16")
	private java.lang.String sVarchar16;
	/**sVarchar17*/
	@Excel(name = "sVarchar17", width = 15)
    @ApiModelProperty(value = "sVarchar17")
	private java.lang.String sVarchar17;
	/**sVarchar18*/
	@Excel(name = "sVarchar18", width = 15)
    @ApiModelProperty(value = "sVarchar18")
	private java.lang.String sVarchar18;
	/**sVarchar19*/
	@Excel(name = "sVarchar19", width = 15)
    @ApiModelProperty(value = "sVarchar19")
	private java.lang.String sVarchar19;
	/**sVarchar20*/
	@Excel(name = "sVarchar20", width = 15)
    @ApiModelProperty(value = "sVarchar20")
	private java.lang.String sVarchar20;
	/**是否*/
	@TableField(value = "i_is_1")
	@Excel(name = "是否", width = 15)
    @ApiModelProperty(value = "是否")
	private java.lang.Integer iIs1;
	/**是否*/
	@TableField(value = "i_is_2")
	@Excel(name = "是否", width = 15)
    @ApiModelProperty(value = "是否")
	private java.lang.Integer iIs2;
	/**是否*/
	@TableField(value = "i_is_3")
	@Excel(name = "是否", width = 15)
    @ApiModelProperty(value = "是否")
	private java.lang.Integer iIs3;
	/**是否*/
	@TableField(value = "i_is_4")
	@Excel(name = "是否", width = 15)
    @ApiModelProperty(value = "是否")
	private java.lang.Integer iIs4;
	/**是否*/
	@TableField(value = "i_is_5")
	@Excel(name = "是否", width = 15)
    @ApiModelProperty(value = "是否")
	private java.lang.Integer iIs5;
	/**是否起草底稿*/
	@TableField(value = "i_is_draft")
	@Excel(name = "是否起草底稿", width = 15)
	@ApiModelProperty(value = "是否起草底稿")
	private java.lang.Integer iIsdraft;
	/**是否冲突*/
	@ApiModelProperty(value = "是否冲突")
	@TableField(exist = false)
	private java.lang.Integer conflict;

}
