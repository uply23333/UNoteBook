package com.example.yangtianrui.notebook;

import android.app.Application;

import com.example.yangtianrui.notebook.config.Constants;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobBatch;
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
    }

    public BmobUser getUser() {
        return user;
    }
}
