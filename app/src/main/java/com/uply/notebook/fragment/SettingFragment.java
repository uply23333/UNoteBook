package com.uply.notebook.fragment;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.view.View;

import com.uply.notebook.R;


/**
 * Created by yangtianrui on 16-5-23.
 * <p/>
 * 程序设置界面,提供退出功能
 */
public class SettingFragment extends PreferenceFragment {

    private CheckBoxPreference checkBoxPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 显示资源文件
        addPreferencesFromResource(R.xml.preferences);
    }

}
