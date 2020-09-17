package com.zs.models.auth.bean;

import java.io.Serializable;
import java.util.List;

/**
 * author: admin
 * date: 2018/01/02
 * version: 0
 * mail: secret
 * desc: AuthUser
 */

public class AuthUser implements Serializable {


    /**
     * msg : success
     * userEntity : {"id":"USER320200346623235229487104","
     * createUser":"USER320200265918680297115648",
     * "createTime":"2020-09-12 11:57:00","updateUser":"USER320200346623235229487104","updateTime":"2020-09-12 12:00:36","orderNum":null,"delFlag":"0","username":"zfjlycs","password":"99349fcf224f4e788f76ad7d4a42c92bdd262aa9d9a220331168cd2c05cfe6fc","salt":"tbsxqgVNcHyXgFHlTw3Q","cardId":"zfjlycs001","email":"","mobile":"18952280597","status":1,"deptId":"996633694769266690","nickName":"zfjlycs","position":"321","isAdmin":0,"loginTime":"2020-09-12 11:58:05","errorNum":0,"isLock":"1","token":"1","expireTime":null,"identityCard":"312321","deptCode":"320200000000","needPswUpt":"1","sexCode":"1","fzjg":"321","ssztqc":"局领导应急管理局","professionalName":"321","professionalRankCode":"1","nationCode":"1","highestEducationCode":"10","profession":"321","personnelNatureCode":"01","isJdry":"1","isFlzyzg":"1","isWt":"1","isSq":"1","isCompletion":"1","picId":null,"faceId":null,"fingerprintId":null,"zylb":"安全生产类-安全工程","lastUptPswTime":"2020/09/12 11:58","isSynData":0,"loginType":0,"mapId":null,"code":null,"roleIdList":["1032449138773577731","1032438007728295937","1019791551213432833","1062530582166138882","1001","1032438224745779201","ROLEASH001","ROLE320200343417254198644736","ADMINDATA04"],"roleNameList":"新吴区安监局执法角色,无锡市安监局执法角色,编辑执法计划角色,无锡市管理员,执法人员,锡山安监局执法角色,案审会人员,管理员1,省级权限","deptName":"局领导","encryptPassword":null,"deptNameWithFullLevel":null,"fullDeptName":null}
     * server : {"mediaWebUrl":"","mediaUrl":"","mediaPort":"","mediaCharset":""}
     * code : 0
     * data : 91ac8b1ec1e919a2064ead927e905298
     */

