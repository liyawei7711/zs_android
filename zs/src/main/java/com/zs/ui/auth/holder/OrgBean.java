package com.zs.ui.auth.holder;

import java.io.Serializable;

public class OrgBean implements Serializable {
    public boolean isSelected;
    public String name;
    public String code;
    public String ip;
    public String port;

    public OrgBean(String name, String code, String ip, String port) {
        this.name = name;
        this.code = code;
        this.ip = ip;
        this.port = port;
    }
}
