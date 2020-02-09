package com.cfcc.modules.workflow.service.impl;

import com.cfcc.modules.system.mapper.SysUserMapper;
import com.cfcc.modules.workflow.mapper.OaBusdataPermitMapper;
import com.cfcc.modules.workflow.pojo.OaBusdataPermit;
import com.cfcc.modules.workflow.service.OaBusDataPermitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class OaBusDataPermitServiceImpl implements OaBusDataPermitService {
    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private OaBusdataPermitMapper oaBusdataPermitMapper;


    @Override
    public void save(String table, List<String> uids,
                     Integer functionId, Integer busDataId) {

        if (uids.size() == 0) return;
        List<Map<String, Object>> userMsg = sysUserMapper.selectUserAndDeptByUserId(uids);
        ArrayList<OaBusdataPermit> oaBusdataPermits = new ArrayList<>();
        userMsg.stream().forEach(map -> {
            OaBusdataPermit oaBusdataPermit = new OaBusdataPermit();
            oaBusdataPermit.setIBusdataId(busDataId);
            oaBusdataPermit.setIBusFunctionId(functionId);
            oaBusdataPermit.setSUserId(map.get("uid") + "");
            oaBusdataPermit.setSUserdeptId(map.get("did") + "");
            oaBusdataPermit.setSUserunitId(map.get("dpid") + "");
            oaBusdataPermit.setIIsRead(false);
            oaBusdataPermit.setIIsCancel(false);
            oaBusdataPermits.add(oaBusdataPermit);
        });
        //6 批量写入数据
        if (oaBusdataPermits.size() > 0) {
            oaBusdataPermitMapper.insertBatch(table, oaBusdataPermits);
        }
    }

    @Override
    public boolean deleteBusDataPermitByBusDataIdAndFuncationId(String table, Integer busdataId, Integer funcationId) {
         Boolean total=oaBusdataPermitMapper.deleteBusDataPermitByBusDataIdAndFuncationId(table,busdataId,funcationId);
        return total;
    }

}