    public String msg;
    private UserEntityBean userEntity;
    private ServerBean server;
    public String code;
    public String data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public UserEntityBean getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntityBean userEntity) {
        this.userEntity = userEntity;
    }

    public ServerBean getServer() {
        return server;
    }

    public void setServer(ServerBean server) {
        this.server = server;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public static class UserEntityBean {
        /**
         * id : USER320200346623235229487104
         * createUser : USER320200265918680297115648
         * createTime : 2020-09-12 11:57:00
         * updateUser : USER320200346623235229487104
         * updateTime : 2020-09-12 12:00:36
         * orderNum : null
         * delFlag : 0
         * username : zfjlycs
         * password : 99349fcf224f4e788f76ad7d4a42c92bdd262aa9d9a220331168cd2c05cfe6fc
         * salt : tbsxqgVNcHyXgFHlTw3Q
         * cardId : zfjlycs001
         * email :
         * mobile : 18952280597
         * status : 1
         * deptId : 996633694769266690
         * nickName : zfjlycs
         * position : 321
         * isAdmin : 0
         * loginTime : 2020-09-12 11:58:05
         * errorNum : 0
         * isLock : 1
         * token : 1
         * expireTime : null
         * identityCard : 312321
         * deptCode : 320200000000
         * needPswUpt : 1
         * sexCode : 1
         * fzjg : 321
         * ssztqc : 局领导应急管理局
         * professionalName : 321
         * professionalRankCode : 1
         * nationCode : 1
         * highestEducationCode : 10
         * profession : 321
         * personnelNatureCode : 01
         * isJdry : 1
         * isFlzyzg : 1
         * isWt : 1
         * isSq : 1
         * isCompletion : 1
         * picId : null
         * faceId : null
         * fingerprintId : null
         * zylb : 安全生产类-安全工程
         * lastUptPswTime : 2020/09/12 11:58
         * isSynData : 0
         * loginType : 0
         * mapId : null
         * code : null
         * roleIdList : ["1032449138773577731","1032438007728295937","1019791551213432833","1062530582166138882","1001","1032438224745779201","ROLEASH001","ROLE320200343417254198644736","ADMINDATA04"]
         * roleNameList : 新吴区安监局执法角色,无锡市安监局执法角色,编辑执法计划角色,无锡市管理员,执法人员,锡山安监局执法角色,案审会人员,管理员1,省级权限
         * deptName : 局领导
         * encryptPassword : null
         * deptNameWithFullLevel : null
         * fullDeptName : null
         */

        private String id;
        private String createUser;
        private String createTime;
        private String updateUser;
        private String updateTime;
        public String hyToken;
        private Object orderNum;
        private String delFlag;
        private String username;
        private String password;
        private String salt;
        private String cardId;
        private String email;
        private String mobile;
        private int status;
        private String deptId;
        private String nickName;
        private String position;
        private int isAdmin;
        private String loginTime;
        private int errorNum;
        private String isLock;
        private String token;
        private Object expireTime;
        private String identityCard;
        private String deptCode;
        private String needPswUpt;
        private String sexCode;
        private String fzjg;
        private String ssztqc;
        private String professionalName;
        private String professionalRankCode;
        private String nationCode;
        private String highestEducationCode;
        private String profession;
        private String personnelNatureCode;
        private String isJdry;
        private String isFlzyzg;
        private String isWt;
        private String isSq;
        private String isCompletion;
        private Object picId;
        private Object faceId;
        private Object fingerprintId;
        private String zylb;
        private String lastUptPswTime;
        private int isSynData;
        private int loginType;
        private Object mapId;
        private Object code;
        private String roleNameList;
        private String deptName;
        private Object encryptPassword;
        private Object deptNameWithFullLevel;
        private Object fullDeptName;
        private List<String> roleIdList;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCreateUser() {
            return createUser;
        }

        public void setCreateUser(String createUser) {
            this.createUser = createUser;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getUpdateUser() {
            return updateUser;
        }

        public void setUpdateUser(String updateUser) {
            this.updateUser = updateUser;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public Object getOrderNum() {
            return orderNum;
        }

        public void setOrderNum(Object orderNum) {
            this.orderNum = orderNum;
        }

        public String getDelFlag() {
            return delFlag;
        }

        public void setDelFlag(String delFlag) {
            this.delFlag = delFlag;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getSalt() {
            return salt;
        }

        public void setSalt(String salt) {
            this.salt = salt;
        }

        public String getCardId() {
            return cardId;
        }

        public void setCardId(String cardId) {
            this.cardId = cardId;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getDeptId() {
            return deptId;
        }

        public void setDeptId(String deptId) {
            this.deptId = deptId;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public int getIsAdmin() {
            return isAdmin;
        }

        public void setIsAdmin(int isAdmin) {
            this.isAdmin = isAdmin;
        }

        public String getLoginTime() {
            return loginTime;
        }

        public void setLoginTime(String loginTime) {
            this.loginTime = loginTime;
        }

        public int getErrorNum() {
            return errorNum;
        }

        public void setErrorNum(int errorNum) {
            this.errorNum = errorNum;
        }

        public String getIsLock() {
            return isLock;
        }

        public void setIsLock(String isLock) {
            this.isLock = isLock;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public Object getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(Object expireTime) {
            this.expireTime = expireTime;
        }

        public String getIdentityCard() {
            return identityCard;
        }

        public void setIdentityCard(String identityCard) {
            this.identityCard = identityCard;
        }

        public String getDeptCode() {
            return deptCode;
        }

        public void setDeptCode(String deptCode) {
            this.deptCode = deptCode;
        }

        public String getNeedPswUpt() {
            return needPswUpt;
        }

        public void setNeedPswUpt(String needPswUpt) {
            this.needPswUpt = needPswUpt;
        }

        public String getSexCode() {
            return sexCode;
        }

        public void setSexCode(String sexCode) {
            this.sexCode = sexCode;
        }

        public String getFzjg() {
            return fzjg;
        }

        public void setFzjg(String fzjg) {
            this.fzjg = fzjg;
        }

        public String getSsztqc() {
            return ssztqc;
        }

        public void setSsztqc(String ssztqc) {
            this.ssztqc = ssztqc;
        }

        public String getProfessionalName() {
            return professionalName;
        }

        public void setProfessionalName(String professionalName) {
            this.professionalName = professionalName;
        }

        public String getProfessionalRankCode() {
            return professionalRankCode;
        }

        public void setProfessionalRankCode(String professionalRankCode) {
            this.professionalRankCode = professionalRankCode;
        }

        public String getNationCode() {
            return nationCode;
        }

        public void setNationCode(String nationCode) {
            this.nationCode = nationCode;
        }

        public String getHighestEducationCode() {
            return highestEducationCode;
        }

        public void setHighestEducationCode(String highestEducationCode) {
            this.highestEducationCode = highestEducationCode;
        }

        public String getProfession() {
            return profession;
        }

        public void setProfession(String profession) {
            this.profession = profession;
        }

        public String getPersonnelNatureCode() {
            return personnelNatureCode;
        }

        public void setPersonnelNatureCode(String personnelNatureCode) {
            this.personnelNatureCode = personnelNatureCode;
        }

        public String getIsJdry() {
            return isJdry;
        }

        public void setIsJdry(String isJdry) {
            this.isJdry = isJdry;
        }

        public String getIsFlzyzg() {
            return isFlzyzg;
        }

        public void setIsFlzyzg(String isFlzyzg) {
            this.isFlzyzg = isFlzyzg;
        }

        public String getIsWt() {
            return isWt;
        }

        public void setIsWt(String isWt) {
            this.isWt = isWt;
        }

        public String getIsSq() {
            return isSq;
        }

        public void setIsSq(String isSq) {
            this.isSq = isSq;
        }

        public String getIsCompletion() {
            return isCompletion;
        }

        public void setIsCompletion(String isCompletion) {
            this.isCompletion = isCompletion;
        }

        public Object getPicId() {
            return picId;
        }

        public void setPicId(Object picId) {
            this.picId = picId;
        }

        public Object getFaceId() {
            return faceId;
        }

        public void setFaceId(Object faceId) {
            this.faceId = faceId;
        }

        public Object getFingerprintId() {
            return fingerprintId;
        }

        public void setFingerprintId(Object fingerprintId) {
            this.fingerprintId = fingerprintId;
        }

        public String getZylb() {
            return zylb;
        }

        public void setZylb(String zylb) {
            this.zylb = zylb;
        }

        public String getLastUptPswTime() {
            return lastUptPswTime;
        }

        public void setLastUptPswTime(String lastUptPswTime) {
            this.lastUptPswTime = lastUptPswTime;
        }

        public int getIsSynData() {
            return isSynData;
        }

        public void setIsSynData(int isSynData) {
            this.isSynData = isSynData;
        }

        public int getLoginType() {
            return loginType;
        }

        public void setLoginType(int loginType) {
            this.loginType = loginType;
        }

        public Object getMapId() {
            return mapId;
        }

        public void setMapId(Object mapId) {
            this.mapId = mapId;
        }

        public Object getCode() {
            return code;
        }

        public void setCode(Object code) {
            this.code = code;
        }

        public String getRoleNameList() {
            return roleNameList;
        }

        public void setRoleNameList(String roleNameList) {
            this.roleNameList = roleNameList;
        }

        public String getDeptName() {
            return deptName;
        }

        public void setDeptName(String deptName) {
            this.deptName = deptName;
        }

        public Object getEncryptPassword() {
            return encryptPassword;
        }

        public void setEncryptPassword(Object encryptPassword) {
            this.encryptPassword = encryptPassword;
        }

        public Object getDeptNameWithFullLevel() {
            return deptNameWithFullLevel;
        }

        public void setDeptNameWithFullLevel(Object deptNameWithFullLevel) {
            this.deptNameWithFullLevel = deptNameWithFullLevel;
        }

        public Object getFullDeptName() {
            return fullDeptName;
        }

        public void setFullDeptName(Object fullDeptName) {
            this.fullDeptName = fullDeptName;
        }

        public List<String> getRoleIdList() {
            return roleIdList;
        }

        public void setRoleIdList(List<String> roleIdList) {
            this.roleIdList = roleIdList;
        }
    }

    public static class MediaConfigBean{
        public String charset;
        public String ip;
        public String port;
    }
    public static class WebH5ConfigBean{
        public String url;
    }

    public static class ServerBean {
        /**
         * mediaWebUrl :
         * mediaUrl :
         * mediaPort :
         * mediaCharset :
         */

        private String mediaWebUrl;

        private String mediaUrl;
        private String mediaPort;

        private String rtspUrl;
        private String rtspPort;

        private String mediaCharset;

        public String getRtspUrl() {
            return rtspUrl;
        }

        public void setRtspUrl(String rtspUrl) {
            this.rtspUrl = rtspUrl;
        }

        public String getRtspPort() {
            return rtspPort;
        }

        public void setRtspPort(String rtspPort) {
            this.rtspPort = rtspPort;
        }

        public String getMediaWebUrl() {
            return mediaWebUrl;
        }

        public void setMediaWebUrl(String mediaWebUrl) {
            this.mediaWebUrl = mediaWebUrl;
        }

        public String getMediaUrl() {
            return mediaUrl;
        }

        public void setMediaUrl(String mediaUrl) {
            this.mediaUrl = mediaUrl;
        }

        public String getMediaPort() {
            return mediaPort;
        }

        public void setMediaPort(String mediaPort) {
            this.mediaPort = mediaPort;
        }

        public String getMediaCharset() {
            return mediaCharset;
        }

        public void setMediaCharset(String mediaCharset) {
            this.mediaCharset = mediaCharset;
        }
    }
}
