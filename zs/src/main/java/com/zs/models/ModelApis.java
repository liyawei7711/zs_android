package com.zs.models;

import com.zs.models.auth.AuthApi;
import com.zs.models.contacts.ContactsApi;
import com.zs.models.download.DownloadApi;

/**
 * author: admin
 * date: 2018/01/03
 * version: 0
 * mail: secret
 * desc: ModelApis
 */

public class ModelApis {

    private ModelApis() {

    }

    public static AuthApi Auth() {
        return AuthApi.get();
    }

}
