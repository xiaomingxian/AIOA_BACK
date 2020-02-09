package  com.cfcc.modules.system.mapper;

import java.util.List;

import com.cfcc.modules.system.entity.Tempmanage;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 测试类
 * @Author: jeecg-boot
 * @Date:   2019-09-25
 * @Version: V1.0
 */
public interface TempmanageMapper extends BaseMapper<Tempmanage> {
    boolean updateByIid(Tempmanage tempmanage);
}
