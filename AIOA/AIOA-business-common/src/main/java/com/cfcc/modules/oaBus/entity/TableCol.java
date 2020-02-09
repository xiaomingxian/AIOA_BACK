package com.cfcc.modules.oaBus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 表中的列名
 * @Author: jeecg-boot
 * @Date:   2019-10-26
 * @Version: V1.0
 */
@Data
//@TableName("table_col")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="table_col对象", description="表中的列名")
public class TableCol {
    
	/**表id*/
	@TableId(type = IdType.UUID)
    @ApiModelProperty(value = "表id")
	private java.lang.Integer id;
	/**列的名字*/
	@Excel(name = "列的名字", width = 15)
    @ApiModelProperty(value = "数据表的列名")
	private java.lang.String sTableColumn;
	/**列的注释*/
	@Excel(name = "列的注释", width = 15)
    @ApiModelProperty(value = "数据表的列的含义")
	private java.lang.String sColumnName;
}
