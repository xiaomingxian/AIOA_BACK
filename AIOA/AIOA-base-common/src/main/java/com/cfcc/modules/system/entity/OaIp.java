package com.cfcc.modules.system.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 部门表 封装树结构的部门的名称的实体类
 * <p>
 * 
 * @Author Steve
 * @Since 2019-01-22 
 *
 */
@Data
public class OaIp implements Serializable {

    private static final long serialVersionUID = 1L;

    // 主键ID
    private Long id;

    //用户ID
    private Integer userId;

    // 用户客户端IP
    private String ip;

    //登录时间
    private java.util.Date tlogin;


}
