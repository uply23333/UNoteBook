package com.uply.notebook.service;

import com.uply.notebook.activity.LoginActivity;
import com.uply.notebook.activity.SignUpActivity;

import cn.bmob.v3.BmobUser;

/**
 * @Auther: Uply
 * @Date: 2019/3/16 16:52
 * @Description:
 */
public class UserService {
    /**
     * 账号密码登录
     */
    public static void login(final String name, final String pwd, final LoginActivity loginActivity) {
        final BmobUser user = new BmobUser();
        user.setUsername(name);
        user.setPassword(pwd);
        user.login(loginActivity.new LoginListener());
    }

    /**signUp
     * 账号密码注册
     */
    public static void signUp(final String name, final String pwd, final SignUpActivity view) {
        final BmobUser user = new BmobUser();
        user.setUsername(name);
        user.setPassword(pwd);
        user.signUp(view.new SignUpCallBack());
    }
}
