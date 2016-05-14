package com.ysy.classpower_student.activities.test;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.ysy.classpower.R;
import com.ysy.classpower_common.constant.ServerUrlConstant;
import com.ysy.classpower_student.activities.home.StudentHomeActivity;
import com.ysy.classpower_utils.DestroyAllActivities;
import com.ysy.classpower_utils.OwnApp;
import com.ysy.classpower_utils.json_processor.PostJsonAndGetCallback;
import com.ysy.classpower_utils.json_processor.ReadJsonByGson;
import com.ysy.classpower_utils.swipe_back.SwipeBackActivity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.util.Arrays;

public class TestResultActivity extends SwipeBackActivity {

    private String courseId = "";
    private String subId = "";
    private String token = "";
    private String testId = "";
    private OwnApp ownApp;
    private ProgressDialog waitDialog;
    private TextView correctAnswerTextView;
    private TextView studentsAnswerTextView;
    private TextView detailsAnswerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);
        DestroyAllActivities.getInstance().addActivity(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();

        ownApp = (OwnApp) getApplication();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        correctAnswerTextView = (TextView) findViewById(R.id.correct_answer_text_view);
        studentsAnswerTextView = (TextView) findViewById(R.id.students_answer_text_view);
        detailsAnswerTextView = (TextView) findViewById(R.id.test_result_details_answer_tv);

        SharedPreferences token_sp = getSharedPreferences("token", MODE_PRIVATE);
        SharedPreferences courseId_sp = getSharedPreferences("courseId", MODE_PRIVATE);
        SharedPreferences subId_sp = getSharedPreferences("subId", MODE_PRIVATE);
        token = token_sp.getString("token", "");
        courseId = courseId_sp.getString("courseId", "");
        subId = subId_sp.getString("subId", "");
        testId = new ReadJsonByGson(ownApp.getTestPreviewInfo()).getValue("test_id");

        JSONObject object = new JSONObject();
        try {
            object.put("test_id", testId);
            object.put("token", token); // new
            object.put("course_id", courseId); // new
            object.put("sub_id", subId); // new
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), ServerUrlConstant.getCourseQuestionGetallquestionsforresultUrl(ownApp.getURL_FIGURE()), object.toString(), new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                waitDialog = ProgressDialog.show(TestResultActivity.this, "正在获取信息", "请稍等…");
                super.onStart();
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                waitDialog.dismiss();
                Toast.makeText(getApplicationContext(), "网络错误，请重试！", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                detailsAnswerTextView.setText("");
                waitDialog.dismiss();
                ReadJsonByGson readJsonByGson = new ReadJsonByGson(s);
                String[] content = readJsonByGson.getQuestionsInTest("content");
                String[] type = readJsonByGson.getQuestionsInTest("type");
                for (int j = 0; j < content.length; ++j) {
                    if (type[j].equals("1")) { // 选择题
                        detailsAnswerTextView.append((j + 1) + "：" + content[j] + "\n");
                        String[] choices = readJsonByGson.getChoicesOfQuestionsInTest(j);
                        String[] answers = readJsonByGson.getAnswersOfQuestionsInTest(j);
                        detailsAnswerTextView.append("    " + choices[Integer.valueOf(answers[0]) - 1] + "\n");
                        if (j == content.length - 1)
                            correctAnswerTextView.append(NumToChar(answers[0]));
                        else
                            correctAnswerTextView.append(NumToChar(answers[0]) + "\n");
                    } else if (type[j].equals("2")) { // 填空题，此处不确定，可能填空题不存在choices
                        String[] answers = readJsonByGson.getAnswersOfQuestionsInTest(j);
                        detailsAnswerTextView.append((j + 1) + "：" + content[j] + "\n");
                        detailsAnswerTextView.append("    " + Arrays.toString(answers) + "\n");
                        if (j == content.length - 1)
                            correctAnswerTextView.append(Arrays.toString(answers));
                        else
                            correctAnswerTextView.append(Arrays.toString(answers) + "\n");
                    }
                }
            }
        });

//        String correct_answer_str = "";
//        for (int i = 0; i < 2; ++i) {
////            correct_answer_str += ReadCorrectAnswerFromJSON("computer_network_" + (i + 1) + ".json");
//            correct_answer_str += "";
//        }
//        if (!StudentHomeActivity.isDirectlyCheckResult) {
//            correctAnswerTextView.setText(correct_answer_str);
//        } else if (StudentHomeActivity.isDirectlyCheckResult) {
//
//        }
//
//
//        //获取TestRunning的全局静态变量，对应设置不同的答案
//        String students_answer_str = "";
//        if (!StudentHomeActivity.isDirectlyCheckResult) {
//            for (int i = 0; i < 2; i++) {
////                students_answer_str += TestRunningActivity.ANSWER[i];
//            }
//            studentsAnswerTextView.setText(students_answer_str);
//        } else if (StudentHomeActivity.isDirectlyCheckResult) {
//
//        }

    }

    private String NumToChar(String num) {
        if (num.equals("1"))
            return "A";
        else if (num.equals("2"))
            return "B";
        else if (num.equals("3"))
            return "C";
        else if (num.equals("4"))
            return "D";
        else if (num.equals("5"))
            return "E";
        else if (num.equals("6"))
            return "F";
        else if (num.equals("7"))
            return "G";
        else if (num.equals("8"))
            return "H";
        else
            return "_";
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

//    private String ReadCorrectAnswerFromJSON(String json_name) {
//        //从JSON获取正确答案
//        InputStreamReader json = new InputStreamReader(getClass().getResourceAsStream("/assets/" + json_name));
//        ReadJsonByGson readJsonByGson = new ReadJsonByGson(json);
//        String correct_answer = readJsonByGson.getValue("answer");
//        return correct_answer;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //覆盖整个Activity的返回按钮
        int id = item.getItemId();
        if (id == android.R.id.home) {
            scrollToFinishActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        scrollToFinishActivity();
    }

    @Override
    public void finish() {
        TestPreviewActivity.isOpenFromTestResult = true;
        super.finish();
    }
}
