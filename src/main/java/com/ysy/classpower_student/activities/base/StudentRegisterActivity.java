package com.ysy.classpower_student.activities.base;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.ysy.classpower.R;
import com.ysy.classpower_common.constant.ServerUrlConstant;
import com.ysy.classpower_utils.ConnectionDetector;
import com.ysy.classpower_utils.PostJsonAndGetCallback;
import com.ysy.classpower_utils.ReadJsonByGson;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StudentRegisterActivity extends AppCompatActivity {

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

    private Spinner schoolsSpinner;
    private Spinner majorsSpinner;
    private Spinner classesSpinner;
    private String majorId = "";
    private String classId = "";

    private boolean isMale;
    private JSONObject registerObject;
    private static final String USER_REGISTER_STUDENT_URL = ServerUrlConstant.USER_REGISTER_STUDENT_URL;
    private static final String USER_REGISTER_GETSCHOOLS_URL = ServerUrlConstant.USER_REGISTER_GETSCHOOLS_URL;
    private static final String USER_REGISTER_GETMAJORS_URL = ServerUrlConstant.USER_REGISTER_GETMAJORS_URL;
    private static final String USER_REGISTER_GETCLASSES_URL = ServerUrlConstant.USER_REGISTER_GETCLASSES_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);
        setupActionBar();

        registerObject = new JSONObject();

        registerLayout = (RelativeLayout) findViewById(R.id.register_layout);
        registerSuccessLayout = (RelativeLayout) findViewById(R.id.register_success_layout);

        userIdEditText = (EditText) findViewById(R.id.user_id_edit_text);
        passWordEditText = (EditText) findViewById(R.id.password_edit_text);
        nameEditText = (EditText) findViewById(R.id.name_edit_text);
        emailEditText = (EditText) findViewById(R.id.email_edit_text);
        telEditText = (EditText) findViewById(R.id.tel_editText);
        maleRadioButton = (RadioButton) findViewById(R.id.male_radio_button);
        femaleRadioButton = (RadioButton) findViewById(R.id.female_radio_button);
        handleRegisterButton = (Button) findViewById(R.id.handle_register_button);

        userIdTextView = (TextView) findViewById(R.id.user_id_text_view);
        nameTextView = (TextView) findViewById(R.id.name_text_view);
        sexTextView = (TextView) findViewById(R.id.sex_text_view);
        emailTextView = (TextView) findViewById(R.id.email_text_view);
        roleTextView = (TextView) findViewById(R.id.role_text_view);
        backToLoginButton = (Button) findViewById(R.id.back_to_login_button);

        schoolsSpinner = (Spinner) findViewById(R.id.schools_spinner);
        majorsSpinner = (Spinner) findViewById(R.id.majors_spinner);
        classesSpinner = (Spinner) findViewById(R.id.classes_spinner);

        handleRegisterButton.setOnClickListener(onClickListener);
        maleRadioButton.setOnClickListener(onClickListener);
        femaleRadioButton.setOnClickListener(onClickListener);
        backToLoginButton.setOnClickListener(onClickListener);

        maleRadioButton.setChecked(true);
        isMale = true;
        femaleRadioButton.setChecked(false);
        registerLayout.setVisibility(View.VISIBLE);
        registerSuccessLayout.setVisibility(View.GONE);

        final List<String> list = new ArrayList<>();
        list.add(0, "<---请选择学院--->");
        ArrayAdapter<String> schoolsSpinnerAdapter = new ArrayAdapter<>(StudentRegisterActivity.this, android.R.layout.simple_spinner_item, list);
        schoolsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        schoolsSpinner.setAdapter(schoolsSpinnerAdapter);
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        if (cd.isConnectingToInternet()) {
            new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), USER_REGISTER_GETSCHOOLS_URL, "{}", new TextHttpResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    if (i == 0)
                        Toast.makeText(StudentRegisterActivity.this, "服务器未响应，院校信息获取失败！", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                    String school_name_array[] = jsonByGson.getSchoolsInfo("school_name");
                    final String school_id_array[] = jsonByGson.getSchoolsInfo("school_id");
                    for (int j = 0; j < school_name_array.length; ++j) {
                        list.add(school_name_array[j]);
                    }
                    ArrayAdapter<String> schoolsSpinnerAdapter = new ArrayAdapter<>(StudentRegisterActivity.this, android.R.layout.simple_spinner_item, list);
                    schoolsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    schoolsSpinner.setAdapter(schoolsSpinnerAdapter);
                    schoolsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String school_id;
                            if (position == 0) {
                                school_id = "";
                            } else if (position > 0) {
                                school_id = school_id_array[position - 1];
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            });

        } else
            Toast.makeText(StudentRegisterActivity.this, "院校信息获取失败，请检查网络连接！", Toast.LENGTH_SHORT).show();

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.handle_register_button:
                    ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
                    if (cd.isConnectingToInternet()) {
                        registerInfoAsJson();
                        final String studentInfoJson = registerObject.toString();
                        new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), USER_REGISTER_STUDENT_URL, studentInfoJson, new TextHttpResponseHandler() {
                            @Override
                            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                                if (i == 0)
                                    Toast.makeText(StudentRegisterActivity.this, "服务器连接超时！", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(StudentRegisterActivity.this, "提交失败，请完整填写必要信息！", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(int i, Header[] headers, String s) {
                                ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                                if (jsonByGson.getValue("msg").equals("Success")) {
                                    setRegisterSuccessInfoByJson(studentInfoJson);
                                    registerLayout.setVisibility(View.GONE);
                                    registerSuccessLayout.setVisibility(View.VISIBLE);
                                } else
                                    Toast.makeText(StudentRegisterActivity.this, "未知错误，请重试！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else
                        Toast.makeText(StudentRegisterActivity.this, "请检查网络连接！", Toast.LENGTH_SHORT).show();
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
            registerObject.put("major_id", "");
            registerObject.put("class_id", "");
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
        roleTextView.setText("学生");
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
