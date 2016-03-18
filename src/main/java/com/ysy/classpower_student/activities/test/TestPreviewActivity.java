package com.ysy.classpower_student.activities.test;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ysy.classpower.R;
import com.ysy.classpower_student.activities.home.StudentHomeActivity;
import com.ysy.classpower_common.activities.TestDetailsActivity;
import com.ysy.classpower_utils.OwnApp;
import com.ysy.classpower_utils.json_processor.ReadJsonByGson;
import com.ysy.classpower_utils.swipe_back.SwipeBackActivity;

import java.util.Timer;
import java.util.TimerTask;

public class TestPreviewActivity extends SwipeBackActivity {

    private Button testBeginButton;
    private Timer timer = null;
    private TextView beginTimeContentTextView;
    private TextView endTimeContentTextView;
    private TextView testStateContentTextView;
    private TextView limitTimeContentTextView;
    private TextView testCountContentTextView;
    private TextView accuracyRateTextView;
    private TextView accuracyRateContentTextView;
    private View sixthDividerView;
    private Button checkAnswerButton;
    private Button testDetailsButton;
    private TextView additionalInfoContentTextView;

    private String beginsOn;
    private String expiresOn;
    private String state;

    public static int timeNumber = 180;
    public static boolean isOpenFromTestResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_test_preview);
        setupActionBar();

        timeNumber = 180;
        isOpenFromTestResult = false;

        testBeginButton = (Button) findViewById(R.id.test_begin_button);
        testBeginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer != null)
                    stopTime();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Intent intent = new Intent(TestPreviewActivity.this, TestRunningActivity.class);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(TestPreviewActivity.this, testBeginButton, "test_preview_running_transition");
                    startActivity(intent, options.toBundle());
                } else
                    startActivity(new Intent(TestPreviewActivity.this, TestRunningActivity.class));
                testBeginButton.setText("继续测试");
//              finish();
            }
        });

        checkAnswerButton = (Button) findViewById(R.id.check_answer_button);
        checkAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudentHomeActivity.isDirectlyCheckResult = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Intent intent = new Intent(TestPreviewActivity.this, TestResultActivity.class);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(TestPreviewActivity.this, checkAnswerButton, "test_preview_result_transition");
                    startActivity(intent, options.toBundle());
                } else
                    startActivity(new Intent(TestPreviewActivity.this, TestResultActivity.class));
//                finish();
            }
        });

        testDetailsButton = (Button) findViewById(R.id.end_test_button);
        testDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestPreviewActivity.this, TestDetailsActivity.class));
//                finish();
            }
        });

        beginTimeContentTextView = (TextView) findViewById(R.id.begin_time_content_text_view);
        endTimeContentTextView = (TextView) findViewById(R.id.end_time_content_text_view);
        testStateContentTextView = (TextView) findViewById(R.id.test_state_content_text_view);
        limitTimeContentTextView = (TextView) findViewById(R.id.limit_time_content_text_view);
        testCountContentTextView = (TextView) findViewById(R.id.test_count_content_text_view);
        accuracyRateTextView = (TextView) findViewById(R.id.accuracy_rate_text_view);
        accuracyRateContentTextView = (TextView) findViewById(R.id.accuracy_rate_content_text_view);
        sixthDividerView = findViewById(R.id.View6);
        additionalInfoContentTextView = (TextView) findViewById(R.id.additional_info_content_text_view);

        OwnApp ownApp = (OwnApp) getApplication();
        ReadJsonByGson jsonByGson = new ReadJsonByGson(ownApp.getTestPreviewInfo());
        beginsOn = jsonByGson.getArrayValue("test", "begins_on");
        expiresOn = jsonByGson.getArrayValue("test", "expires_on");
        if (!ownApp.getTestIsFinished())
            state = "已结束";
        else
            state = "未完成";
        if (ownApp.getTestIsFinished()) {
            testBeginButton.setVisibility(View.VISIBLE);
            checkAnswerButton.setVisibility(View.GONE);
            testDetailsButton.setVisibility(View.GONE);
            accuracyRateTextView.setVisibility(View.GONE);
            accuracyRateContentTextView.setVisibility(View.GONE);
            sixthDividerView.setVisibility(View.GONE);

            beginTimeContentTextView.setText(beginsOn);
            endTimeContentTextView.setText(expiresOn);
            testCountContentTextView.setText("2");
            testStateContentTextView.setText(state);
            limitTimeContentTextView.setText("3分钟");
            additionalInfoContentTextView.setText("测试");
        } else {
            testBeginButton.setVisibility(View.GONE);
            checkAnswerButton.setVisibility(View.VISIBLE);
            testDetailsButton.setVisibility(View.VISIBLE);
            accuracyRateTextView.setVisibility(View.VISIBLE);
            accuracyRateContentTextView.setVisibility(View.VISIBLE);
            sixthDividerView.setVisibility(View.VISIBLE);

            beginTimeContentTextView.setText(beginsOn);
            endTimeContentTextView.setText(expiresOn);
            testCountContentTextView.setText("2");
            accuracyRateContentTextView.setText("1/2");
            testStateContentTextView.setText(state);
            limitTimeContentTextView.setText("无");
            additionalInfoContentTextView.setText("无");
        }
