package com.ysy.classpower_teacher.activities.base;

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
import com.ysy.classpower_common.constant.ServerUrlConstant;
import com.ysy.classpower_utils.json_processor.PostJsonAndGetCallback;
import com.ysy.classpower_utils.json_processor.ReadJsonByGson;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class TeacherRegisterActivity extends AppCompatActivity {

    private EditText userIdEditText;
    private EditText passWordEditText;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText telEditText;
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;
    private Button handleRegisterButton;
    private Button backToLoginButton;

    private TextView userIdTextView;
    private TextView nameTextView;
    private TextView sexTextView;
    private TextView emailTextView;
    private TextView roleTextView;

    private RelativeLayout registerLayout;
    private RelativeLayout registerSuccessLayout;

    private boolean isMale;
    private JSONObject registerObject;
    private static final String USER_REGISTER_TEACHER_URL = ServerUrlConstant.USER_REGISTER_TEACHER_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_register);
        setupActionBar();

        registerObject = new JSONObject();

        registerLayout = (RelativeLayout) findViewById(R.id.register_layout);
        registerSuccessLayout = (RelativeLayout) findViewById(R.id.register_success_layout);

        userIdEditText = (EditText) findViewById(R.id.student_register_userId_editText);
        passWordEditText = (EditText) findViewById(R.id.student_register_password_editText);
        nameEditText = (EditText) findViewById(R.id.student_register_double_password_editText);
        emailEditText = (EditText) findViewById(R.id.student_register_name_editText);
        telEditText = (EditText) findViewById(R.id.student_register_email_editText);
        maleRadioButton = (RadioButton) findViewById(R.id.male_radio_button);
        femaleRadioButton = (RadioButton) findViewById(R.id.female_radio_button);
        handleRegisterButton = (Button) findViewById(R.id.handle_register_button);

        userIdTextView = (TextView) findViewById(R.id.user_id_text_view);
        nameTextView = (TextView) findViewById(R.id.name_text_view);
        sexTextView = (TextView) findViewById(R.id.sex_text_view);
        emailTextView = (TextView) findViewById(R.id.email_text_view);
        roleTextView = (TextView) findViewById(R.id.role_text_view);
        backToLoginButton = (Button) findViewById(R.id.back_to_login_button);

        handleRegisterButton.setOnClickListener(onClickListener);
        maleRadioButton.setOnClickListener(onClickListener);
        femaleRadioButton.setOnClickListener(onClickListener);
        backToLoginButton.setOnClickListener(onClickListener);

        maleRadioButton.setChecked(true);
        isMale = true;
        femaleRadioButton.setChecked(false);
        registerLayout.setVisibility(View.VISIBLE);
        registerSuccessLayout.setVisibility(View.GONE);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.handle_register_button:
                    registerInfoAsJson();
                    final String teacherInfoJson = registerObject.toString();
                    new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), USER_REGISTER_TEACHER_URL, teacherInfoJson, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

                        }

                        @Override
                        public void onSuccess(int i, Header[] headers, String s) {
                            setRegisterSuccessInfoByJson(teacherInfoJson);
                            registerLayout.setVisibility(View.GONE);
                            registerSuccessLayout.setVisibility(View.VISIBLE);
                        }
                    });
                    break;

                case R.id.male_radio_button:
                    maleRadioButton.setChecked(true);
                    isMale = true;
                    femaleRadioButton.setChecked(false);
                    break;

                case R.id.female_radio_button:
                    maleRadioButton.setChecked(false);
                    femaleRadioButton.setChecked(true);
                    isMale = false;
                    break;

                case R.id.back_to_login_button:
                    finish();
                    break;

                default:
                    break;
            }
        }
    };

    private void registerInfoAsJson() {
        try {
            registerObject.put("user_id", userIdEditText.getText().toString());
            registerObject.put("password", passWordEditText.getText().toString());
            registerObject.put("name", nameEditText.getText().toString());
            registerObject.put("email", emailEditText.getText().toString());
            registerObject.put("tel", telEditText.getText().toString());
            registerObject.put("gender", isMale);
            registerObject.put("dept_id", "");
            registerObject.put("office", "");
            registerObject.put("title", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setRegisterSuccessInfoByJson(String json) {
        ReadJsonByGson jsonByGson = new ReadJsonByGson(json);
        if (jsonByGson.getBoolValue("gender")) {
            sexTextView.setText("男");
        } else if (!jsonByGson.getBoolValue("gender")) {
            sexTextView.setText("女");
        }
        userIdTextView.setText(jsonByGson.getValue("user_id"));
        nameTextView.setText(jsonByGson.getValue("name"));
        emailTextView.setText(jsonByGson.getValue("email"));
        roleTextView.setText("教师");
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
