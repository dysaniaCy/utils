package com.yiyouliao.autoprod.liaoyuan.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.yiyouliao.autoprod.common.exception.BizException;
import com.yiyouliao.autoprod.liaoyuan.entity.bo.AccountBO;
import com.yiyouliao.autoprod.liaoyuan.entity.domain.AccountDO;
import com.yiyouliao.autoprod.liaoyuan.entity.domain.SnapshotMicroAppUserDO;
import com.yiyouliao.autoprod.liaoyuan.security.LoginHelper;

/**
 * Created with IntelliJ IDEA.
 *
 * @author ld
 * @date 2022/8/29
 */
public class AuthUtil {
    public static AccountBO getCurrentUser() {
//        String accountId = UserContext.get("accountId");
//        String accountName = UserContext.get("accountName");
//        String accountType = UserContext.get("accountType");
//        String shopName = UserContext.get("shopName");
//        String channelAccountId = UserContext.get("channelAccountId");
//        if (StrUtil.isBlank(accountType) || StrUtil.isBlank(accountName) || StrUtil.isBlank(accountId)) {
//            throw new BizException("用户未登录");
//        }
//        return new AccountBO(accountId, accountName, AccountType.valueOf(accountType), shopName, channelAccountId);
        return LoginHelper.getLoginUser();
    }

    public static void setAccount(AccountDO account){
        UserContext.set("accountId",account.getAccountId());
        UserContext.set("accountName",account.getAccountName());
        UserContext.set("accountType","");
        UserContext.set("shopName",account.getShopName());
        UserContext.set("channelAccountId", StrUtil.blankToDefault(account.getChannelAccountId(), ""));
    }

    public static void updateLoginAccount(AccountBO accountBO){
        LoginHelper.updateLoginUser(accountBO);
    }

    public static SnapshotMicroAppUserDO getCurrentMicroAppUser() {
        SnapshotMicroAppUserDO snapshotMicroAppUserDO = JSONObject.parseObject(UserContext.get("microAppUser"), SnapshotMicroAppUserDO.class);
        if (ObjectUtil.isNull(snapshotMicroAppUserDO)) {
            throw new BizException("小程序用户未登录");
        }
        return snapshotMicroAppUserDO;
    }

    public static String getAccountId() {
        try {
            return getCurrentMicroAppUser().getLyAccountId();
        }catch (Exception e){
            AccountBO currentUser = getCurrentUser();
            if (currentUser == null) {
                return null;
            }
            return currentUser.getAccountId();
        }
    }

    public static Long getCurrentDeptId(){
       return getCurrentUser().getCurrentDeptId();
    }

    public static String getCurrentCorpId(){
       return getCurrentUser().getCorp().getCorpId();
    }

    /**
     * 获取字符串类型的部门id
     */
    public static String getDeptId() {
        return StrUtil.toString(getCurrentUser().getCurrentDeptId());
    }
}
