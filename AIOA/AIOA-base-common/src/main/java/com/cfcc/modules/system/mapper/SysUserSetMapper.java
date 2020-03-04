package  com.cfcc.modules.system.mapper;

import java.util.List;

import com.cfcc.modules.system.entity.SysUserSet;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 权限设置
 * @Author: jeecg-boot
 * @Date:   2019-10-17
 * @Version: V1.0
 */
public interface SysUserSetMapper extends BaseMapper<SysUserSet> {
    SysUserSet findById(Integer iId);

    int updateByIid(SysUserSet sysUserSet);

    int deleteByIidd(String id);

    List<SysUserSet> findPage(@Param("pageNo") Integer pageNo,@Param("pageSize") Integer pageSize, SysUserSet sysUserSet);

    int count(SysUserSet sysUserSet);

    List<SysUserSet> findList();

    int insertinto(SysUserSet sysUserSet);

    SysUserSet findByUserId(String userId);

    List<SysUserSet> findByIId(Integer iId);

    SysUserSet HomeAndDay(SysUserSet sysUserSet);

    List<String> queryUserSetByIdsDao(@Param("nameStr")String nameStr);
}
