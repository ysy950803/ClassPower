package com.ysy.classpower_student.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.inthecheesefactory.thecheeselibrary.fragment.support.v4.app.StatedFragment;
import com.ysy.classpower.R;
import com.ysy.classpower_student.activities.test.TestDoingActivity;
import com.ysy.classpower_utils.OwnApp;

/**
 * Created by 姚圣禹 on 2016/3/24.
 */
public class TestDoingTypeOneFragment extends StatedFragment {

    View view;
    private TextView contentTextView;
    private TextView difficultyTextView;
    private Bundle data;
    private RadioGroup radioGroup;
    private RadioButton[] radioButtons;
    private LinearLayout linearLayout;
    private int radioButtonsNum;
    private OwnApp ownApp;
    private boolean isRestoreChecked = false;

    public TestDoingTypeOneFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ownApp = (OwnApp) getActivity().getApplication();
        data = getArguments();
        view = inflater.inflate(R.layout.fragment_test_doing_type_one, container, false);
        contentTextView = (TextView) view.findViewById(R.id.test_content_text_view);
        difficultyTextView = (TextView) view.findViewById(R.id.test_difficulty_content_text_view);
        linearLayout = (LinearLayout) view.findViewById(R.id.answer_container);

        contentTextView.setText(data.getString("question_content"));
        if (data.getString("question_difficulty") != null) {
            if (data.getString("question_difficulty").equals("1"))
                difficultyTextView.setText("简单");
            else if (data.getString("question_difficulty").equals("2"))
                difficultyTextView.setText("中等");
            else if (data.getString("question_difficulty").equals("3"))
                difficultyTextView.setText("困难");
        }

        final String[] choices = data.getStringArray("question_choices");
        radioGroup = new RadioGroup(getActivity());
        assert choices != null;
        radioButtons = new RadioButton[choices.length];
        radioButtonsNum = radioButtons.length;
        for (int i = 0; i < choices.length; ++i) {
            radioButtons[i] = new RadioButton(getActivity());
            radioButtons[i].setPadding(16, 0, 0, 0); // 设置文字距离按钮四周的距离
            radioButtons[i].setText(choices[i]);
//            radioButtons[i].setTag(i);
            radioGroup.addView(radioButtons[i], LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        linearLayout.addView(radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                boolean[] states = new boolean[choices.length];
                for (int i = 0; i < choices.length; ++i) {
                    states[i] = false;
                    if (radioButtons[i].getId() == checkedId) {
                        states[i] = true;
                    }
                }
                ownApp.setQuestionStates(data.getInt("question_position"), states);
                // 若标记为false（默认），则表示选项不是靠恢复状态而自动check，而是用户手动check，故执行spinner的刷新
                if (!isRestoreChecked) {
                    String[] questionArray = new String[ownApp.getQuestionStates().length];
                    for (int k = 0; k < ownApp.getQuestionStates().length; ++k) {
                        if (ownApp.getQuestionStates(k) == null) {
                            questionArray[k] = "题目" + (k + 1) + "(未完成)";
                        } else
                            questionArray[k] = "题目" + (k + 1) + "(已完成)";
                    }
                    // fragment与activity的通信回调
                    TestDoingActivity testDoingActivity = (TestDoingActivity) getActivity();
                    testDoingActivity.setSpinnerAdapter(questionArray, data.getInt("question_position"));
//                    Log.d("TEST", "setSpinnerAdapter" + data.getInt("question_position"));
                }

            }
        });

        // 通过应用级全局存储，恢复每道题的选项状态，若是新开的fragment，则判空而不执行下面方法
        if (ownApp.getQuestionStates(data.getInt("question_position")) != null) {
//            Log.d("TEST", "setChecked" + data.getInt("question_position"));
            for (int i = 0; i < radioButtonsNum; ++i) {
                if (ownApp.getQuestionStates(data.getInt("question_position"))[i]) {
                    isRestoreChecked = true; // 设置为true表示标记为选项自动check，控制spinner不刷新，否则出现奇怪的viewPager连跳bug
                    radioButtons[i].setChecked(true);
                }
            }
        }

        return view;
    }

//    @Override
//    public void onResume() {
//        final int question_position = ownApp.getQuestionPosition();
//        final String[] choices = data.getStringArray("question_choices");
//        radioButtons = new RadioButton[choices.length];
//        for (int i = 0; i < choices.length; ++i) {
//            radioButtons[i] = new RadioButton(getActivity());
//            radioButtons[i].setPadding(16, 0, 0, 0); // 设置文字距离按钮四周的距离
//            radioButtons[i].setText(choices[i]);
//            radioButtons[i].setTag(i);
//            if (!ownApp.getQuestionsTempInfo().get(question_position).equals("")) {
//                Log.d("TEST", question_position + ownApp.getQuestionsTempInfo().get(question_position));
//                if (ownApp.getQuestionsTempInfo().get(question_position).equals("type_one:" + i)) {
//                    Log.d("TEST", "type_one:" + i);
//                    radioButtons[i].setText("Selected");
//                }
//            }
//            radioGroup.addView(radioButtons[i], LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        }
//        linearLayout.addView(radioGroup);
//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                for (int i = 0; i < choices.length; ++i) {
//                    if (radioButtons[i].getId() == checkedId) {
//                        ownApp.setQuestionsTempInfo(question_position, "type_one:" + radioButtons[i].getTag());
//                        Log.d("TEST", "set_type_one:" + radioButtons[i].getTag());
//                    }
//                }
//            }
//        });
//
//        Log.d("TEST", "onResume" + ownApp.getQuestionPosition());
//        super.onResume();
//    }

    /**
     * Save Fragment's State here
     */
    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
        // For example:
        //outState.putString(text, tvSample.getText().toString());
        // 其实此处并没有用到outState参数来存储状态数据，而是靠的应用级的全局方法来存储的
        boolean[] states = new boolean[radioButtonsNum];
        int false_count = 0;
        for (int i = 0; i < radioButtonsNum; ++i) {
            if (radioButtons[i].isChecked()) {
                states[i] = true;
            } else {
                states[i] = false;
                ++false_count;
            }
        }
        if (false_count == states.length)
            states = null; // 若全为false，则说明用户未选中任何一项，即布尔数组置空，方便在某些地方判断
//        outState.putBooleanArray("radioButtons_state", states);
        ownApp.setQuestionStates(data.getInt("question_position"), states); // 根据题号来为每道题设置一组选项状态
//        Log.d("TEST", "onSaveState" + data.getInt("question_position"));
    }

    /**
     * Restore Fragment's State here
     */
    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        // For example:
        //tvSample.setText(savedInstanceState.getString(text));
//        Log.d("TEST", "onRestoreState" + data.getInt("question_position"));
        // 由于onRestoreState在onCreateView之后执行，所以不需要以下代码来恢复check状态，因为onCreateView末尾已有类似代码
//        boolean[] state = savedInstanceState.getBooleanArray("radioButtons_state");
//        for (int i = 0; i < radioButtonsNum; ++i) {
//            if (state != null && state[i]) radioButtons[i].setChecked(true);
//        }
    }

}
