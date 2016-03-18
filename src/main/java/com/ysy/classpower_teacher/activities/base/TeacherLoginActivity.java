package com.ysy.classpower_teacher.activities.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.ysy.classpower.R;
import com.ysy.classpower_common.constant.ServerUrlConstant;
import com.ysy.classpower_teacher.activities.home.TeacherWelcomeActivity;
import com.ysy.classpower_utils.ConnectionDetector;
import com.ysy.classpower_utils.OwnApp;
import com.ysy.classpower_utils.json_processor.PostJsonAndGetCallback;
import com.ysy.classpower_utils.json_processor.ReadJsonByGson;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class TeacherLoginActivity extends AppCompatActivity {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private View focusView;

    private OwnApp ownApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_login);
        ownApp = (OwnApp) getApplication();
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
//        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin(1);
                    return true;
                }
                return false;
            }
        });

        Button mTeacherSignInButton = (Button) findViewById(R.id.teacher_sign_in_button);
        mTeacherSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin(1);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button teacherRegisterButton = (Button) findViewById(R.id.teacher_register_button);
        teacherRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
                if (cd.isConnectingToInternet())
                    startActivity(new Intent(TeacherLoginActivity.this, TeacherRegisterActivity.class));
                else
                    Toast.makeText(TeacherLoginActivity.this, "请检查网络连接！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void attemptLogin(int role) {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String userId = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(userId)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            focusView.requestFocus();
        } else if (!TextUtils.isEmpty(userId) && TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            focusView.requestFocus();
        } else if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(password)) {
            checkAccountCorrect(userId, password, role);
        }

    }

    private void checkAccountCorrect(String userId, String password, int role) {
        JSONObject userInfoObject = new JSONObject();
        try {
            userInfoObject.put("user_id", userId);
            userInfoObject.put("password", password);
            userInfoObject.put("role", role);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = userInfoObject.toString();

        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        if (cd.isConnectingToInternet()) {
            new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), ServerUrlConstant.getUserLoginUrl(ownApp.getURL_FIGURE()), json, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    if (i == 0) {
                        Toast.makeText(TeacherLoginActivity.this, "服务器连接超时！", Toast.LENGTH_SHORT).show();
                    } else if (i == 400) {
                        ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                        if (s.contains("error_code")) {
                            if (jsonByGson.getValue("error_code").equals("701")) {
                                mEmailView.setError(getString(R.string.error_invalid_email));
                                focusView = mEmailView;
                                focusView.requestFocus();
                            } else {
                                if (jsonByGson.getValue("error_code").equals("602")) {
                                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                                    focusView = mPasswordView;
                                    focusView.requestFocus();
                                }
                            }
                        }
                    }
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                    if (s.contains("token")) {
                        // 共有属性
                        String email = jsonByGson.getValue("email");
                        boolean gender = jsonByGson.getBoolValue("gender");
                        String name = jsonByGson.getValue("name");
                        String tel = jsonByGson.getValue("tel");
                        String token = jsonByGson.getValue("token");
                        String userId = jsonByGson.getValue("user_id");
                        // 教师属性
                        String office = jsonByGson.getValue("office");
                        String title = jsonByGson.getValue("title");
                        // 特殊属性（待定：courses对象数组处理）

                        saveCallBakOfLogin(email, gender, name, tel, token, userId, office, title);

                        mEmailView.setError(null);
                        mPasswordView.setError(null);
                        showProgress(true);
                        startActivity(new Intent(TeacherLoginActivity.this, TeacherWelcomeActivity.class));
                        finish();
                    }
                }
            });
        } else
            Toast.makeText(TeacherLoginActivity.this, "请检查网络连接！", Toast.LENGTH_SHORT).show();

    }

    private void saveCallBakOfLogin(String email, boolean gender, String name, String tel, String token, String userId,
                                    String office, String title) {
        // 共有属性
        SharedPreferences email_sp = getSharedPreferences("email", MODE_PRIVATE);
        SharedPreferences gender_sp = getSharedPreferences("gender", MODE_PRIVATE);
        SharedPreferences name_sp = getSharedPreferences("name", MODE_PRIVATE);
        SharedPreferences tel_sp = getSharedPreferences("tel", MODE_PRIVATE);
        SharedPreferences token_sp = getSharedPreferences("token", MODE_PRIVATE);
        SharedPreferences userId_sp = getSharedPreferences("userId", MODE_PRIVATE);
        // 教师属性
        SharedPreferences office_sp = getSharedPreferences("office", MODE_PRIVATE);
        SharedPreferences title_sp = getSharedPreferences("title", MODE_PRIVATE);

        SharedPreferences.Editor email_editor = email_sp.edit();
        SharedPreferences.Editor gender_editor = gender_sp.edit();
        SharedPreferences.Editor name_editor = name_sp.edit();
        SharedPreferences.Editor tel_editor = tel_sp.edit();
        SharedPreferences.Editor token_editor = token_sp.edit();
        SharedPreferences.Editor userId_editor = userId_sp.edit();

        SharedPreferences.Editor office_editor = office_sp.edit();
        SharedPreferences.Editor title_editor = title_sp.edit();

        email_editor.putString("email", email);
        gender_editor.putBoolean("gender", gender);
        name_editor.putString("name", name);
        tel_editor.putString("tel", tel);
        token_editor.putString("token", token);
        userId_editor.putString("userId", userId);

        office_editor.putString("office", office);
        title_editor.putString("title", title);

        email_editor.apply();
        gender_editor.apply();
        name_editor.apply();
        tel_editor.apply();
        token_editor.apply();
        userId_editor.apply();

        office_editor.apply();
        title_editor.apply();
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

    // 连续按两次退出
    private long exitTime;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "请再按一次以退出！", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return false; // 此处返回true达到的效果相同，但若不返回值，会出现按一次就显示提示并直接退出
        }
        return super.onKeyDown(keyCode, event);
    }

}
