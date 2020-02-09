package com.cfcc.modules.workflow.service;

import java.util.List;

public interface OaBusDataPermitService {
     void save(String table, List<String> uids,
                     Integer functionId, Integer busDataId);

    boolean deleteBusDataPermitByBusDataIdAndFuncationId(String table, Integer busdataId, Integer funcationId);
}
