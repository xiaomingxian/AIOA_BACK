package com.cfcc.modules.workflow.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProcessDefinitionJsonAble implements Serializable {
    java.lang.String id;

    java.lang.String category;

    java.lang.String name;

    java.lang.String key;

    java.lang.String description;

    int version;

    java.lang.String resourceName;

    java.lang.String deploymentId;

    java.lang.String diagramResourceName;

    boolean hasStartFormKey;

    boolean hasGraphicalNotation;

    boolean isSuspended;

    public String getSus() {
        return isSuspended ? "挂起" : "激活";
    }

    java.lang.String tenantId;
}
