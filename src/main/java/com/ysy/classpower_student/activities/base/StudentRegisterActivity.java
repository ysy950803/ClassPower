package com.ysy.classpower_student.activities.base;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBar;
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

import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.ysy.classpower.R;
import com.ysy.classpower_common.constant.ErrorCodeConstant;
import com.ysy.classpower_common.constant.ServerUrlConstant;
import com.ysy.classpower_utils.ConnectionDetector;
import com.ysy.classpower_utils.DestroyAllActivities;
import com.ysy.classpower_utils.OwnApp;
import com.ysy.classpower_utils.json_processor.PostJsonAndGetCallback;
import com.ysy.classpower_utils.json_processor.ReadJsonByGson;
import com.ysy.classpower_utils.swipe_back.SwipeBackActivity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudentRegisterActivity extends SwipeBackActivity {

    private EditText userIdEditText;
    private EditText passwordEditText;
    private EditText doublePasswordEditText;
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
    private TextView classTextView;
    private TextView roleTextView;

    private RelativeLayout registerLayout;
    private RelativeLayout registerSuccessLayout;

    //    private TextView schoolsTextView;
    private TextView majorsTextView;
    private TextView classesTextView;
    private Spinner schoolsSpinner;
    private Spinner majorsSpinner;
    private Spinner classesSpinner;
    private List<String> schools_list = new ArrayList<>();
    private List<String> majors_list = new ArrayList<>();
    private List<String> classes_list = new ArrayList<>();
    private String schoolId = "";
    private String majorId = "";
    private String classId = "";
    private String className = "";

    private boolean isMale;
    private JsonObject registerObject;

    private OwnApp ownApp;

    private ProgressDialog waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);
        setupActionBar();
        DestroyAllActivities.getInstance().addActivity(this);

        ownApp = (OwnApp) getApplication();

        registerObject = new JsonObject();

        registerLayout = (RelativeLayout) findViewById(R.id.register_layout);
        registerSuccessLayout = (RelativeLayout) findViewById(R.id.register_success_layout);

        userIdEditText = (EditText) findViewById(R.id.student_register_userId_editText);
        passwordEditText = (EditText) findViewById(R.id.student_register_password_editText);
        doublePasswordEditText = (EditText) findViewById(R.id.student_register_double_password_editText);
        nameEditText = (EditText) findViewById(R.id.student_register_name_editText);
        emailEditText = (EditText) findViewById(R.id.student_register_email_editText);
        telEditText = (EditText) findViewById(R.id.student_register_tel_editText);
        maleRadioButton = (RadioButton) findViewById(R.id.male_radio_button);
        femaleRadioButton = (RadioButton) findViewById(R.id.female_radio_button);
        handleRegisterButton = (Button) findViewById(R.id.handle_register_button);

        userIdTextView = (TextView) findViewById(R.id.user_id_text_view);
        nameTextView = (TextView) findViewById(R.id.name_text_view);
        sexTextView = (TextView) findViewById(R.id.sex_text_view);
        classTextView = (TextView) findViewById(R.id.class_text_view);
        roleTextView = (TextView) findViewById(R.id.role_text_view);
        backToLoginButton = (Button) findViewById(R.id.back_to_login_button);

