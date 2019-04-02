package com.uply.notebook.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.uply.notebook.R;
import com.uply.notebook.service.UserService;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    private EditText mEtUsername;
    private EditText mEtPwd;
    private View mProgressView;
    private View mLoginFormView;
    private Button mBtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initEvent();
    }


    /**
     * 初始化组件
     */
    private void initView() {
        mEtPwd = findViewById(R.id.id_et_password);
        mEtUsername = findViewById(R.id.id_et_username);
        mBtnLogin = findViewById(R.id.id_btn_login);
        mProgressView = findViewById(R.id.id_pb_loading);
        mLoginFormView = findViewById(R.id.id_lv_login_form);
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        mEtPwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.integer.action_sign_in) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mBtnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    /**
     * 接收登陆的结果,并判断错误
     */
    private void attemptLogin() {
        mEtUsername.setError(null);
        mEtPwd.setError(null);

        String userName = mEtUsername.getText().toString();
        String password = mEtPwd.getText().toString();

        boolean success = true;
        View view = null;

        // 检查用户名
        if (TextUtils.isEmpty(userName)) {
            mEtUsername.setError("用户名是必填项");
            view = mEtUsername;
            success = false;
        } else if (!isUserNameValid(userName)) {
            mEtUsername.setError("用户名必须大于4位");
            view = mEtUsername;
            success = false;
        }
        // 检查密码
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mEtPwd.setError("密码必须大于5位");
            view = mEtPwd;
            success = false;
        }

        if (success) {
            showProgress(true);
            UserService.login(userName, password, this);
        } else {
            view.requestFocus();
        }
    }

    public final class LoginListener extends SaveListener<BmobUser>  {
        @Override
        public void done(BmobUser bmobUser, BmobException e) {
            if (e == null) {
                Snackbar bar = Snackbar.make(mLoginFormView, "登陆成功,欢迎回来!", Snackbar.LENGTH_SHORT);
                final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                bar.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        startActivity(intent);
                        finish();
                    }
                });
                bar.show();
            } else {
                showProgress(false);
                Snackbar.make(mLoginFormView, "登录失败：" + e.getMessage(),
                        Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 用户名必须大于4位
     */
    private boolean isUserNameValid(String userName) {
        return userName.length() >= 4;
    }

    /**
     * 密码必须大于5位
     */
    private boolean isPasswordValid(String password) {
        return password.length() >= 5;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * 点击此按钮时,注册新用户
     */
    public void signUp(View view) {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}

