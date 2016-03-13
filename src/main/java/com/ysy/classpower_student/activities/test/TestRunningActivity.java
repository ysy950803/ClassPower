package com.ysy.classpower_student.activities.test;

import android.animation.Animator;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ysy.classpower.R;
import com.ysy.classpower_student.activities.home.StudentHomeActivity;
import com.ysy.classpower_utils.json_processor.ReadJsonByGson;

import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

public class TestRunningActivity extends AppCompatActivity {

    private TextView timerTextView;
    private TextView testContentTextView;
    private TextView testATextView;
    private TextView testBTextView;
    private TextView testCTextView;
    private TextView testDTextView;
    private TextView testProgressContentTextView;
    private TextView testDifficultyContentTextView;
    private RadioButton radioButtonA;
    private RadioButton radioButtonB;
    private RadioButton radioButtonC;
    private RadioButton radioButtonD;
    private Button handAnswerButton;
    private Button nextItemButton;

    public static String[] ANSWER = new String[100];
    private static int POSITION = 0;
    public static int TEST_ARRAY_LENGTH = 0;
    private static int BLANK_ANSWER_COUNT = 0;
    private boolean isTestTimeOut = false;

    private Timer timer = null;
    private int timeNumber2 = TestPreviewActivity.timeNumber;

    private boolean isHelpsCardOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_running);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();

        timerTextView = (TextView) findViewById(R.id.timer_text_view);
        testContentTextView = (TextView) findViewById(R.id.test_content_text_view);
        testATextView = (TextView) findViewById(R.id.test_A_text_view);
        testBTextView = (TextView) findViewById(R.id.test_B_text_view);
        testCTextView = (TextView) findViewById(R.id.test_C_text_view);
        testDTextView = (TextView) findViewById(R.id.test_D_text_view);
        testProgressContentTextView = (TextView) findViewById(R.id.test_progress_content_text_view);
        testDifficultyContentTextView = (TextView) findViewById(R.id.test_difficulty_content_text_view);
        //答案选项按钮
        radioButtonA = (RadioButton) findViewById(R.id.radioButton_A);
        radioButtonB = (RadioButton) findViewById(R.id.radioButton_B);
        radioButtonC = (RadioButton) findViewById(R.id.radioButton_C);
        radioButtonD = (RadioButton) findViewById(R.id.radioButton_D);

        String testArray[] = { //待扩展为根据不同Test题目数量而变的数组
                "题 1",
                "题 2",
        };

        TEST_ARRAY_LENGTH = testArray.length;
        resetANSWERArrayValue();

        // Setup spinner
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(new MyAdapter(
                toolbar.getContext(),
//                new String[]{
//                        "题 1",
//                        "题 2",
//                }
                testArray
        ));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                POSITION = position;
                if (POSITION + 1 < TEST_ARRAY_LENGTH) {
                    nextItemButton.setClickable(true);
                    nextItemButton.setBackgroundResource(R.drawable.item_will_press);
                    nextItemButton.setTextColor(Color.BLACK);
                } else if (POSITION + 1 == TEST_ARRAY_LENGTH) {
                    nextItemButton.setClickable(false);
                    nextItemButton.setBackgroundColor(Color.GRAY);
                    nextItemButton.setTextColor(Color.WHITE);
                }
                String number = (position + 1) + "";
                readJSONandSetContent("computer_network_" + number + ".json", position + 1);
                switch (ANSWER[position]) {
                    case "A":
                        radioButtonA.setChecked(true);
                        radioButtonB.setChecked(false);
                        radioButtonC.setChecked(false);
                        radioButtonD.setChecked(false);
                        break;
                    case "B":
                        radioButtonB.setChecked(true);
                        radioButtonA.setChecked(false);
                        radioButtonC.setChecked(false);
                        radioButtonD.setChecked(false);
                        break;
                    case "C":
                        radioButtonC.setChecked(true);
                        radioButtonB.setChecked(false);
                        radioButtonA.setChecked(false);
                        radioButtonD.setChecked(false);
                        break;
                    case "D":
                        radioButtonD.setChecked(true);
                        radioButtonB.setChecked(false);
                        radioButtonC.setChecked(false);
                        radioButtonA.setChecked(false);
                        break;
                    default:
                        radioButtonD.setChecked(false);
                        radioButtonB.setChecked(false);
                        radioButtonC.setChecked(false);
                        radioButtonA.setChecked(false);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        nextItemButton = (Button) findViewById(R.id.next_item_button);
        nextItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++POSITION;
                if (POSITION + 1 == TEST_ARRAY_LENGTH) {
                    nextItemButton.setClickable(false);
                    nextItemButton.setBackgroundColor(Color.GRAY);
                    nextItemButton.setTextColor(Color.WHITE);
                } else if (POSITION + 1 < TEST_ARRAY_LENGTH) {
                    nextItemButton.setClickable(true);
                    nextItemButton.setBackgroundResource(R.drawable.item_will_press);
                    nextItemButton.setTextColor(Color.BLACK);
                }
                String number = (POSITION + 1) + "";
                readJSONandSetContent("computer_network_" + number + ".json", POSITION + 1);
                spinner.setSelection(POSITION);
                switch (ANSWER[POSITION]) {
                    case "A":
                        radioButtonA.setChecked(true);
                        radioButtonB.setChecked(false);
                        radioButtonC.setChecked(false);
                        radioButtonD.setChecked(false);
                        break;
                    case "B":
                        radioButtonB.setChecked(true);
                        radioButtonA.setChecked(false);
                        radioButtonC.setChecked(false);
                        radioButtonD.setChecked(false);
                        break;
                    case "C":
                        radioButtonC.setChecked(true);
                        radioButtonB.setChecked(false);
                        radioButtonA.setChecked(false);
                        radioButtonD.setChecked(false);
                        break;
                    case "D":
                        radioButtonD.setChecked(true);
                        radioButtonB.setChecked(false);
                        radioButtonC.setChecked(false);
                        radioButtonA.setChecked(false);
                        break;
                    default:
                        radioButtonD.setChecked(false);
                        radioButtonB.setChecked(false);
                        radioButtonC.setChecked(false);
                        radioButtonA.setChecked(false);
                        break;
                }
            }
        });

        final CardView helpsCardView = (CardView) findViewById(R.id.test_running_helps_cardView);
        final FloatingActionButton helpsFab = (FloatingActionButton) findViewById(R.id.test_running_helps_fab);
        helpsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                helpTips();
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

        //只能单选
        radioButtonA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButtonB.setChecked(false);
                radioButtonC.setChecked(false);
                radioButtonD.setChecked(false);
                for (int i = 0; i < TEST_ARRAY_LENGTH; i++) {
                    if (POSITION == i) {
                        ANSWER[i] = "A";
                    }
                }
            }
        });
        radioButtonB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButtonA.setChecked(false);
                radioButtonC.setChecked(false);
                radioButtonD.setChecked(false);
                for (int i = 0; i < TEST_ARRAY_LENGTH; i++) {
                    if (POSITION == i) {
                        ANSWER[i] = "B";
                    }
                }
            }
        });
        radioButtonC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButtonB.setChecked(false);
                radioButtonA.setChecked(false);
                radioButtonD.setChecked(false);
                for (int i = 0; i < TEST_ARRAY_LENGTH; i++) {
                    if (POSITION == i) {
                        ANSWER[i] = "C";
                    }
                }
            }
        });
        radioButtonD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButtonB.setChecked(false);
                radioButtonC.setChecked(false);
                radioButtonA.setChecked(false);
                for (int i = 0; i < TEST_ARRAY_LENGTH; i++) {
                    if (POSITION == i) {
                        ANSWER[i] = "D";
                    }
                }
            }
        });

        //提交答案
        handAnswerButton = (Button) findViewById(R.id.hand_answer_button);
        handAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < TEST_ARRAY_LENGTH; i++) {
                    if (ANSWER[i].equals("_")) {
                        ++BLANK_ANSWER_COUNT;
                    }
                }
                int DONE_ANSWER_COUNT = TEST_ARRAY_LENGTH - BLANK_ANSWER_COUNT;
                if (DONE_ANSWER_COUNT >= 0) {
                    if (!isTestTimeOut)
                        confirmHandAnswer(DONE_ANSWER_COUNT, TEST_ARRAY_LENGTH);
                    else {
                        startActivity(new Intent(TestRunningActivity.this, TestResultActivity.class));
                        finish();
                    }
                    BLANK_ANSWER_COUNT = 0;
                    StudentHomeActivity.isDirectlyCheckResult = false;
                }
            }
        });

        // 进入即启动倒计时
        startTime();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void confirmHandAnswer(int DONE_ANSWER_COUNT, int TEST_ARRAY_LENGTH) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("即将提交答案").setMessage("当前完成度为 " + DONE_ANSWER_COUNT + "/" + TEST_ARRAY_LENGTH + " ，确定要提交吗？").setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(TestRunningActivity.this, TestResultActivity.class));
                        Toast.makeText(TestRunningActivity.this, "提交成功！", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                BLANK_ANSWER_COUNT = 0;
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
        }, TestPreviewActivity.timeNumber * 1000); //时间到时自动提交答案并且关闭Dialog（如果你此时打开着）

        //设置透明度
        Window window = alert.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.75f;
        window.setAttributes(lp);
    }

    private void helpTips() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("提示").setMessage(getString(R.string.tips_example)).setCancelable(false)
                .setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();

        //设置透明度
        Window window = alert.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.75f;
        window.setAttributes(lp);
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

    //重置ANSWER数组的值，避免全局静态变量保存使得Activity重启却无法清除选项
    private void resetANSWERArrayValue() {
        for (int i = 0; i < TEST_ARRAY_LENGTH; i++) {
            ANSWER[i] = "_";
        }
    }

    //通过传递各种参数来解析不同的JSON
    private void readJSONandSetContent(String json_name, int test_progress_content) {
//        String test_content = null, test_difficulty_content = null;
//        String test_A = null, test_B = null, test_C = null, test_D = null;
//        try {
//
//            InputStreamReader isr = new InputStreamReader(getAssets().open(json_name), "UTF-8");
//            BufferedReader br = new BufferedReader(isr);
//            String line;
//            StringBuilder builder = new StringBuilder();
//            while ((line = br.readLine()) != null) {
//                builder.append(line);
//            }
//            br.close();
//            isr.close();
//
//            JSONObject root = new JSONObject(builder.toString());
//            test_content = root.getString("content");
//            test_difficulty_content = root.getString("difficulty");
//
//            JSONObject test_Items = new JSONObject(root.getString("choices"));
//            test_A = test_Items.getString("A");
//            test_B = test_Items.getString("B");
//            test_C = test_Items.getString("C");
//            test_D = test_Items.getString("D");
//
//        } catch (IOException | JSONException e) {
//            e.printStackTrace();
//        }

        InputStreamReader json = new InputStreamReader(getClass().getResourceAsStream("/assets/" + json_name));
        ReadJsonByGson readJsonByGson = new ReadJsonByGson(json);
        //解析JSON后设置答案选项内容
        testContentTextView.setText(readJsonByGson.getValue("content"));
        testATextView.setText(readJsonByGson.getArrayValue("choices", "A"));
        testBTextView.setText(readJsonByGson.getArrayValue("choices", "B"));
        testCTextView.setText(readJsonByGson.getArrayValue("choices", "C"));
        testDTextView.setText(readJsonByGson.getArrayValue("choices", "D"));
        testProgressContentTextView.setText(test_progress_content + "/2"); //"/2"待扩展为动态的题目总数
        testDifficultyContentTextView.setText(readJsonByGson.getValue("difficulty"));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //覆盖整个Activity的返回按钮
        int id = item.getItemId();
        if (id == android.R.id.home) {
            exitTips();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitTips();
        }
        return super.onKeyDown(keyCode, event);
    }

    private static class MyAdapter extends ArrayAdapter<String> implements ThemedSpinnerAdapter {
        private final ThemedSpinnerAdapter.Helper mDropDownHelper;

        public MyAdapter(Context context, String[] objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
            mDropDownHelper = new ThemedSpinnerAdapter.Helper(context);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                LayoutInflater inflater = mDropDownHelper.getDropDownViewInflater();
                view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            } else {
                view = convertView;
            }

            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getItem(position));

            return view;
        }

        @Override
        public Resources.Theme getDropDownViewTheme() {
            return mDropDownHelper.getDropDownViewTheme();
        }

        @Override
        public void setDropDownViewTheme(Resources.Theme theme) {
            mDropDownHelper.setDropDownViewTheme(theme);
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
            radioButtonA.setClickable(false);
            radioButtonB.setClickable(false);
            radioButtonC.setClickable(false);
            radioButtonD.setClickable(false);
            Toast.makeText(this, "时间到！系统已为你自动提交答案。", Toast.LENGTH_SHORT).show();
            // 后台提交数据
            handAnswerButton.setText("查看结果");
            handAnswerButton.setTextColor(Color.WHITE);
        }
    }

    private void stopTime() {
        timer.cancel();
    }

    @Override
    public void finish() {
        stopTime(); // 关闭计时器，否则退出此Activity后仍出现“时间到”的Toast
        super.finish();
    }
}