//        else if (StudentHomeActivity.STUDENT_TEST_STATE.equals("WILL")) {
//            testBeginButton.setVisibility(View.GONE);
//            checkAnswerButton.setVisibility(View.GONE);
//            testDetailsButton.setVisibility(View.GONE);
//            accuracyRateTextView.setVisibility(View.GONE);
//            accuracyRateContentTextView.setVisibility(View.GONE);
//            sixthDividerView.setVisibility(View.GONE);
//
//            beginTimeContentTextView.setText("2015-10-15 13:41:26");
//            endTimeContentTextView.setText("2016-09-15 16:42:11");
//            testCountContentTextView.setText("2");
//            testStateContentTextView.setText("未开始");
//            limitTimeContentTextView.setText("3分钟");
//            additionalInfoContentTextView.setText("测试");
//        }

    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            testBeginButton.setText("继续测试" + "（" + msg.arg1 + "秒）");
            startTime();
        }
    };

    private void startTime() {
        timer = new Timer();
        setSwipeBackEnable(false);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                timeNumber--;
                Message message = mHandler.obtainMessage();
                message.arg1 = timeNumber;
                mHandler.sendMessage(message);
            }
        };
        timer.schedule(task, 1000);
        if (timeNumber <= 0) { //由于有mHandler，所以此处可以动态判断
            stopTime();
            timer = null;
            testBeginButton.setVisibility(View.GONE);
            checkAnswerButton.setVisibility(View.VISIBLE);
            testDetailsButton.setVisibility(View.VISIBLE);
            accuracyRateTextView.setVisibility(View.VISIBLE);
            accuracyRateContentTextView.setVisibility(View.VISIBLE);
            sixthDividerView.setVisibility(View.VISIBLE);

            beginTimeContentTextView.setText(beginsOn);
            endTimeContentTextView.setText(expiresOn);
            testCountContentTextView.setText("2");
            accuracyRateContentTextView.setText("1/2");
            testStateContentTextView.setText(state);
            limitTimeContentTextView.setText("无");
            additionalInfoContentTextView.setText("无");

            Toast.makeText(this, "时间到！系统已为你自动提交答案。", Toast.LENGTH_SHORT).show();
            // 后台提交数据
        }
    }

    private void stopTime() {
        timer.cancel();
        setSwipeBackEnable(true);
    }

    @Override
    protected void onResume() {
        Log.d("Time_Number", timeNumber + "");
        if (timeNumber < 180 && timeNumber > 0) {
            if (!isOpenFromTestResult)
                startTime();
        }
        if (timeNumber <= 0 || isOpenFromTestResult) {
            timer = null;
            setSwipeBackEnable(true);
            testBeginButton.setVisibility(View.GONE);
            checkAnswerButton.setVisibility(View.VISIBLE);
            testDetailsButton.setVisibility(View.VISIBLE);
            accuracyRateTextView.setVisibility(View.VISIBLE);
            accuracyRateContentTextView.setVisibility(View.VISIBLE);
            sixthDividerView.setVisibility(View.VISIBLE);

            beginTimeContentTextView.setText(beginsOn);
            endTimeContentTextView.setText(expiresOn);
            testCountContentTextView.setText("2");
            accuracyRateContentTextView.setText("1/2");
            testStateContentTextView.setText(state);
            limitTimeContentTextView.setText("无");
            additionalInfoContentTextView.setText("无");
        }
        super.onResume();
    }

    @Override
    public void finish() {
        if (timer != null)
            stopTime();
        super.finish();
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
            if (timer == null)
                scrollToFinishActivity();
            else
                exitTips();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (timer == null)
            scrollToFinishActivity();
        else
            exitTips();
    }

    private void exitTips() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("提示").setMessage("离开后将不能再继续测试，确定要返回吗？").setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopTime();
//                        finish();
                        scrollToFinishActivity();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
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

}
