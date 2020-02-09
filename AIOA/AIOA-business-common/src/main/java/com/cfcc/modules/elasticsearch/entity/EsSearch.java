package com.cfcc.modules.elasticsearch.entity;/*
 *
 *
 */

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@TableName("oa_es_search")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="oa_es_search对象", description="全文检索统计表")
public class EsSearch implements Serializable {

    private static final long serialVersionUID = 1L ;

    /**主键id*/
    private Integer id;

    @TableField("key_word")
    private String keyWord;

    private Integer frequency;

}
