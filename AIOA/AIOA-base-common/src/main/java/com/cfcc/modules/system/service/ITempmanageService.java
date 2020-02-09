package com.cfcc.modules.system.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.system.entity.Tempmanage;

/**
 * @Description: 测试类
 * @Author: jeecg-boot
 * @Date:   2019-09-25
 * @Version: V1.0
 */
public interface ITempmanageService extends IService<Tempmanage> {

    boolean updateBYIid(Tempmanage tempmanage);

}
