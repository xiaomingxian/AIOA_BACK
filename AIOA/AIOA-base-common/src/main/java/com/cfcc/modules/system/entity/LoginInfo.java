package com.cfcc.modules.system.entity;


import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class LoginInfo extends SysUser implements Serializable {

    public LoginInfo() {
    }

    List<SysRole> roles;

    SysDepart depart;

    List<SysDepart> departs;

    List<String> roleNames;

    private List<String> roleIds;

    private SysUser agentUser;


    public List<String> getRoleIds() {
        ArrayList<String> rs = new ArrayList<>();
        ArrayList<String> rNames = new ArrayList<>();
        if (roles != null && roles.size() > 0) {
            for (SysRole role : roles) {
                String id = role.getId();
                rs.add(id);
                rNames.add(role.getRoleName());
            }
        }
        this.roleNames = rNames;
        return rs;
    }

    public List<String> getRoleNames() {
        ArrayList<String> rNames = new ArrayList<>();
        if (roles != null && roles.size() > 0) {
            for (SysRole role : roles) {
                rNames.add(role.getRoleName());
            }
        }
        return rNames;
    }


}
