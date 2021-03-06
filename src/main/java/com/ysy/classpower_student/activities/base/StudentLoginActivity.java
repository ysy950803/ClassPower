package com.ysy.classpower_student.activities.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityOptions;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.ysy.classpower.R;
import com.ysy.classpower_common.constant.ErrorCodeConstant;
import com.ysy.classpower_common.constant.ServerUrlConstant;
import com.ysy.classpower_utils.ConnectionDetector;
import com.ysy.classpower_utils.DestroyAllActivities;
import com.ysy.classpower_utils.OwnApp;
import com.ysy.classpower_utils.for_design.MaterialButtonRectangle;
import com.ysy.classpower_utils.json_processor.PostJsonAndGetCallback;
import com.ysy.classpower_utils.json_processor.ReadJsonByGson;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class StudentLoginActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
//    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private View focusView;

    private int betaHiddenUrlEditorCount;
    private OwnApp ownApp;

    private ProgressDialog waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);
        DestroyAllActivities.getInstance().exit();
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
                    attemptLogin(2);
                    return true;
                }
                return false;
            }
        });

        MaterialButtonRectangle mStudentSignInButton = (MaterialButtonRectangle) findViewById(R.id.student_sign_in_button);
        mStudentSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(2);
            }
        });
        mStudentSignInButton.setRippleSpeed(36f);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "若需要帮助，请点击查看。", Snackbar.LENGTH_LONG)
                        .setAction("更多", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();
            }
        });

        final RelativeLayout studentRegisterTipsLayout = (RelativeLayout) findViewById(R.id.student_register_tips_layout);
        assert studentRegisterTipsLayout != null;
        studentRegisterTipsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
                if (cd.isConnectingToInternet()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Intent intent = new Intent(StudentLoginActivity.this, StudentRegisterActivity.class);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(StudentLoginActivity.this, studentRegisterTipsLayout, "student_login_register_transition");
                        startActivity(intent, options.toBundle());
                    } else
                        startActivity(new Intent(StudentLoginActivity.this, StudentRegisterActivity.class));
                } else
                    Toast.makeText(StudentLoginActivity.this, "请检查网络连接！", Toast.LENGTH_SHORT).show();
            }
        });

        betaHiddenUrlEditorCount = 0;
        TextView betaHiddenUrlEditor = (TextView) findViewById(R.id.beta_hidden_url_editor);
        assert betaHiddenUrlEditor != null;
        betaHiddenUrlEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++betaHiddenUrlEditorCount;
                if (betaHiddenUrlEditorCount == 8) {
                    editorEnterTips();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        betaHiddenUrlEditorCount = 0;
        super.onResume();
    }

    private void editorEnterTips() {
        final EditText editText = new EditText(getApplicationContext());
        editText.setTextColor(getResources().getColor(R.color.colorPrimary));
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("开发者测试功能（URL编辑器）").setMessage("请输入开发者管理密钥：").setView(editText).setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText().toString().equals("950803"))
                            editorTips();
                        else {
                            Toast.makeText(getApplicationContext(), "密钥错误，非开发者请勿使用此功能！", Toast.LENGTH_SHORT).show();
                            betaHiddenUrlEditorCount = 0;
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                betaHiddenUrlEditorCount = 0;
                dialog.cancel();
            }
        });
        final android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();

        //设置透明度
        Window window = alert.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.75f;
        window.setAttributes(lp);
    }

    private void editorTips() {
        final EditText editText = new EditText(getApplicationContext());
        editText.setText("42.96.197.43");
        editText.setTextColor(getResources().getColor(R.color.colorPrimary));
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("开发者测试功能（URL编辑器）").setMessage("请输入服务器URL：").setView(editText).setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ownApp.setURL_FIGURE(editText.getText().toString());
                        betaHiddenUrlEditorCount = 0;
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                betaHiddenUrlEditorCount = 0;
                dialog.cancel();
            }
        });
        final android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();

        //设置透明度
        Window window = alert.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.75f;
        window.setAttributes(lp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            this.finish();
            System.exit(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
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
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        if (cd.isConnectingToInternet()) {
            JSONObject userInfoObject = new JSONObject();
            try {
                userInfoObject.put("user_id", userId);
                userInfoObject.put("password", password);
                userInfoObject.put("role", role);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String json = userInfoObject.toString();
            new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), ServerUrlConstant.getUserLoginUrl(ownApp.getURL_FIGURE()), json, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    waitDialog = ProgressDialog.show(StudentLoginActivity.this, "正在登录", "请稍等…");
                    super.onStart();
                }

                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    waitDialog.dismiss();
                    if (i == 0) {
                        Toast.makeText(StudentLoginActivity.this, "服务器连接超时，请重试！", Toast.LENGTH_SHORT).show();
                    } else if (i == 400) {
                        ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                        if (s.contains("error_code")) {
                            if (jsonByGson.getValue("error_code").equals(ErrorCodeConstant.USER_NOT_FOUND)) {
                                mEmailView.setError(getString(R.string.error_invalid_email));
                                focusView = mEmailView;
                                focusView.requestFocus();
                            } else {
                                if (jsonByGson.getValue("error_code").equals(ErrorCodeConstant.WRONG_PASSWORD)) {
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
                    waitDialog.dismiss();
                    ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                    if (s.contains("token")) {
                        // 共有属性
                        String currentWeek = jsonByGson.getValue("current_week");
                        String email = jsonByGson.getArrayValue("user", "email");
                        boolean gender = jsonByGson.getArrayBoolValue("user", "gender");
                        String name = jsonByGson.getArrayValue("user", "name");
                        String tel = jsonByGson.getArrayValue("user", "tel");
                        String token = jsonByGson.getValue("token");
                        String userId = jsonByGson.getArrayValue("user", "user_id");
                        // 学生属性
                        String className = jsonByGson.getArrayValue("user", "class_name");
                        String majorName = jsonByGson.getArrayValue("user", "major_name");

                        saveCallBakOfLogin(s, currentWeek, email, gender, name, tel, token, userId, className, majorName);

                        mEmailView.setError(null);
                        mPasswordView.setError(null);
//                        showProgress(true);
                        startActivity(new Intent(StudentLoginActivity.this, StudentWelcomeActivity.class));
                        finish();
                    } else
                        Toast.makeText(StudentLoginActivity.this, "未知错误，请重试！", Toast.LENGTH_SHORT).show();
                }
            });
        } else
            Toast.makeText(StudentLoginActivity.this, "登录失败，请检查网络连接！", Toast.LENGTH_SHORT).show();

    }

    private void saveCallBakOfLogin(String loginJson, String currentWeek, String email, boolean gender, String name, String tel, String token, String userId,
                                    String className, String majorName) {
        // 共有属性
        SharedPreferences currentWeek_sp = getSharedPreferences("currentWeek", MODE_PRIVATE);
        SharedPreferences email_sp = getSharedPreferences("email", MODE_PRIVATE);
        SharedPreferences gender_sp = getSharedPreferences("gender", MODE_PRIVATE);
        SharedPreferences name_sp = getSharedPreferences("name", MODE_PRIVATE);
        SharedPreferences tel_sp = getSharedPreferences("tel", MODE_PRIVATE);
        SharedPreferences token_sp = getSharedPreferences("token", MODE_PRIVATE);
        SharedPreferences userId_sp = getSharedPreferences("userId", MODE_PRIVATE);
        SharedPreferences password_sp = getSharedPreferences("password", MODE_PRIVATE);
        // 学生属性
        SharedPreferences className_sp = getSharedPreferences("className", MODE_PRIVATE);
        SharedPreferences majorName_sp = getSharedPreferences("majorName", MODE_PRIVATE);
        // 登录返回大量信息（包括课程和时间）
        SharedPreferences loginJson_sp = getSharedPreferences("loginJson", MODE_PRIVATE);

        SharedPreferences.Editor currentWeek_editor = currentWeek_sp.edit();
        SharedPreferences.Editor email_editor = email_sp.edit();
        SharedPreferences.Editor gender_editor = gender_sp.edit();
        SharedPreferences.Editor name_editor = name_sp.edit();
        SharedPreferences.Editor tel_editor = tel_sp.edit();
        SharedPreferences.Editor token_editor = token_sp.edit();
        SharedPreferences.Editor userId_editor = userId_sp.edit();
        SharedPreferences.Editor password_editor = password_sp.edit();

        SharedPreferences.Editor className_editor = className_sp.edit();
        SharedPreferences.Editor majorName_editor = majorName_sp.edit();

        SharedPreferences.Editor loginJson_editor = loginJson_sp.edit();

        currentWeek_editor.putString("currentWeek", currentWeek);
        email_editor.putString("email", email);
        gender_editor.putBoolean("gender", gender);
        name_editor.putString("name", name);
        tel_editor.putString("tel", tel);
        token_editor.putString("token", token);
        userId_editor.putString("userId", userId);
        password_editor.putString("password", mPasswordView.getText().toString());

        className_editor.putString("className", className);
        majorName_editor.putString("majorName", majorName);

        loginJson_editor.putString("loginJson", loginJson);
//        Log.d("TEST_LoginJson", loginJson);

        currentWeek_editor.commit();
        loginJson_editor.commit();

        email_editor.apply();
        gender_editor.apply();
        name_editor.apply();
        tel_editor.apply();
        token_editor.apply();
        userId_editor.apply();
        password_editor.apply();

        className_editor.apply();
        majorName_editor.apply();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(StudentLoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
//        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
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
