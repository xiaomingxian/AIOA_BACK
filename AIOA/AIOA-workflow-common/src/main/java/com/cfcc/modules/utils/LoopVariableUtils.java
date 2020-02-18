package com.cfcc.modules.utils;

import org.activiti.engine.impl.persistence.entity.ExecutionEntity;

public class LoopVariableUtils {

    public static void setLoopVariable(ExecutionEntity executionEntity, String varName, Object value) {

        ExecutionEntity parent = executionEntity.getParent();
        parent.setVariableLocal(varName, value);

    }

    public static void setLoopVariableDept(ExecutionEntity executionEntity, String varName, Object value) {

        executionEntity.setVariableLocal(varName, value);

    }

    public static Integer getLoopVariable(ExecutionEntity executionEntity, String varName) {
        Object value = executionEntity.getVariableLocal(varName);
        ExecutionEntity parent = executionEntity.getParent();

        while (value == null && parent != null) {
            value = parent.getVariable(varName);
            parent = parent.getParent();
        }
        return (Integer) (value != null ? value : 0);
    }


    public static Integer getLoopVariableDept(ExecutionEntity executionEntity, String varName) {
        Object value = executionEntity.getVariableLocal(varName);

        while (value == null && executionEntity != null) {
            value = executionEntity.getVariable(varName);
            executionEntity = executionEntity.getParent();
        }
        return (Integer) (value != null ? value : 0);
    }


}
