package com.ysy.classpower_teacher.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ysy.classpower.R;
import com.ysy.classpower_common.activities.LoginActivity;

/**
 * Created by 姚圣禹 on 2016/2/5.
 */
public class TeacherWelcomeCenterFragment extends Fragment {

    View view;

    public TeacherWelcomeCenterFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_teacher_welcome_center, container, false);
        Button logoutButton = (Button) view.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });

        TextView teacherNameContentTextView = (TextView) view.findViewById(R.id.teacher_name_content_text_view);
        TextView teacherNumberContentTextView = (TextView) view.findViewById(R.id.teacher_number_content_text_view);
        TextView teacherTitleContentTextView = (TextView) view.findViewById(R.id.teacher_title_content_text_view);
        TextView teacherOfficeContentTextView = (TextView) view.findViewById(R.id.teacher_office_content_text_view);
        TextView teacherPlatformContentTextView = (TextView) view.findViewById(R.id.teacher_platform_content_text_view);

        teacherNameContentTextView.setText("洪源");
        teacherNumberContentTextView.setText("B0000001");
        teacherTitleContentTextView.setText("副教授");
        teacherOfficeContentTextView.setText("机电楼001");
        teacherPlatformContentTextView.setText("离散数学");

        return view;
    }
}
