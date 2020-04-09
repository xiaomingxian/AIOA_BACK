package com.cfcc.modules.oaBus.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
@TableName("oa_bus_function")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "oa_bus_function对象", description = "业务配置表")
public class FileInfo implements Serializable {

    @TableField(exist = false)
    private Long id;

    @TableField(exist = false)
    private String filename;

    @TableField(exist = false)
    private String identifier;

    @TableField(exist = false)
    private Long totalSize;

    @TableField(exist = false)
    private String type;

    @TableField(exist = false)
    private String location;

}
