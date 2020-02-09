package com.cfcc.modules.oaBus.entity;

import com.cfcc.modules.oabutton.entity.OaButton;
import lombok.Data;

import java.io.Serializable;


@Data
public class ButtonPermit extends OaButton implements Serializable {
    private Boolean permitType;
    private Boolean isCreate;
    private Boolean isReader;
    private Boolean isLastsender;
    private Boolean isTransactors;
    private String sRoles;
}
