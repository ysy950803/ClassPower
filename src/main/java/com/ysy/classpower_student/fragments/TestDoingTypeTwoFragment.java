package com.ysy.classpower_student.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.inthecheesefactory.thecheeselibrary.fragment.support.v4.app.StatedFragment;
import com.ysy.classpower.R;
import com.ysy.classpower_student.activities.test.TestDoingActivity;
import com.ysy.classpower_utils.OwnApp;

/**
 * Created by 姚圣禹 on 2016/4/1.
 */
public class TestDoingTypeTwoFragment extends StatedFragment {

    View view;
    private TextView contentTextView;
    private TextView difficultyTextView;
    private Bundle data;
    private RadioGroup radioGroup;
    private CheckBox[] checkBoxes;
    private int checkBoxesNum;
    private LinearLayout linearLayout;
    private OwnApp ownApp;

    public TestDoingTypeTwoFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ownApp = (OwnApp) getActivity().getApplication();
        data = getArguments();
        view = inflater.inflate(R.layout.fragment_test_doing_type_two, container, false);
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
        checkBoxes = new CheckBox[choices.length];
        checkBoxesNum = checkBoxes.length;
        for (int i = 0; i < choices.length; ++i) {
            checkBoxes[i] = new CheckBox(getActivity());
            checkBoxes[i].setPadding(16, 0, 0, 0); // 设置文字距离按钮四周的距离
            checkBoxes[i].setText(choices[i]);
            radioGroup.addView(checkBoxes[i], LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        linearLayout.addView(radioGroup);
        for (int j = 0; j < checkBoxesNum; ++j) {
            checkBoxes[j].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean[] states = new boolean[choices.length];
                    int false_count = 0;
                    for (int i = 0; i < choices.length; ++i) {
                        if (checkBoxes[i].isChecked())
                            states[i] = true;
                        else {
                            states[i] = false;
                            ++false_count;
                        }
                    }
                    if (false_count == states.length)
                        states = null;
                    ownApp.setQuestionStates(data.getInt("question_position"), states);
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
            });
        }

        // 通过应用级全局存储，恢复每道题的选项状态，若是新开的fragment，则判空而不执行下面方法
        if (ownApp.getQuestionStates(data.getInt("question_position")) != null) {
//            Log.d("TEST", "setChecked" + data.getInt("question_position"));
            for (int i = 0; i < checkBoxesNum; ++i) {
                if (ownApp.getQuestionStates(data.getInt("question_position"))[i]) {
                    checkBoxes[i].setChecked(true);
                }
            }
        }

        return view;
    }

    /**
     * Save Fragment's State here
     */
    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
        // For example:
        //outState.putString(text, tvSample.getText().toString());
        // 其实此处并没有用到outState参数来存储状态数据，而是靠的应用级的全局方法来存储的
        boolean[] states = new boolean[checkBoxesNum];
        int false_count = 0;
        for (int i = 0; i < checkBoxesNum; ++i) {
            if (checkBoxes[i].isChecked()) {
                states[i] = true;
            } else {
                states[i] = false;
                ++false_count;
            }
        }
        if (false_count == states.length)
            states = null; // 若全为false，则说明用户未选中任何一项，即布尔数组置空，方便在某些地方判断
        ownApp.setQuestionStates(data.getInt("question_position"), states); // 根据题号来为每道题设置一组选项状态
//        Log.d("TEST", "onSaveState" + data.getInt("question_position"));
    }

    /**
     * Restore Fragment's State here
     */
    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);

    }

}
