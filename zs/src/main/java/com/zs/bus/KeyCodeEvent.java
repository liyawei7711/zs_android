package com.zs.bus;

import java.io.Serializable;

public class KeyCodeEvent implements Serializable {
    public String action;

    public KeyCodeEvent(String action) {
        this.action = action;
    }
}
