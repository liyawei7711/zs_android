package com.zs.models.auth.bean;

import android.text.TextUtils;

import com.zs.BuildConfig;

import java.io.Serializable;

/**
 * author: admin
 * date: 2018/02/01
 * version: 0
 * mail: secret
 * desc: VersionData
 */

public class VersionData implements Serializable {


    /**
     * msg : success
     * code : 0
     * data : {"id":"AV320600344223092328894464","appName":"1.0.4","newVersion":"5","minVersion":"21","appDesc":null,"accesoryId":"FILE320600344251484453474304","createTime":"2020/09/05 20:59","appAccessoryBaseBean":{"accessoryId":"FILE320600344251484453474304","accessoryName":"app-release20200905225230.apk","accessoryType":".apk","accessoryUrl":"http://58.221.238.134:8086/aj/writ/accessory/downLoadByImg?accessoryId=FILE320600344251484453474304","accessoryDownUrl":"http://58.221.238.134:8086/aj/writ/accessory/download?accessoryId=FILE320600344251484453474304","isLocal":1,"isImageFlag":null}}
     */

    private String msg;
    private String code;
    private DataBean data;

    public boolean isNeedToUpdate() {
        if (TextUtils.isEmpty(data.getNewVersion())) {
            data.setNewVersion("0");
        }
        return BuildConfig.VERSION_CODE < Integer.parseInt(data.getNewVersion());
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : AV320600344223092328894464
         * appName : 1.0.4
         * newVersion : 5
         * minVersion : 21
         * appDesc : null
         * accesoryId : FILE320600344251484453474304
         * createTime : 2020/09/05 20:59
         * appAccessoryBaseBean : {"accessoryId":"FILE320600344251484453474304","accessoryName":"app-release20200905225230.apk","accessoryType":".apk","accessoryUrl":"http://58.221.238.134:8086/aj/writ/accessory/downLoadByImg?accessoryId=FILE320600344251484453474304","accessoryDownUrl":"http://58.221.238.134:8086/aj/writ/accessory/download?accessoryId=FILE320600344251484453474304","isLocal":1,"isImageFlag":null}
         */

        private String id;
        private String appName;
        private String newVersion;
        private String minVersion;
        private String appDesc;
        private String accesoryId;
        private String createTime;
        private AppAccessoryBaseBeanBean appAccessoryBaseBean;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getNewVersion() {
            return newVersion;
        }

        public void setNewVersion(String newVersion) {
            this.newVersion = newVersion;
        }

        public String getMinVersion() {
            return minVersion;
        }

        public void setMinVersion(String minVersion) {
            this.minVersion = minVersion;
        }

        public String getAppDesc() {
            return appDesc;
        }

        public void setAppDesc(String appDesc) {
            this.appDesc = appDesc;
        }

        public String getAccesoryId() {
            return accesoryId;
        }

        public void setAccesoryId(String accesoryId) {
            this.accesoryId = accesoryId;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public AppAccessoryBaseBeanBean getAppAccessoryBaseBean() {
            return appAccessoryBaseBean;
        }

        public void setAppAccessoryBaseBean(AppAccessoryBaseBeanBean appAccessoryBaseBean) {
            this.appAccessoryBaseBean = appAccessoryBaseBean;
        }

        public static class AppAccessoryBaseBeanBean {
            /**
             * accessoryId : FILE320600344251484453474304
             * accessoryName : app-release20200905225230.apk
             * accessoryType : .apk
             * accessoryUrl : http://58.221.238.134:8086/aj/writ/accessory/downLoadByImg?accessoryId=FILE320600344251484453474304
             * accessoryDownUrl : http://58.221.238.134:8086/aj/writ/accessory/download?accessoryId=FILE320600344251484453474304
             * isLocal : 1
             * isImageFlag : null
             */

            private String accessoryId;
            private String accessoryName;
            private String accessoryType;
            private String accessoryUrl;
            private String accessoryDownUrl;
            private int isLocal;
            private Object isImageFlag;

            public String getAccessoryId() {
                return accessoryId;
            }

            public void setAccessoryId(String accessoryId) {
                this.accessoryId = accessoryId;
            }

            public String getAccessoryName() {
                return accessoryName;
            }

            public void setAccessoryName(String accessoryName) {
                this.accessoryName = accessoryName;
            }

            public String getAccessoryType() {
                return accessoryType;
            }

            public void setAccessoryType(String accessoryType) {
                this.accessoryType = accessoryType;
            }

            public String getAccessoryUrl() {
                return accessoryUrl;
            }

            public void setAccessoryUrl(String accessoryUrl) {
                this.accessoryUrl = accessoryUrl;
            }

            public String getAccessoryDownUrl() {
                return accessoryDownUrl;
            }

            public void setAccessoryDownUrl(String accessoryDownUrl) {
                this.accessoryDownUrl = accessoryDownUrl;
            }

            public int getIsLocal() {
                return isLocal;
            }

            public void setIsLocal(int isLocal) {
                this.isLocal = isLocal;
            }

            public Object getIsImageFlag() {
                return isImageFlag;
            }

            public void setIsImageFlag(Object isImageFlag) {
                this.isImageFlag = isImageFlag;
            }
        }
    }
}
