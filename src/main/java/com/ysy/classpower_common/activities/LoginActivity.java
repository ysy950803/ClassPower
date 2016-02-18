package com.ysy.classpower_common.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
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
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.ysy.classpower.R;
import com.ysy.classpower_student.activities.home.StudentWelcomeActivity;
import com.ysy.classpower_teacher.activities.home.TeacherHomeActivity;
import com.ysy.classpower_teacher.activities.home.TeacherWelcomeActivity;
import com.ysy.classpower_utils.ConnectionDetector;
import com.ysy.classpower_utils.PostJsonAndGetCallback;
import com.ysy.classpower_utils.ReadJsonByGson;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

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

    private static final String LOGIN_URL = "http://10.0.2.2:5000/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
//        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin(2); // or 1
                    return true;
                }
                return false;
            }
        });

        Button mStudentSignInButton = (Button) findViewById(R.id.student_sign_in_button);
        mStudentSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(2);
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

        Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
                if (cd.isConnectingToInternet())
                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                else
                    Toast.makeText(LoginActivity.this, "请检查网络连接！", Toast.LENGTH_SHORT).show();
            }
        });
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

    private void checkAccountCorrect(String userId, String password, final int role) {
        JSONObject userInfoObject = new JSONObject();
        try {
            userInfoObject.put("uid", userId);
            userInfoObject.put("pwd", password);
            userInfoObject.put("role", role);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = userInfoObject.toString();

        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        if (cd.isConnectingToInternet()) {
            new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), LOGIN_URL, json, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    if (i == 0) {
                        Toast.makeText(LoginActivity.this, "服务器连接超时！", Toast.LENGTH_SHORT).show();
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
                        String token = jsonByGson.getValue("token");
                        String name = jsonByGson.getValue("name");
                        String userId = "";
                        String major = "";
                        String classNo = "";
                        String gender = jsonByGson.getValue("gender");
                        if (role == 2) {
                            userId = jsonByGson.getValue("student_id");
                            major = jsonByGson.getValue("major");
                            classNo = jsonByGson.getValue("class_no");
                        } else if (role == 1) {
                            userId = jsonByGson.getValue("teacher_id");
                        }

                        storeCallBakOfLogin(token, name, userId, gender, major, classNo);

                        mEmailView.setError(null);
                        mPasswordView.setError(null);
                        if (role == 1) {
                            showProgress(true);
                            startActivity(new Intent(LoginActivity.this, TeacherWelcomeActivity.class));
                            finish();
                        } else if (role == 2) {
                            showProgress(true);
                            startActivity(new Intent(LoginActivity.this, StudentWelcomeActivity.class));
                            finish();
                        }
                    }
                }
            });
        } else
            Toast.makeText(LoginActivity.this, "请检查网络连接！", Toast.LENGTH_SHORT).show();

    }

    private void storeCallBakOfLogin(String token, String name, String userId, String gender, String major, String classNo) {
        SharedPreferences token_sp = getSharedPreferences("token", MODE_PRIVATE);
        SharedPreferences name_sp = getSharedPreferences("name", MODE_PRIVATE);
        SharedPreferences userId_sp = getSharedPreferences("userId", MODE_PRIVATE);
        SharedPreferences gender_sp = getSharedPreferences("gender", MODE_PRIVATE);
        SharedPreferences major_sp = getSharedPreferences("major", MODE_PRIVATE);
        SharedPreferences classNo_sp = getSharedPreferences("classNo", MODE_PRIVATE);
        SharedPreferences.Editor token_editor = token_sp.edit();
        SharedPreferences.Editor name_editor = name_sp.edit();
        SharedPreferences.Editor userId_editor = userId_sp.edit();
        SharedPreferences.Editor gender_editor = gender_sp.edit();
        SharedPreferences.Editor major_editor = major_sp.edit();
        SharedPreferences.Editor classNo_editor = classNo_sp.edit();
        token_editor.putString("token", token);
        name_editor.putString("name", name);
        userId_editor.putString("userId", userId);
        gender_editor.putString("gender", gender);
        major_editor.putString("major", major);
        classNo_editor.putString("classNo", classNo);
        token_editor.apply();
        name_editor.apply();
        userId_editor.apply();
        gender_editor.apply();
        major_editor.apply();
        classNo_editor.apply();
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
                new ArrayAdapter<>(LoginActivity.this,
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
