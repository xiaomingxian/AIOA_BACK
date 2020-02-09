package com.cfcc.modules.workflow.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TaskInfoVoList implements Serializable {

    private List<TaskInfoVO> list;

}
