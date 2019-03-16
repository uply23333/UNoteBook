package com.example.yangtianrui.notebook.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yangtianrui.notebook.R;
import com.example.yangtianrui.notebook.service.UserService;
import com.example.yangtianrui.notebook.util.MyTextUtils;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * A login screen that offers login via email/password.
 */
public class SignUpActivity extends AppCompatActivity {

    // UI references.
    private EditText mEtUsername;
    private EditText mEtPwd;
    private EditText mEtVerify;
    private View mProgressView;
    private View mLoginFormView;
    private Button mBtnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initView();
        initEvent();
    }


    /**
     * 初始化组件
     */
    private void initView() {
        mEtPwd = findViewById(R.id.id_et_signup_password);
        mEtUsername = findViewById(R.id.id_et_signup_username);
        mBtnSignUp = findViewById(R.id.id_btn_signup);
        mProgressView = findViewById(R.id.id_pb_signup_loading);
        mLoginFormView = findViewById(R.id.id_lv_signup_form);
        mEtVerify = findViewById(R.id.id_et_signup_verify);
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        mEtPwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.integer.action_sign_up || id == EditorInfo.IME_NULL) {
                    attemptSignUp();
                    return true;
                }
                return false;
            }
        });

        mBtnSignUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp();
            }
        });
    }

    private void attemptSignUp() {
        // Reset errors.
        mEtUsername.setError(null);
        mEtPwd.setError(null);
        mEtVerify.setError(null);

        // Store values at the time of the login attempt.
        String userName = mEtUsername.getText().toString();
        String password = mEtPwd.getText().toString();
        String verify = mEtVerify.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // 检查用户名
        if (TextUtils.isEmpty(userName)) {
            mEtUsername.setError("用户名是必填项");
            focusView = mEtUsername;
            cancel = true;
        } else if (!MyTextUtils.isUserNameValid(userName)) {
            mEtUsername.setError("用户名必须大于4位");
            focusView = mEtUsername;
            cancel = true;
        }

        // 检查密码
        if (TextUtils.isEmpty(password) || !MyTextUtils.isPasswordValid(password)) {
            mEtPwd.setError("密码必须大于5位");
            focusView = mEtPwd;
            cancel = true;
        }

        // 检查两次密码是否一致
        if (TextUtils.isEmpty(verify) || !verify.equals(password)) {
            mEtVerify.setError("两次输入密码不一致");
            focusView = mEtVerify;
            cancel = true;
        }



        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            UserService.signUp(userName, password, SignUpActivity.this);
        }
    }

    public class SignUpCallBack extends SaveListener<BmobUser> {
        @Override
        public void done(BmobUser bmobUser, BmobException e) {
            if (e == null) {
                Snackbar bar = Snackbar.make(mLoginFormView, "注册成功！",
                        Snackbar.LENGTH_SHORT);
                bar.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        Intent intent = new Intent(SignUpActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                bar.show();
            } else {
                showProgress(false);
                Snackbar.make(mLoginFormView, "注册失败：" + e.getMessage(),
                        Snackbar.LENGTH_SHORT).show();
            }
        }
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
}

