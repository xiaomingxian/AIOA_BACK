package com.cfcc.modules.oaBus.mapper;

import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.entity.oaCalendar;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.system.entity.SysUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.security.core.parameters.P;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: 日程管理表
 * @Author: jeecg-boot
 * @Date:   2019-11-21
 * @Version: V1.0
 */
public interface oaCalendarMapper extends BaseMapper<oaCalendar> {

    oaCalendar findById(Integer iId);

    void deleteByIid(String id);

    void updateByIid(oaCalendar oaCalendar);

    int saveCalendar(oaCalendar oaCalendar);

    int count(oaCalendar oaCalendar);

    int countIsNoLeader(oaCalendar oaCalendar);

    int countIsLeader(oaCalendar oaCalendar);

    List<oaCalendar> findPage(@Param(value = "pageNo") int pageNo, @Param(value = "pageSize")Integer pageSize, @Param(value = "oaCalendar") oaCalendar oaCalendar);

    List<oaCalendar> findByLeader(@Param(value = "pageNo") int pageNo, @Param(value = "pageSize")Integer pageSize,  @Param(value = "oaCalendar") oaCalendar oaCalendar);

    List<oaCalendar> queryPageList( @Param(value = "pageNo") int pageNo, @Param(value = "pageSize")Integer pageSize, @Param(value = "oaCalendar") oaCalendar oaCalendar);

    oaCalendar getByIid(String id);

    String findLeader(String drafterId);

    List<SysUser> getByShowOrder(int showOrder);

    int countIsWait(oaCalendar oaCalendar);

    List<oaCalendar> findWait(@Param(value = "pageNo") int pageNo, @Param(value = "pageSize")Integer pageSize, @Param(value = "oaCalendar")  oaCalendar oaCalendar);

    List<String> getByUserId(@Param(value = "id")String id);

    String getDepartId(@Param(value = "id")String id);

    List<String> getDepartIdList(String userNameId);

    List<BusFunction> busFunctionList();

    Map<String, Object> findMostUser(String id);

    String selectPath(int id);

    List<Map<String, Object>>  LinkList(String sCreateBy);

    List<Map<String, Object>> findMostUser1(String sCreateBy);

    String selectName(int parseInt);

    oaCalendar findByTaskUserId(String taskUserId);

    @Update(" UPDATE oa_calendar set i_is_state=2 where task_user_id=#{v} ")
    void updateByTaskUserId(String s);

    List<Map<String, Object>> findList(@Param(value = "oaCalendar") oaCalendar oaCalendar,@Param(value = "pageNo") int pageNo, @Param(value = "pageSize")Integer pageSize);


    oaCalendar findBybusDataId(Integer busDataId,String userName);

    int appCount(oaCalendar oaCalendar);

    @Select("SELECT s_user_names,task_user_id,d_create_time FROM oa_calendar WHERE i_fun_data_id=#{iTableId} and s_create_by=#{username}")
    List<oaCalendar> selecturgeInform(Integer iTableId, String username);
}
