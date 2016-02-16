package com.ysy.classpower_student.activities.test;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ysy.classpower.R;
import com.ysy.classpower_student.activities.home.StudentHomeActivity;
import com.ysy.classpower_common.activities.TestDetailsActivity;

public class TestPreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_test_preview);
        setupActionBar();

        Button testBeginButton = (Button) findViewById(R.id.test_begin_button);
        testBeginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestPreviewActivity.this, TestRunningActivity.class));
                finish();
            }
        });

        Button checkAnswerButton = (Button) findViewById(R.id.check_answer_button);
        checkAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudentHomeActivity.isDirectlyCheckResult = true;
                startActivity(new Intent(TestPreviewActivity.this, TestResultActivity.class));
                finish();
            }
        });

        Button testDetailsButton = (Button) findViewById(R.id.end_test_button);
        testDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestPreviewActivity.this, TestDetailsActivity.class));
                finish();
            }
        });

        TextView beginTimeContentTextView = (TextView) findViewById(R.id.begin_time_content_text_view);
        TextView endTimeContentTextView = (TextView) findViewById(R.id.end_time_content_text_view);
        TextView testStateContentTextView = (TextView) findViewById(R.id.test_state_content_text_view);
        TextView limitTimeContentTextView = (TextView) findViewById(R.id.limit_time_content_text_view);
        TextView testCountContentTextView = (TextView) findViewById(R.id.test_count_content_text_view);
        TextView accuracyRateTextView = (TextView) findViewById(R.id.accuracy_rate_text_view);
        TextView accuracyRateContentTextView = (TextView) findViewById(R.id.accuracy_rate_content_text_view);
        View sixthDividerView = findViewById(R.id.View6);
        TextView additionalInfoContentTextView = (TextView) findViewById(R.id.additional_info_content_text_view);

        if (StudentHomeActivity.STUDENT_TEST_STATE.equals("DOING")) {
            testBeginButton.setVisibility(View.VISIBLE);
            checkAnswerButton.setVisibility(View.GONE);
            testDetailsButton.setVisibility(View.GONE);
            accuracyRateTextView.setVisibility(View.GONE);
            accuracyRateContentTextView.setVisibility(View.GONE);
            sixthDividerView.setVisibility(View.GONE);

            beginTimeContentTextView.setText("2015-10-15 13:41:26");
            endTimeContentTextView.setText("2016-09-15 16:42:11");
            testCountContentTextView.setText("2");
            testStateContentTextView.setText("未完成");
            limitTimeContentTextView.setText("3分钟");
            additionalInfoContentTextView.setText("测试");
        } else if (StudentHomeActivity.STUDENT_TEST_STATE.equals("DONE")) {
            testBeginButton.setVisibility(View.GONE);
            checkAnswerButton.setVisibility(View.VISIBLE);
            testDetailsButton.setVisibility(View.VISIBLE);
            accuracyRateTextView.setVisibility(View.VISIBLE);
            accuracyRateContentTextView.setVisibility(View.VISIBLE);
            sixthDividerView.setVisibility(View.VISIBLE);

            beginTimeContentTextView.setText("2015-10-15 13:41:26");
            endTimeContentTextView.setText("2016-09-15 16:42:11");
            testCountContentTextView.setText("2");
            accuracyRateContentTextView.setText("1/2");
            testStateContentTextView.setText("已结束");
            limitTimeContentTextView.setText("无");
            additionalInfoContentTextView.setText("无");
        } else if (StudentHomeActivity.STUDENT_TEST_STATE.equals("WILL")) {
            testBeginButton.setVisibility(View.GONE);
            checkAnswerButton.setVisibility(View.GONE);
            testDetailsButton.setVisibility(View.GONE);
            accuracyRateTextView.setVisibility(View.GONE);
            accuracyRateContentTextView.setVisibility(View.GONE);
            sixthDividerView.setVisibility(View.GONE);

            beginTimeContentTextView.setText("2015-10-15 13:41:26");
            endTimeContentTextView.setText("2016-09-15 16:42:11");
            testCountContentTextView.setText("2");
            testStateContentTextView.setText("未开始");
            limitTimeContentTextView.setText("3分钟");
            additionalInfoContentTextView.setText("测试");
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
