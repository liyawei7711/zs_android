package com.zs.ui.home.holder;

import java.io.Serializable;

public class MainZSMenuBean implements Serializable {
    public int img_id;
    public int code;
    public String name;

    public MainZSMenuBean(int img_id, String name, int code) {
        this.img_id = img_id;
        this.code = code;
        this.name = name;
    }
}
