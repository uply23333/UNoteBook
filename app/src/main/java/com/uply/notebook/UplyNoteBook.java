package com.uply.notebook;

import android.app.Application;

import com.uply.notebook.config.Constants;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;


public class UplyNoteBook extends Application {

    private BmobUser user;

    /**
     * 功能描述:
     *
     * @param:
     * @return:
     * @auther: Uply
     * @date: 2019/3/16
     */
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化Bmob
        Bmob.initialize(this, Constants.BMOB_API_KEY);
        user = BmobUser.getCurrentUser(BmobUser.class);
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=" + Constants.XUNFEI_API_KEY);
    }

    public BmobUser getUser() {
        return user;
    }
}
