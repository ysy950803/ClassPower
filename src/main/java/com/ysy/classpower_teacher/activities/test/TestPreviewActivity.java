package com.ysy.classpower_teacher.activities.test;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ysy.classpower.R;
import com.ysy.classpower_common.activities.TestDetailsActivity;
import com.ysy.classpower_teacher.activities.home.TeacherHomeActivity;

public class TestPreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_test_preview);
        setupActionBar();

        Button testDetailsButton = (Button) findViewById(R.id.test_details_button);
        Button changeTestButton = (Button) findViewById(R.id.change_test_button);
        Button endTestButton = (Button) findViewById(R.id.end_test_button);

        testDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestPreviewActivity.this, TestDetailsActivity.class));
                finish();
            }
        });
        endTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TeacherHomeActivity.TEACHER_TEST_STATE.equals("DONE")) {
                    startActivity(new Intent(TestPreviewActivity.this, TestDetailsActivity.class));
                    finish();
                }
            }
        });

        TextView beginTimeContentTextView = (TextView) findViewById(R.id.begin_time_content_text_view);
        TextView endTimeContentTextView = (TextView) findViewById(R.id.end_time_content_text_view);
        TextView testStateContentTextView = (TextView) findViewById(R.id.test_state_content_text_view);
        TextView limitTimeContentTextView = (TextView) findViewById(R.id.limit_time_content_text_view);
        TextView testCountContentTextView = (TextView) findViewById(R.id.test_count_content_text_view);
        TextView additionalInfoContentTextView = (TextView) findViewById(R.id.additional_info_content_text_view);

        if (TeacherHomeActivity.TEACHER_TEST_STATE.equals("DOING")) {
            beginTimeContentTextView.setText("2015-10-15 13:41:26");
            endTimeContentTextView.setText("2016-09-15 16:42:11");
            testCountContentTextView.setText("2");
            testStateContentTextView.setText("16/128已提交");
            limitTimeContentTextView.setText("3分钟");
            additionalInfoContentTextView.setText("测试");

            testDetailsButton.setVisibility(View.VISIBLE);
            changeTestButton.setVisibility(View.VISIBLE);
            endTestButton.setVisibility(View.VISIBLE);
        } else if (TeacherHomeActivity.TEACHER_TEST_STATE.equals("WILL")) {
            beginTimeContentTextView.setText("2015-10-15 13:41:26");
            endTimeContentTextView.setText("2016-09-15 16:42:11");
            testCountContentTextView.setText("2");
            testStateContentTextView.setText("未开始");
            limitTimeContentTextView.setText("3分钟");
            additionalInfoContentTextView.setText("测试");

            testDetailsButton.setVisibility(View.GONE);
            changeTestButton.setVisibility(View.VISIBLE);
            endTestButton.setVisibility(View.VISIBLE);
            endTestButton.setText("发布测试");
            endTestButton.setTextColor(Color.BLACK);
        } else if (TeacherHomeActivity.TEACHER_TEST_STATE.equals("DONE")) {
            beginTimeContentTextView.setText("2015-10-15 13:41:26");
            endTimeContentTextView.setText("2016-09-15 16:42:11");
            testCountContentTextView.setText("2");
            testStateContentTextView.setText("已结束");
            limitTimeContentTextView.setText("3分钟");
            additionalInfoContentTextView.setText("测试");

            testDetailsButton.setVisibility(View.GONE);
            changeTestButton.setVisibility(View.GONE);
            endTestButton.setVisibility(View.VISIBLE);
            endTestButton.setText("测试详情");
            endTestButton.setTextColor(Color.BLACK);
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
