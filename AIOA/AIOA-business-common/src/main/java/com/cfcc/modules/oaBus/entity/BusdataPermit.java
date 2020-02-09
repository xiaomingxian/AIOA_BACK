package com.cfcc.modules.oaBus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 业务数据权限表
 * @Author: jeecg-boot
 * @Date:   2019-12-10
 * @Version: V1.0
 */
@Data
@TableName("oa_busdata12_permit")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="oa_busdata12_permit对象", description="业务数据权限表")
public class BusdataPermit {
    
	/**主键id*/
	@Excel(name = "主键id", width = 15)
    @ApiModelProperty(value = "主键id")
	@TableId(value = "i_id", type = IdType.AUTO)
	private Integer iId;
	/**业务功能配置ID*/
	@Excel(name = "业务功能配置ID", width = 15)
    @ApiModelProperty(value = "业务功能配置ID")
	private Integer iBusFunctionId;
	/**业务数据id*/
	@Excel(name = "业务数据id", width = 15)
    @ApiModelProperty(value = "业务数据id")
	private Integer iBusdataId;
	/**用户id*/
	@Excel(name = "用户id", width = 15)
    @ApiModelProperty(value = "用户id")
	private String sUserId;
	/**用户部门id*/
	@Excel(name = "用户部门id", width = 15)
    @ApiModelProperty(value = "用户部门id")
	private String sUserdeptId;
	/**用户机构id*/
	@Excel(name = "用户机构id", width = 15)
    @ApiModelProperty(value = "用户机构id")
	private String sUserunitId;
	/**是否打开过*/
	@Excel(name = "是否打开过", width = 15)
    @ApiModelProperty(value = "是否打开过")
	private Integer iIsRead;
	/**是否失效（全行传阅后设置为失效状态，公文办结后删除）*/
	@Excel(name = "是否失效（全行传阅后设置为失效状态，公文办结后删除）", width = 15)
    @ApiModelProperty(value = "是否失效（全行传阅后设置为失效状态，公文办结后删除）")
	private Integer iIsCancel;
}
