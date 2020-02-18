package com.cfcc.modules.oaBus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * @Description: 业务模板
 * @Author: jeecg-boot
 * @Date: 2019-10-21
 * @Version: V1.0
 */
@Data
@TableName("oa_bus_model_permit")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "oa_bus_model_permit对象", description = "业务模板")
public class BusModelPermit implements Serializable {

    /**
     * 主键id
     */
    @Excel(name = "主键id", width = 15)
    @ApiModelProperty(value = "主键id")
    @TableId(value = "i_id",type = IdType.AUTO)
    private java.lang.Integer iId;
    /**
     * 业务模块id
     */
    @Excel(name = "业务模块id", width = 15)
    @ApiModelProperty(value = "业务模块id")
    private java.lang.Integer iBusModelId;
    /**
     * 范围类型:0.所有人;1.角色;2.部门;3.人员;
     */
    @Excel(name = "范围类型:0.所有人;1.角色;2.部门;3.人员;", width = 15)
    @ApiModelProperty(value = "范围类型:0.所有人;1.角色;2.部门;3.人员;")
    private java.lang.String sPermitType;
    /**
     * 类型对应id
     */
    @Excel(name = "类型对应id", width = 15)
    @ApiModelProperty(value = "类型对应id")
    private String iTypeId;
    /**
     * 是否可见:0.不可见;1.可见;
     */
    @Excel(name = "是否可见:0.不可见;1.可见;", width = 15)
    @ApiModelProperty(value = "是否可见:0.不可见;1.可见;")
    private java.lang.String sDisplay;

    @TableField(exist = false)
    private java.lang.String busModelName;

    @TableField(exist = false)
    private java.lang.String itypeName;
    @TableField(exist = false)
    private java.lang.String parentName;

}
