package com.cfcc.modules.quartz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.quartz.entity.QuartzJob;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 定时任务在线管理
 * @Author: jeecg-boot
 * @Date:  2019-01-02
 * @Version: V1.0
 */
public interface QuartzJobMapper extends BaseMapper<QuartzJob> {

	public List<QuartzJob> findByJobClassName(@Param("jobClassName") String jobClassName);


    List<QuartzJob> getQuartzJob(@Param("jobClassName") String jobClassName,@Param("status") Integer status);
}
