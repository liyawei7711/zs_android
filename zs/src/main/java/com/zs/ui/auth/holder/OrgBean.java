package com.zs.ui.auth.holder;

import java.io.Serializable;

public class OrgBean implements Serializable {
    public boolean isSelected;
    public String name;
    public String code;

    public OrgBean(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
