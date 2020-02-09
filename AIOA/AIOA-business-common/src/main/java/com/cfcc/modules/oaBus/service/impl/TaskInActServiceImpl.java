package com.cfcc.modules.oaBus.service.impl;

import com.cfcc.common.constant.workflow.TaskConstant;
import com.cfcc.common.util.workflow.VarsWithBus;
import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.mapper.OaBusDynamicTableMapper;
import com.cfcc.modules.oaBus.service.IBusFunctionService;
import com.cfcc.modules.oaBus.service.TaskInActService;
import com.cfcc.modules.system.entity.LoginInfo;
import com.cfcc.modules.system.service.ISysUserService;
import com.cfcc.modules.utils.JumpTaskCmd;
import com.cfcc.modules.workflow.service.OaBusDataPermitService;
import com.cfcc.modules.workflow.service.TaskCommonService;
import com.cfcc.modules.workflow.vo.TaskInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
@Slf4j
public class TaskInActServiceImpl implements TaskInActService {

    @Autowired
    private TaskCommonService taskCommonService;
    @Resource
    private OaBusDynamicTableMapper oaBusDynamicTableMapper;
    @Autowired
    private OaBusDataPermitService oaBusDataPermitService;

    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private IBusFunctionService busFunctionService;


    @Override
    public void doTask(TaskInfoVO taskInfoVO, HttpServletRequest request) {

        //1 流程办理
        String nextTaskMsg = taskCommonService.doTask(taskInfoVO);
        //2 业务相关
        if (nextTaskMsg.endsWith("  ")) {
            Map<String, Object> busData = taskInfoVO.getBusData();
            LoginInfo loginInfo = sysUserService.getLoginInfo(request);
            busData.put("s_signer", loginInfo.getUsername());

            busData.put("d_date1", new Date());//new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//
        }
        busAbout(taskInfoVO, nextTaskMsg);
    }

    private void busAbout(TaskInfoVO taskInfoVO, String nextTaskMsg) {
        //2 更新流程对应的业务数据......
        //排除掉多余的字段
        //移除多余字段
        Map<String, Object> busData = taskInfoVO.getBusData();
        //更新当前节点信息
        busData.put("s_cur_task_name", nextTaskMsg);
        if ("end-已结束".equals(nextTaskMsg)) {
            busData.put("i_is_state", 1);
        }
        for (String remove : TaskConstant.REMOVEFILEDS) {
            busData.remove(remove);
        }
        busData.put("i_is_display", '0');
        //********************* 写入参与人 *********************
        String table = busData.get("table") + "_permit";
        oaBusDynamicTableMapper.updateData(busData);

        //4 存储用户信息到 业务数据权限表 - 构造用户信息
        SaveDataPermit(taskInfoVO, table);
    }


    @Override
    public void doTaskMore(List<TaskInfoVO> taskInfoVOs) {


        String nextTaskMsg = taskCommonService.doTasksMore(taskInfoVOs);
        //TODO 业务信息

    }


    /**
     * 并行/包容 追加用户
     * @param taskInfoVOS
     */
    @Override
    public void doAddUsers(ArrayList<TaskInfoVO> taskInfoVOS) {


        //commandExecutor.execute(new JumpTaskCmd(jumpMsg.getTaskId(), jumpMsg.getExecutionId(),
        //        jumpMsg.getProcessInstanceId(), dest, jumpMsg.getVars(), curr, jumpMsg.getDeleteReason()));


    }

    private void SaveDataPermit(TaskInfoVO taskInfoVO, String table) {
        ArrayList<String> uids = new ArrayList<>();
        Boolean isDept = taskInfoVO.getIsDept();
        if (null != isDept && isDept) {
            Map<String, String[]> deptMsg = taskInfoVO.getTaskWithDepts().getDeptMsg();

            for (Map.Entry<String, String[]> entry : deptMsg.entrySet()) {
                String[] ids = entry.getValue();
                Arrays.stream(ids).forEach(id -> {
                    uids.add(id);
                });
            }

        } else {
            List<String> assignee = (List<String>) taskInfoVO.getAssignee();
            assignee.stream().forEach(id -> {
                uids.add(id);
            });
        }

        //5 构造业务数据权限表数据
        Integer functionId = taskInfoVO.getFunctionId();
        Integer busDataId = taskInfoVO.getBusDataId();
        oaBusDataPermitService.save(table, uids, functionId, busDataId);
    }

}
