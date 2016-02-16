package com.ysy.classpower_student.activities.test;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ysy.classpower.R;
import com.ysy.classpower_student.activities.home.StudentHomeActivity;
import com.ysy.classpower_utils.ReadJsonByGson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        TextView correctAnswerTextView = (TextView) findViewById(R.id.correct_answer_text_view);
        TextView studentsAnswerTextView = (TextView) findViewById(R.id.students_answer_text_view);

        String correct_answer_str = "";
        for (int i = 0; i < 2; ++i) {
            correct_answer_str += ReadCorrectAnswerFromJSON("computer_network_" + (i + 1) + ".json");
        }
        if (!StudentHomeActivity.isDirectlyCheckResult) {
            correctAnswerTextView.setText(correct_answer_str);
        } else if (StudentHomeActivity.isDirectlyCheckResult) {

        }


        //获取TestRunning的全局静态变量，对应设置不同的答案
        String students_answer_str = "";
        if (!StudentHomeActivity.isDirectlyCheckResult) {
            for (int i = 0; i < 2; i++) {
                students_answer_str += TestRunningActivity.ANSWER[i];
            }
            studentsAnswerTextView.setText(students_answer_str);
        } else if (StudentHomeActivity.isDirectlyCheckResult) {

        }

    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private String ReadCorrectAnswerFromJSON(String json_name) {
        //从JSON获取正确答案
        InputStreamReader json = new InputStreamReader(getClass().getResourceAsStream("/assets/" + json_name));
        ReadJsonByGson readJsonByGson = new ReadJsonByGson(json);
        String correct_answer = readJsonByGson.getValue("answer");
        return correct_answer;
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