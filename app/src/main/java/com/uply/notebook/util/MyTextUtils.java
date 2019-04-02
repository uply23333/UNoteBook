package com.uply.notebook.util;

/**
 * @Auther: Uply
 * @Date: 2019/3/16 18:39
 * @Description:
 */
public class MyTextUtils {
    /**
     * 用户名必须大于4位
     */
    public static boolean isUserNameValid(String userName) {
        return userName.length() >= 4;
    }

    /**
     * 密码必须大于5位
     */
    public static boolean isPasswordValid(String password) {
        return password.length() >= 5;
    }
}
