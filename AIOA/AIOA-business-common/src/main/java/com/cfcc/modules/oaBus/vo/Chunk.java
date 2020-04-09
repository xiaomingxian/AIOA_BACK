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
public class Chunk implements Serializable {

    @TableField(exist = false)
    private Long id;
    /**
     * 当前文件块，从1开始
     */
    @TableField(exist = false)
    private Integer chunkNumber;
    /**
     * 分块大小
     */
    @TableField(exist = false)
    private Long chunkSize;
    /**
     * 当前分块大小
     */
    @TableField(exist = false)
    private Long currentChunkSize;
    /**
     * 总大小
     */
    @TableField(exist = false)
    private Long totalSize;
    /**
     * 文件标识
     */
    @TableField(exist = false)
    private String identifier;
    /**
     * 文件名
     */
    @TableField(exist = false)
    private String filename;
    /**
     * 相对路径
     */
    @TableField(exist = false)
    private String relativePath;
    /**
     * 总块数
     */
    @TableField(exist = false)
    private Integer totalChunks;
    /**
     * 文件类型
     */
    @TableField(exist = false)
    private String type;

    @TableField(exist = false)
    private MultipartFile file;
}
