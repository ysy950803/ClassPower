package com.ysy.classpower_common.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.ysy.classpower.R;
import com.ysy.classpower_utils.PostJsonAndGetCallback;
import com.ysy.classpower_utils.ReadJsonByGson;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private EditText userIdEditText;
    private EditText passWordEditText;
    private EditText nameEditText;
    private EditText emailEditText;
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;
    private Button teacherRegisterButton;
    private Button studentRegisterButton;
    private Button backToLoginButton;
    private TextView userIdTextView;
    private TextView nameTextView;
    private TextView sexTextView;
    private TextView emailTextView;
    private TextView roleTextView;

    private RelativeLayout registerLayout;
    private RelativeLayout registerSuccessLayout;

    private int sexNumber;
    private JSONObject registerObject;
    private static final String REGISTER_URL = "http://10.0.2.2:5000/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setupActionBar();

        registerObject = new JSONObject();

        userIdEditText = (EditText) findViewById(R.id.user_id_edit_text);
        passWordEditText = (EditText) findViewById(R.id.password_edit_text);
        nameEditText = (EditText) findViewById(R.id.name_edit_text);
        emailEditText = (EditText) findViewById(R.id.email_edit_text);
        maleRadioButton = (RadioButton) findViewById(R.id.male_radio_button);
        femaleRadioButton = (RadioButton) findViewById(R.id.female_radio_button);
        teacherRegisterButton = (Button) findViewById(R.id.teacher_register_button);
        studentRegisterButton = (Button) findViewById(R.id.student_register_button);
        backToLoginButton = (Button) findViewById(R.id.back_to_login_button);
        registerLayout = (RelativeLayout) findViewById(R.id.register_layout);
        registerSuccessLayout = (RelativeLayout) findViewById(R.id.register_success_layout);
        userIdTextView = (TextView) findViewById(R.id.user_id_text_view);
        nameTextView = (TextView) findViewById(R.id.teacher_title_text_view);
        sexTextView = (TextView) findViewById(R.id.sex_text_view);
        emailTextView = (TextView) findViewById(R.id.email_text_view);
        roleTextView = (TextView) findViewById(R.id.role_text_view);

        teacherRegisterButton.setOnClickListener(onClickListener);
        studentRegisterButton.setOnClickListener(onClickListener);
        maleRadioButton.setOnClickListener(onClickListener);
        femaleRadioButton.setOnClickListener(onClickListener);
        backToLoginButton.setOnClickListener(onClickListener);

        maleRadioButton.setChecked(true);
        sexNumber = 0;
        femaleRadioButton.setChecked(false);
        registerLayout.setVisibility(View.VISIBLE);
        registerSuccessLayout.setVisibility(View.GONE);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.student_register_button:
                    registerInfoAsJson(2);
                    String studentInfoJson = registerObject.toString();
                    new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), REGISTER_URL, studentInfoJson, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

                        }

                        @Override
                        public void onSuccess(int i, Header[] headers, String s) {
                            setRegisterSuccessInfoByJson(s, 2);
                            registerLayout.setVisibility(View.GONE);
                            registerSuccessLayout.setVisibility(View.VISIBLE);
                        }
                    });
                    break;

                case R.id.teacher_register_button:
                    registerInfoAsJson(1);
                    String teacherInfoJson = registerObject.toString();
                    new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), REGISTER_URL, teacherInfoJson, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

                        }

                        @Override
                        public void onSuccess(int i, Header[] headers, String s) {
                            setRegisterSuccessInfoByJson(s, 1);
                            registerLayout.setVisibility(View.GONE);
                            registerSuccessLayout.setVisibility(View.VISIBLE);
                        }
                    });
                    break;

                case R.id.male_radio_button:
                    maleRadioButton.setChecked(true);
                    sexNumber = 0;
                    femaleRadioButton.setChecked(false);
                    break;

                case R.id.female_radio_button:
                    maleRadioButton.setChecked(false);
                    femaleRadioButton.setChecked(true);
                    sexNumber = 1;
                    break;

                case R.id.back_to_login_button:
                    finish();
                    break;

            }
        }
    };

    private void setRegisterSuccessInfoByJson(String json, int role) {

        ReadJsonByGson jsonByGson = new ReadJsonByGson(json);
        if (role == 2) {
            userIdTextView.setText(jsonByGson.getArrayValue("user_info", "student_id"));
            roleTextView.setText("学生");
        } else if (role == 1) {
            userIdTextView.setText(jsonByGson.getArrayValue("user_info", "teacher_id"));
            roleTextView.setText("教师");
        }
        if (jsonByGson.getArrayValue("user_info", "gender").equals("0")) {
            sexTextView.setText("男");
        } else if (jsonByGson.getArrayValue("user_info", "gender").equals("1")) {
            sexTextView.setText("女");
        }
        nameTextView.setText(jsonByGson.getArrayValue("user_info", "name"));
        emailTextView.setText(jsonByGson.getArrayValue("user_info", "email"));

    }

    private void registerInfoAsJson(int role) {
        try {
            registerObject.put("uid", userIdEditText.getText().toString());
            registerObject.put("pwd", passWordEditText.getText().toString());
            registerObject.put("name", nameEditText.getText().toString());
            registerObject.put("email", emailEditText.getText().toString());
            registerObject.put("role", role);
            registerObject.put("gender", sexNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //覆盖整个Activity的返回按钮
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed(); //调用onKeyDown内部方法
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
