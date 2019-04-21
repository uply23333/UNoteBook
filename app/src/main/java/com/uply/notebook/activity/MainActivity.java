package com.uply.notebook.activity;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uply.notebook.R;
import com.uply.notebook.UplyNoteBook;
import com.uply.notebook.fragment.AllNotesFragment;
import com.uply.notebook.fragment.MyCalendarFragment;
import com.uply.notebook.fragment.SearchNoteFragment;
import com.uply.notebook.fragment.SettingFragment;
import com.uply.notebook.service.AlarmService;
import com.uply.notebook.util.JsonParser;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;



public class MainActivity extends AppCompatActivity
        implements Toolbar.OnMenuItemClickListener, AdapterView.OnItemClickListener, NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private ActionBarDrawerToggle mToggle;
    private FrameLayout mFlContent;
    private NavigationView mNvMenu;
    private DrawerLayout mDlLayout;
    private TextView mTvUserName;
    private View mHeaderView;

    private Fragment mFragments[] = new Fragment[4];
    private boolean isCalendarFragment = false;

    private String mUserName;
    private long curTimeMills;

    // 是否启动Service执行计划任务
    public static boolean IS_SYNC = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 获取配置信息
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        IS_SYNC = pref.getBoolean("auto_sync", false);

        mUserName = ((UplyNoteBook)getApplication()).getUser().getUsername();
        mFragments[0] = new AllNotesFragment();
        mFragments[1] = new SearchNoteFragment();
        mFragments[2] = new SettingFragment();
        mFragments[3] = new MyCalendarFragment();
        initView();
        showFragment(mFragments[0]);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void initView() {
        mToolbar = findViewById(R.id.id_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setOnMenuItemClickListener(this);
        mDlLayout = findViewById(R.id.id_dl_main_layout);
        mFlContent = findViewById(R.id.id_fl_main_content);
        mNvMenu =  findViewById(R.id.id_nav_menu);
        // 获取HeaderView
        mHeaderView = mNvMenu.getHeaderView(0);
        mTvUserName = mHeaderView.findViewById(R.id.id_tv_username);
        mNvMenu.setNavigationItemSelectedListener(this);
        mToggle = new ActionBarDrawerToggle(this, mDlLayout, mToolbar, R.string.AppName, R.string.AppName);
        mToggle.syncState();
        mDlLayout.setDrawerListener(mToggle);
        mTvUserName.setText(mUserName);
    }


    /**
     * 显示指定的Fragment
     */
    private void showFragment(Fragment fragment) {
        getFragmentManager().beginTransaction().replace(R.id.id_fl_main_content, fragment).commit();
    }


    /**
     * 添加菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * 设置添加事件
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_menu_add_note_auto:

                RecognizerDialog mDialog = new RecognizerDialog(this, new InitListener() {
                    @Override
                    public void onInit(int i) {

                    }
                });
                final List<String> contents = new ArrayList<>();
                mDialog.setListener( new RecognizerDialogListener() {
                    @Override
                    public void onResult(RecognizerResult recognizerResult, boolean b) {
                        if (recognizerResult != null) {
                            contents.add(JsonParser.parseIatResult(recognizerResult.getResultString()));
                        }
                        if (b) { // 录音结束
                            Intent intent = new Intent(MainActivity.this, isCalendarFragment? CalendarDetailActivity.class: NoteDetailActivity.class);
                            StringBuilder stringBuilder = new StringBuilder();
                            for (String str: contents) {
                                stringBuilder.append(str + "\n");
                            }
                            intent.putExtra("SPEECH_CONTENT", stringBuilder.toString());
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onError(SpeechError speechError) {
                        Toast.makeText(MainActivity.this, speechError.getErrorDescription(), Toast.LENGTH_SHORT).show();
                    }
                });

                mDialog.show();
                break;
            case R.id.id_menu_add_note:
                Intent intent = new Intent(this, isCalendarFragment? CalendarDetailActivity.class: NoteDetailActivity.class);
                startActivity(intent);
                break;
        }
        return false;
    }

    /**
     * 切换到相应的Fragment
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        getFragmentManager().beginTransaction()
                .replace(R.id.id_fl_main_content, mFragments[position]).commit();
        mDlLayout.closeDrawers();
    }


    /**
     * 点击侧滑触发相应的事件
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_all_notes:
                isCalendarFragment = false;
                showFragment(mFragments[0]);
                break;
            case R.id.nav_search:
                isCalendarFragment = false;
                showFragment(mFragments[1]);
                break;
            case R.id.nav_setting:
                isCalendarFragment = false;
                showFragment(mFragments[2]);
                break;
            case R.id.nav_calendar:
                isCalendarFragment = true;
                showFragment(mFragments[3]);
                break;
            case R.id.nav_logout:
                logout();
                break;
            default:
                Toast.makeText(this, "功能开发中", Toast.LENGTH_SHORT).show();
        }
        // 关闭菜单
        mDlLayout.closeDrawer(GravityCompat.START);
        // 表示已经处理完毕点击事件
        return true;
    }


    /**
     * 退出当前用户
     */
    private void logout() {
        BmobUser.logOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 单击后退键时,提示用户是否退出
     */
    @Override
    public void onBackPressed() {
        //curTimeMills = System.currentTimeMillis();
        if (mDlLayout.isDrawerOpen(GravityCompat.START)) {
            mDlLayout.closeDrawer(GravityCompat.START);
        } else {
            // 关闭程序
            exitAPP();
        }
    }

    /**
     * 两秒内单击两下即可关闭APP
     */
    private void exitAPP() {

        if (System.currentTimeMillis() - curTimeMills > 2000) {
            Snackbar.make(mDlLayout, "再单击一下即可退出", Snackbar.LENGTH_SHORT).show();
            curTimeMills = System.currentTimeMillis();
        } else {
            finish();
        }

    }
}