//        schoolsTextView = (TextView) findViewById(R.id.schools_textView);
        majorsTextView = (TextView) findViewById(R.id.majors_textView);
        classesTextView = (TextView) findViewById(R.id.classes_textView);
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

        schools_list.add(0, "<---请选择学院--->");
        majors_list.add(0, "<---请选择专业--->");
        classes_list.add(0, "<---请选择班级--->");
        spinnerAdapter(schools_list, schoolsSpinner);
        spinnerAdapter(majors_list, majorsSpinner);
        spinnerAdapter(classes_list, classesSpinner);

        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        if (cd.isConnectingToInternet()) {
            new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), ServerUrlConstant.getUserRegisterGetschoolsUrl(ownApp.getURL_FIGURE()), "{}", new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    waitDialog = ProgressDialog.show(StudentRegisterActivity.this, "正在连接", "请稍等…");
                    super.onStart();
                }

                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    waitDialog.dismiss();
                    if (i == 0)
                        Toast.makeText(StudentRegisterActivity.this, "服务器未响应，无法获取学院，请退出重试！", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    waitDialog.dismiss();
                    ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                    String school_name_array[] = jsonByGson.getSchoolsInfo("school_name");
                    final String school_id_array[] = jsonByGson.getSchoolsInfo("school_id");
                    schools_list.clear();
                    schools_list.add(0, "<---请选择学院--->");
                    Collections.addAll(schools_list, school_name_array);
                    spinnerAdapter(schools_list, schoolsSpinner);
                    schoolsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position == 0) {
                                schoolId = "";
                                majorsTextView.setVisibility(View.INVISIBLE);
                                majorsSpinner.setVisibility(View.INVISIBLE);
                                classesTextView.setVisibility(View.INVISIBLE);
                                classesSpinner.setVisibility(View.INVISIBLE);
                            } else if (position > 0) {
                                majorsTextView.setVisibility(View.VISIBLE);
                                majorsSpinner.setVisibility(View.VISIBLE);
                                classesTextView.setVisibility(View.INVISIBLE);
                                classesSpinner.setVisibility(View.INVISIBLE);
                                schoolId = school_id_array[position - 1];
                                getMajorsToSpinner(schoolId);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            });

        } else
            Toast.makeText(StudentRegisterActivity.this, "网络未连接，获取学院失败，请退出重试！", Toast.LENGTH_SHORT).show();

    }

    private void spinnerAdapter(List<String> list, Spinner spinner) {
        ArrayAdapter<String> schoolsSpinnerAdapter = new ArrayAdapter<>(StudentRegisterActivity.this, android.R.layout.simple_spinner_item, list);
        schoolsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(schoolsSpinnerAdapter);
    }

    private void getMajorsToSpinner(String schoolId) {
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        if (cd.isConnectingToInternet()) {
            JSONObject object = new JSONObject();
            try {
                object.put("school_id", schoolId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String json = object.toString();
            new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), ServerUrlConstant.getUserRegisterGetmajorsUrl(ownApp.getURL_FIGURE()), json, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    waitDialog = ProgressDialog.show(StudentRegisterActivity.this, "正在获取专业列表", "请稍等…");
                    super.onStart();
                }

                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    schoolsSpinner.setSelection(0);
                    waitDialog.dismiss();
                    if (i == 0) {
                        Toast.makeText(StudentRegisterActivity.this, "服务器未响应，无法获取专业，请重选学院！", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    waitDialog.dismiss();
                    ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                    String major_name_array[] = jsonByGson.getMajorsInfo("major_name");
                    final String major_id_array[] = jsonByGson.getMajorsInfo("major_id");
                    majors_list.clear();
                    majors_list.add(0, "<---请选择专业--->");
                    Collections.addAll(majors_list, major_name_array);
                    spinnerAdapter(majors_list, majorsSpinner);
                    majorsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position == 0) {
                                majorId = "";
                                classesSpinner.setVisibility(View.INVISIBLE);
                                classesTextView.setVisibility(View.INVISIBLE);
                            } else if (position > 0) {
                                majorId = major_id_array[position - 1];
                                classesSpinner.setVisibility(View.VISIBLE);
                                classesTextView.setVisibility(View.VISIBLE);
                                getClassesToSpinner(majorId);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            });
        } else
            Toast.makeText(StudentRegisterActivity.this, "网络未连接，获取专业失败，请重选学院！", Toast.LENGTH_SHORT).show();

    }

    private void getClassesToSpinner(String majorId) {
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        if (cd.isConnectingToInternet()) {
            JSONObject object = new JSONObject();
            try {
                object.put("major_id", majorId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String json = object.toString();
            new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), ServerUrlConstant.getUserRegisterGetclassesUrl(ownApp.getURL_FIGURE()), json, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    waitDialog = ProgressDialog.show(StudentRegisterActivity.this, "正在获取班级列表", "请稍等…");
                    super.onStart();
                }

                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    waitDialog.dismiss();
                    majorsSpinner.setSelection(0);
                    if (i == 0) {
                        Toast.makeText(StudentRegisterActivity.this, "服务器未响应，无法获取班级，请重选专业！", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    waitDialog.dismiss();
                    ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                    final String class_name_array[] = jsonByGson.getClassesInfo("class_name");
                    final String class_id_array[] = jsonByGson.getClassesInfo("class_id");
                    classes_list.clear();
                    classes_list.add(0, "<---请选择班级--->");
                    Collections.addAll(classes_list, class_name_array);
                    spinnerAdapter(classes_list, classesSpinner);
                    classesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position == 0) {
                                classId = "";
                            } else if (position > 0) {
                                classId = class_id_array[position - 1];
                                className = class_name_array[position - 1];
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            });
        } else
            Toast.makeText(StudentRegisterActivity.this, "网络未连接，获取班级失败，请重选专业！", Toast.LENGTH_SHORT).show();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.handle_register_button:
                    ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
                    if (cd.isConnectingToInternet()) {
                        if (registerInfoAsJson()) {
                            final String studentInfoJson = registerObject.toString();
                            new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), ServerUrlConstant.getUserRegisterStudentUrl(ownApp.getURL_FIGURE()), studentInfoJson, new TextHttpResponseHandler() {
                                @Override
                                public void onStart() {
                                    waitDialog = ProgressDialog.show(StudentRegisterActivity.this, "正在提交", "请稍等…");
                                    super.onStart();
                                }

                                @Override
                                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                                    waitDialog.dismiss();
                                    if (i == 0)
                                        Toast.makeText(StudentRegisterActivity.this, "服务器连接超时，请重试！", Toast.LENGTH_SHORT).show();
                                    else if (i == 400) {
                                        ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                                        if (s.contains("error_code")) {
                                            if (jsonByGson.getValue("error_code").equals(ErrorCodeConstant.USER_ALREADY_EXISTS))
                                                Toast.makeText(StudentRegisterActivity.this, "用户已存在，请重新注册！", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }

                                @Override
                                public void onSuccess(int i, Header[] headers, String s) {
                                    waitDialog.dismiss();
                                    ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                                    if (jsonByGson.getValue("msg").equals("Success")) {
                                        setRegisterSuccessInfoByJson(studentInfoJson);
                                        registerLayout.setVisibility(View.GONE);
                                        registerSuccessLayout.setVisibility(View.VISIBLE);
                                    } else
                                        Toast.makeText(StudentRegisterActivity.this, "未知错误，请重试！", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
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

    private boolean registerInfoAsJson() {
        if (userIdEditText.getText().toString().equals("")) {
            Toast.makeText(StudentRegisterActivity.this, "用户名不能为空！", Toast.LENGTH_SHORT).show();
            return false;
        } else if (passwordEditText.getText().toString().equals("")) {
            Toast.makeText(StudentRegisterActivity.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
            return false;
        } else if (passwordEditText.getText().toString().length() < 6) {
            Toast.makeText(StudentRegisterActivity.this, "密码太短啦，不安全哦！", Toast.LENGTH_LONG).show();
            return false;
        } else if (!passwordEditText.getText().toString().equals(doublePasswordEditText.getText().toString())) {
            Toast.makeText(StudentRegisterActivity.this, "两次密码输入不一致，请修改一下吧！", Toast.LENGTH_LONG).show();
            return false;
        } else if (nameEditText.getText().toString().equals("")) {
            Toast.makeText(StudentRegisterActivity.this, "姓名不能为空！", Toast.LENGTH_SHORT).show();
            return false;
        } else if (majorId.equals("") || classId.equals("")) {
            Toast.makeText(StudentRegisterActivity.this, "请选择完整的院校信息！", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            registerObject.addProperty("user_id", userIdEditText.getText().toString());
            registerObject.addProperty("password", passwordEditText.getText().toString());
            registerObject.addProperty("name", nameEditText.getText().toString());
            registerObject.addProperty("email", emailEditText.getText().toString());
            registerObject.addProperty("tel", telEditText.getText().toString());
            registerObject.addProperty("gender", isMale);
            registerObject.addProperty("major_id", majorId);
            registerObject.addProperty("class_id", classId);
            return true;
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
        classTextView.setText(className);
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
//            onBackPressed(); //调用onKeyDown内部方法
            scrollToFinishActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        scrollToFinishActivity();
    }

}
