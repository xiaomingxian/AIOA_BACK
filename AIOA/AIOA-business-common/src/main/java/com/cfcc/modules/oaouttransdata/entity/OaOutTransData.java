package com.cfcc.modules.oaouttransdata.entity;

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
 * @Description: 归档送公文传输
 * @Author: jeecg-boot
 * @Date:   2019-10-18
 * @Version: V1.0
 */
@Data
@TableName("oa_out_trans_data")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="oa_out_trans_data对象", description="归档送公文传输")
public class OaOutTransData {
    
	/**iid*/
	@Excel(name = "iid", width = 15)
    @ApiModelProperty(value = "iid")
	private java.lang.Integer iid;
	/**syear*/
	@Excel(name = "syear", width = 15)
    @ApiModelProperty(value = "syear")
	private java.lang.String syear;
	/**文件类别  1 发文、2 收文、3 签报 */
	@Excel(name = "文件类别  1 发文、2 收文、3 签报 ", width = 15)
    @ApiModelProperty(value = "文件类别  1 发文、2 收文、3 签报 ")
	private java.lang.String suppertype;
	/**文件小类别  1 行发文、2 办公室发文、3 行收文*/
	@Excel(name = "文件小类别  1 行发文、2 办公室发文、3 行收文", width = 15)
    @ApiModelProperty(value = "文件小类别  1 行发文、2 办公室发文、3 行收文")
	private java.lang.String stype;
	/**文件文字号，如：广州银发〔2005〕2号*/
	@Excel(name = "文件文字号，如：广州银发〔2005〕2号", width = 15)
    @ApiModelProperty(value = "文件文字号，如：广州银发〔2005〕2号")
	private java.lang.String sfileno;
	/**机关代字，如广州银发*/
	@Excel(name = "机关代字，如广州银发", width = 15)
    @ApiModelProperty(value = "机关代字，如广州银发")
	private java.lang.String scategory;
	/**secret*/
	@Excel(name = "secret", width = 15)
    @ApiModelProperty(value = "secret")
	private java.lang.String secret;
	/**semergency*/
	@Excel(name = "semergency", width = 15)
    @ApiModelProperty(value = "semergency")
	private java.lang.String semergency;
	/**signers*/
	@Excel(name = "signers", width = 15)
    @ApiModelProperty(value = "signers")
	private java.lang.String signers;
	/**dissuedate*/
	@Excel(name = "dissuedate", width = 15)
    @ApiModelProperty(value = "dissuedate")
	private java.lang.String dissuedate;
	/**sdraft*/
	@Excel(name = "sdraft", width = 15)
    @ApiModelProperty(value = "sdraft")
	private java.lang.String sdraft;
	/**tcompose*/
	@Excel(name = "tcompose", width = 15)
    @ApiModelProperty(value = "tcompose")
	private java.lang.String tcompose;
	/**文件的发文机关或署名者*/
	@Excel(name = "文件的发文机关或署名者", width = 15)
    @ApiModelProperty(value = "文件的发文机关或署名者")
	private java.lang.String sdepartment;
	/**主办部门 主办部门，文件经办部门*/
	@Excel(name = "主办部门 主办部门，文件经办部门", width = 15)
    @ApiModelProperty(value = "主办部门 主办部门，文件经办部门")
	private java.lang.String smaindept;
	/**stitle*/
	@Excel(name = "stitle", width = 15)
    @ApiModelProperty(value = "stitle")
	private java.lang.String stitle;
	/**ssendto*/
	@Excel(name = "ssendto", width = 15)
    @ApiModelProperty(value = "ssendto")
	private java.lang.String ssendto;
	/**scopyto*/
	@Excel(name = "scopyto", width = 15)
    @ApiModelProperty(value = "scopyto")
	private java.lang.String scopyto;
	/**sinternalto*/
	@Excel(name = "sinternalto", width = 15)
    @ApiModelProperty(value = "sinternalto")
	private java.lang.String sinternalto;
	/**ipages*/
	@Excel(name = "ipages", width = 15)
    @ApiModelProperty(value = "ipages")
	private java.lang.String ipages;
	/**保密期限：文件的实际保密期限，以月为单位*/
	@Excel(name = "保密期限：文件的实际保密期限，以月为单位", width = 15)
    @ApiModelProperty(value = "保密期限：文件的实际保密期限，以月为单位")
	private java.lang.String itimelimit;
	/**smemo*/
	@Excel(name = "smemo", width = 15)
    @ApiModelProperty(value = "smemo")
	private java.lang.String smemo;
	/**会签处室、会办或协办处室*/
	@Excel(name = "会签处室、会办或协办处室", width = 15)
    @ApiModelProperty(value = "会签处室、会办或协办处室")
	private java.lang.String sapproveddepts;
	/**发文文件红头，如：中国人民银行广州分行*/
	@Excel(name = "发文文件红头，如：中国人民银行广州分行", width = 15)
    @ApiModelProperty(value = "发文文件红头，如：中国人民银行广州分行")
	private java.lang.String shead;
	/**sdisporg*/
	@Excel(name = "sdisporg", width = 15)
    @ApiModelProperty(value = "sdisporg")
	private java.lang.String sdisporg;
	/**收文文件类型，如：函等*/
	@Excel(name = "收文文件类型，如：函等", width = 15)
    @ApiModelProperty(value = "收文文件类型，如：函等")
	private java.lang.String sfromtype;
	/**sseqno*/
	@Excel(name = "sseqno", width = 15)
    @ApiModelProperty(value = "sseqno")
	private java.lang.String sseqno;
	/**srecvno*/
	@Excel(name = "srecvno", width = 15)
    @ApiModelProperty(value = "srecvno")
	private java.lang.String srecvno;
	/**收文文件的办文方式，如办文、阅文*/
	@Excel(name = "收文文件的办文方式，如办文、阅文", width = 15)
    @ApiModelProperty(value = "收文文件的办文方式，如办文、阅文")
	private java.lang.String sdomode;
	/**dtimelimit*/
	@Excel(name = "dtimelimit", width = 15)
    @ApiModelProperty(value = "dtimelimit")
	private java.lang.String dtimelimit;
	/**sdofileno*/
	@Excel(name = "sdofileno", width = 15)
    @ApiModelProperty(value = "sdofileno")
	private java.lang.String sdofileno;
	/**业务模块记录ID*/
	@Excel(name = "业务模块记录ID", width = 15)
    @ApiModelProperty(value = "业务模块记录ID")
	private java.lang.String ibusmodeid;
	/**发文正文/收文正文/签报正文*/
	@Excel(name = "发文正文/收文正文/签报正文", width = 15)
    @ApiModelProperty(value = "发文正文/收文正文/签报正文")
	private java.lang.String srbody;
	/**发文附件/收文附件/签报附件*/
	@Excel(name = "发文附件/收文附件/签报附件", width = 15)
    @ApiModelProperty(value = "发文附件/收文附件/签报附件")
	private java.lang.String srattachment;
	/**发文办文单/收文办文单/签报文办文单*/
	@Excel(name = "发文办文单/收文办文单/签报文办文单", width = 15)
    @ApiModelProperty(value = "发文办文单/收文办文单/签报文办文单")
	private java.lang.String srcover;
	/**处理状态 0 未归档 1 已归档*/
	@Excel(name = "处理状态 0 未归档 1 已归档", width = 15)
    @ApiModelProperty(value = "处理状态 0 未归档 1 已归档")
	private java.lang.String sstate;
}
