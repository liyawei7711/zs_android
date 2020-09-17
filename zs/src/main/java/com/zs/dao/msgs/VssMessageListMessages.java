package com.zs.dao.msgs;


/**
 * author: admin
 * date: 2018/01/09
 * version: 0
 * mail: secret
 * desc: AppMessages
 */
public class VssMessageListMessages {
    private VssMessageListMessages() {

    }

    static class Holder {
        static final VssMessageListMessages SINGLETON = new VssMessageListMessages();
    }

    public static VssMessageListMessages get() {
        return Holder.SINGLETON;
    }


}
