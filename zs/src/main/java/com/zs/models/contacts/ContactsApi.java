package com.zs.models.contacts;

import com.zs.dao.AppConstants;
import com.zs.dao.auth.AppAuth;
import com.zs.models.ModelCallback;
import com.zs.models.contacts.bean.PersonBean;
import com.zs.models.contacts.bean.SosPersonBean;
import ttyy.com.jinnetwork.Https;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

/**
 * author: admin
 * date: 2018/01/02
 * version: 0
 * mail: secret
 * desc: ContactsModel
 */

public class ContactsApi {

    String URL;

    private ContactsApi() {
    }

    public static ContactsApi get() {
        return new ContactsApi();
    }

    public void getPerson(int nPage, int size, int nOrderByID, int nAscOrDesc,
                          final ModelCallback<PersonBean> callback) {
        URL = AppConstants.getAddressBaseURL() + "vss/httpjson/get_user_list";
        Https.post(URL)
                .addHeader("X-Token", AppAuth.get().getToken())
                .addParam("nPage", nPage)
                .addParam("nSize", size)
                .addParam("nOrderByID", nOrderByID)
                .addParam("nAscOrDesc", nAscOrDesc)
                .addParam("strDomainCode", AppAuth.get().getDomainCode())
                .setHttpCallback(new ModelCallback<PersonBean>() {
                    @Override
                    public void onSuccess(PersonBean personBean) {
                        if (personBean != null && callback != null) {
                            callback.onSuccess(personBean);
                        }
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                    }
                })
                .build()
                .requestNowAsync();
    }
    public void loadSos(final ModelCallback<SosPersonBean> callback) {
        URL = AppConstants.getAddressBaseURL() + "vss/httpjson/get_seek_help_info";
        Https.post(URL)
                .addHeader("X-Token", AppAuth.get().getToken())
                .setHttpCallback(new ModelCallback<SosPersonBean>() {
                    @Override
                    public void onSuccess(SosPersonBean personBean) {
                        if (personBean != null && callback != null) {
                            callback.onSuccess(personBean);
                        }
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                    }
                })
                .build()
                .requestNowAsync();
    }


}
