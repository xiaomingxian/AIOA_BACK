package com.cfcc.modules.oaBus.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * @Description: 业务页面详情表（实际业务字段含义说明）
 * @Author: jeecg-boot
 * @Date:   2019-10-18
 * @Version: V1.0
 */
@Data
@TableName("oa_bus_page_detail")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="oa_bus_page_detail对象", description="业务页面详情表（实际业务字段含义说明）")
public class BusPageDetail /* extends Model<BusPageDetail>*/ {
    
	/**主键id*/
	@Excel(name = "主键id", width = 15)
    @ApiModelProperty(value = "主键id")
	//@TableField(value = "i_id")
	@TableId(value = "i_id",type = IdType.AUTO)
	private java.lang.Integer iId;
	/**业务页面id*/
	@Excel(name = "业务页面id", width = 15)
    @ApiModelProperty(value = "业务页面id")
	private java.lang.Integer iBusPageId;
	/**业务功能id*/
	@Excel(name = "业务功能id", width = 15)
    @ApiModelProperty(value = "业务功能id")
	private java.lang.Integer iBusFunctionId;

	/**业务数据表名*/
	@Excel(name = "业务数据表名", width = 15)
    @ApiModelProperty(value = "业务数据表名")
	private java.lang.String sBusdataTable;
	/**业务数据表列名*/
	@Excel(name = "业务数据表列名", width = 15)
    @ApiModelProperty(value = "业务数据表列名")
	private java.lang.String sTableColumn;
	/**排序order*/
	@Excel(name = "排序order", width = 15)
	@ApiModelProperty(value = "排序order")
	private java.lang.Integer iOrder;
	/**业务数据表列名*/
	@Excel(name = "数据字典类别ID", width = 15)
	@ApiModelProperty(value = "数据字典类别ID")
	@TableField(updateStrategy = FieldStrategy.IGNORED)
	private java.lang.String sDictId;

	/**页面显示的数据项名*/
	@Excel(name = "页面显示的数据项名", width = 15)
    @ApiModelProperty(value = "页面显示的数据项名")
	private java.lang.String sColumnName;
	/**数据字典columnType）1：输入框；2：下拉框；3：文本框；4：日期框；5：双日期框；6：单选框；*/
	@Excel(name = "数据字典columnType）1：输入框；2：下拉框；3：文本框；4：日期框；5：双日期框；6：单选框；", width = 15)
    @ApiModelProperty(value = "数据字典columnType）1：输入框；2：下拉框；3：文本框；4：日期框；5：双日期框；6：单选框；")
	private java.lang.Integer iColumnType;
	/**是否作为列表标题列*/
	@Excel(name = "是否作为列表标题列", width = 15)
    @ApiModelProperty(value = "是否作为列表标题列")
	private java.lang.Integer iIsListtitle;
	/**是否作为列表查询条件*/
	@Excel(name = "是否作为列表查询条件", width = 15)
    @ApiModelProperty(value = "是否作为列表查询条件")
	private java.lang.Integer iIsListquery;
	/**是否必填校验*/
	@Excel(name = "是否必填校验", width = 15)
    @ApiModelProperty(value = "是否必填校验")
	private java.lang.Integer iCheckIsNull;
	/**java校验规则，正则表达式*/
	@Excel(name = "java校验规则，正则表达式", width = 15)
    @ApiModelProperty(value = "java校验规则，正则表达式")
	private java.lang.String sCheckExpjava;
	/**数据校验sql*/
	@Excel(name = "数据校验sql", width = 15)
    @ApiModelProperty(value = "数据校验sql")
	private java.lang.String sCheckExpsql;/**数据校验sql*/
	@Excel(name = "自定义sql查询", width = 32)
    @ApiModelProperty(value = "自定义sql查询")
	@TableField(updateStrategy = FieldStrategy.IGNORED)
	private java.lang.String sDictSqlKey;
	/**js校验规则，正则表达式（从数据字典查）*/
	@Excel(name = "js校验规则，正则表达式（从数据字典查）", width = 15)
    @ApiModelProperty(value = "js校验规则，正则表达式（从数据字典查）")
	private Integer iCheckExpjs;
	/**校验提示信息*/
	@Excel(name = "校验提示信息", width = 15)
    @ApiModelProperty(value = "校验提示信息")
	private java.lang.String sCheckShowmsg;
	/**对应公文传输字段*/
	@Excel(name = "对应公文传输字段", width = 15)
    @ApiModelProperty(value = "对应公文传输字段")
	private java.lang.String sSendKey;
	/**档案系统对应字段*/
	@Excel(name = "档案系统对应字段", width = 15)
    @ApiModelProperty(value = "档案系统对应字段")
	private java.lang.String sArchivesKey;
	/**自定义含义对应word书签字段*/
	@Excel(name = "自定义含义对应word书签字段", width = 15)
	@ApiModelProperty(value = "自定义含义对应word书签字段")
	private java.lang.String sMarkKey;
	/**是否模板业务原型含义（1是、0否）*/
	@Excel(name = "是否模板业务原型含义（1是、0否）", width = 15)
    @ApiModelProperty(value = "是否模板业务原型含义（1是、0否）")
	private java.lang.Integer iIsDefault;

	/**业务数据列备注*/
	@Excel(name = "业务数据列备注", width = 15)
    @ApiModelProperty(value = "业务数据列备注")
	private java.lang.String sColumnRemarks;




//	@Override
//	protected Serializable pkVal() {
//		return this.iId;
//	}
}
