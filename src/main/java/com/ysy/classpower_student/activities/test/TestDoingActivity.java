package com.ysy.classpower_student.activities.test;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.ysy.classpower.R;
import com.ysy.classpower_common.constant.ServerUrlConstant;
import com.ysy.classpower_student.activities.home.StudentHomeActivity;
import com.ysy.classpower_student.adapters.TestDoingFragmentAdapter;
import com.ysy.classpower_student.adapters.TestDoingSpinnerAdapter;
import com.ysy.classpower_student.fragments.TestDoingTypeOneFragment;
import com.ysy.classpower_utils.OwnApp;
import com.ysy.classpower_utils.json_processor.PostJsonAndGetCallback;
import com.ysy.classpower_utils.json_processor.ReadJsonByGson;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TestDoingActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private Spinner spinner;
    private TextView timerTextView;
    private Button handAnswerButton;
    private boolean isTestTimeOut = false;
    private Timer timer = null;
    private int timeNumber2 = TestPreviewActivity.timeNumber;
    private boolean isHelpsCardOpen = false;
    private ProgressDialog waitDialog;

    private OwnApp ownApp;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_doing);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();

        final CardView helpsCardView = (CardView) findViewById(R.id.test_running_helps_cardView);
        final FloatingActionButton helpsFab = (FloatingActionButton) findViewById(R.id.test_running_helps_fab);
        assert helpsFab != null;
        helpsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                helpTips();
                assert helpsCardView != null;
                if (!isHelpsCardOpen) {
                    isHelpsCardOpen = true;
                    helpsCardView.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Animator animator = ViewAnimationUtils.createCircularReveal(
                                helpsCardView,
                                helpsCardView.getWidth(),
                                helpsCardView.getHeight(),
                                0,
                                (float) Math.hypot(helpsCardView.getWidth(), helpsCardView.getHeight()));
                        animator.setInterpolator(new AccelerateDecelerateInterpolator());
                        animator.setDuration(1000);
                        animator.start();
                    }
                } else {
                    isHelpsCardOpen = false;
                    helpsCardView.setVisibility(View.GONE);
                }
            }
        });

        //提交答案
        handAnswerButton = (Button) findViewById(R.id.hand_answer_button);
        handAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int unfinished_answer_num = 0;
                for (int i = 0; i < ownApp.getQuestionStates().length; ++i) {
                    if (ownApp.getQuestionStates(i) == null) {
                        ++unfinished_answer_num;
                    }
                }
                if (!isTestTimeOut)
                    confirmHandAnswer(ownApp.getQuestionStates().length - unfinished_answer_num, ownApp.getQuestionStates().length);
                else {
                    startActivity(new Intent(TestDoingActivity.this, TestResultActivity.class));
                    finish();
                }
                StudentHomeActivity.isDirectlyCheckResult = false;
            }
        });

        timerTextView = (TextView) findViewById(R.id.timer_text_view);

        mViewPager = (ViewPager) findViewById(R.id.container);
        spinner = (Spinner) findViewById(R.id.spinner);

        ownApp = (OwnApp) getApplication();
        ReadJsonByGson jsonByGson = new ReadJsonByGson(ownApp.getTestPreviewInfo());
        JSONObject object = new JSONObject();
        try {
            object.put("test_id", jsonByGson.getValue("test_id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), ServerUrlConstant.getCourseTestGetquestionsintestUrl(ownApp.getURL_FIGURE()), object.toString(), new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                waitDialog = ProgressDialog.show(TestDoingActivity.this, "正在获取题目", "请稍等…");
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
                waitDialog.dismiss();
                startTime(); // 进入即启动倒计时
                List<Fragment> fragments = new ArrayList<>();
                ReadJsonByGson readJsonByGson = new ReadJsonByGson(s);
                String[] question_type = readJsonByGson.getQuestionsInTest("type");
                String[] question_content = readJsonByGson.getQuestionsInTest("content");
                String[] question_difficulty = readJsonByGson.getQuestionsInTest("difficulty");
                String[] questionArray;
                if (ownApp.getQuestionStates() == null) {
                    ownApp.setQuestionStatesSize(question_type.length);
                    questionArray = new String[question_type.length];
                    for (int k = 0; k < question_type.length; ++k) {
                        questionArray[k] = "题目" + (k + 1) + "(未完成)";
                    }
                } else {
                    questionArray = new String[ownApp.getQuestionStates().length];
                    for (int k = 0; k < ownApp.getQuestionStates().length; ++k) {
                        if (ownApp.getQuestionStates(k) == null) {
                            questionArray[k] = "题目" + (k + 1) + "(未完成)";
                        } else
                            questionArray[k] = "题目" + (k + 1) + "(已完成)";
                    }
                }

                assert toolbar != null;
                spinner.setAdapter(new TestDoingSpinnerAdapter(toolbar.getContext(), questionArray));
//                ownApp.setQuestionInfoSize(question_type.length);

                for (int j = 0; j < question_type.length; ++j) {
                    Bundle data = new Bundle();
                    data.putString("question_content", question_content[j]);
                    data.putString("question_difficulty", question_difficulty[j]);
                    data.putInt("question_position", j);
                    if (question_type[j].equals("1")) { // 单选题
                        String[] question_choices = readJsonByGson.getChoicesOfQuestionsInTest(j);
                        data.putStringArray("question_choices", question_choices);
                        Fragment type_one_fragment = new TestDoingTypeOneFragment();
                        type_one_fragment.setArguments(data);
                        fragments.add(type_one_fragment);
                    } else if (question_type[j].equals("2")) {
//                        Fragment list_fragment = new TestDoingTypeOneFragment();
//                        list_fragment.setArguments(data);
//                        fragments.add(list_fragment);
                    }
                }
                TestDoingFragmentAdapter fragmentAdapter = new TestDoingFragmentAdapter(getSupportFragmentManager(), fragments);
                mViewPager.setAdapter(fragmentAdapter);

            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                Toast.makeText(TestDoingActivity.this, "onPageSelected:" + position, Toast.LENGTH_SHORT).show();
                spinner.setSelection(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(TestDoingActivity.this, "onItemSelected:" + position, Toast.LENGTH_SHORT).show();
                mViewPager.setCurrentItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void setSpinnerAdapter(String[] questionArray, int position) {
        spinner.setAdapter(new TestDoingSpinnerAdapter(toolbar.getContext(), questionArray));
        spinner.setSelection(position);
        // 此处不应调用setCurrentItem，因为setSelection会call出viewPager的滚动，否则出现“Recursive entry to executePendingTransactions”错误
//        mViewPager.setCurrentItem(position);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitTips();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exitTips() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("提示").setMessage("离开后计时不会暂停，确定要返回吗？").setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopTime();
                        finish();
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

    private void confirmHandAnswer(int DONE_ANSWER_COUNT, int TEST_ARRAY_LENGTH) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("即将提交答案").setMessage("当前完成度为 " + DONE_ANSWER_COUNT + "/" + TEST_ARRAY_LENGTH + " ，确定要提交吗？").setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(TestDoingActivity.this, TestResultActivity.class));
                        Toast.makeText(TestDoingActivity.this, "提交成功！", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        final android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();

        final Timer alertLife = new Timer();
        alertLife.schedule(new TimerTask() {
            public void run() {
                alert.dismiss(); // when the task active then close the dialog
                alertLife.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
            }
        }, TestPreviewActivity.timeNumber * 1000); // 时间到时自动提交答案并且关闭Dialog（如果你此时打开着）

        //设置透明度
        Window window = alert.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.75f;
        window.setAttributes(lp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test_doing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        if (id == android.R.id.home) {
            exitTips();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            timerTextView.setText(msg.arg1 + "");
            startTime();
        }
    };

    private void startTime() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                timeNumber2--;
                Message message = mHandler.obtainMessage();
                message.arg1 = timeNumber2;
                TestPreviewActivity.timeNumber = timeNumber2;
                mHandler.sendMessage(message);
            }
        };
        timer.schedule(task, 1000);
        if (timeNumber2 <= 0) { //由于有mHandler，所以此处可以动态判断
            stopTime();
            isTestTimeOut = true;
            Toast.makeText(this, "时间到！系统已为你自动提交答案。", Toast.LENGTH_SHORT).show();
            // 后台提交数据
            handAnswerButton.setText("查看结果");
            handAnswerButton.setTextColor(Color.WHITE);
        }
    }

    private void stopTime() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void finish() {
        stopTime(); // 关闭计时器，否则退出此Activity后仍出现“时间到”的Toast
        super.finish();
    }

}
