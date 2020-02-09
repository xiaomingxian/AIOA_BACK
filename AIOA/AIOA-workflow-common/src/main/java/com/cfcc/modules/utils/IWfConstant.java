package com.cfcc.modules.utils;

public interface IWfConstant {
    /**
     * 流程实例启动者
     */
    String WF_TASK_STARTER = "drafter";
    /**
     * 已办
     */
    String TASK_DONE = "task_done";
    /**
     * 待办
     */
    String TASK_TODO = "task_todo";

    /**
     * 流程监控数据
     */
    String TASK_MONITOR = "task_monitor";
    /**
     * 我的委托
     */
    String MY_AGENT = "my_agent";

    /**
     * 跳转/重置
     */
    String JUMP = "jump";
    /**
     * 任务移交
     */
    String SHHIFT = "shift";
}
