package com.ysy.classpower_student.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ysy.classpower.R;

/**
 * Created by 姚圣禹 on 2016/2/22.
 */
public class StudentUnreadNotificationsFragment extends Fragment {

    View view;

    public StudentUnreadNotificationsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_student_unread_notifications, container, false);

        return view;
    }
}
