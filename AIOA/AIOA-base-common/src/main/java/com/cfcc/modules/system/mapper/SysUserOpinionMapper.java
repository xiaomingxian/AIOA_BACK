package  com.cfcc.modules.system.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.system.entity.SysUserOpinion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 快捷意见
 * @Author: jeecg-boot
 * @Date:   2019-10-12
 * @Version: V1.0
 */
public interface SysUserOpinionMapper extends BaseMapper<SysUserOpinion> {
    SysUserOpinion findById(Integer iId);

    int updateByIid(SysUserOpinion sysUserOpinion);

    int deleteByIidd(String id);

    List<SysUserOpinion> findPage(@Param("pageNo") Integer pageNo,@Param("pageSize") Integer pageSize, SysUserOpinion sysUserOpinion);
    int count(SysUserOpinion sysUserOpinion);

    /**
     * 查询用户个人意见
     * @param sysUserOpinion
     * @return
     */
    List<SysUserOpinion> queryUserOpinion(SysUserOpinion sysUserOpinion);

}
