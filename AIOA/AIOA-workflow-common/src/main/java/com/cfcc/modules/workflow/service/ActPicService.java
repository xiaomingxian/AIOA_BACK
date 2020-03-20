package com.cfcc.modules.workflow.service;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ActPicService {


    void queryProPlan(String processInstanceId, HttpServletResponse response)  throws IOException;
    void savePic(String processInstanceId, HttpServletResponse response,String dest)  throws IOException;

    void queryProPlanFromLocal(String processInstanceId, String endTime, HttpServletResponse response);
}
