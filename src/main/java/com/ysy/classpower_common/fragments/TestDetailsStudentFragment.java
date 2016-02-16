package com.ysy.classpower_common.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ysy.classpower.R;

/**
 * Created by 姚圣禹 on 2016/2/5.
 */
public class TestDetailsStudentFragment extends Fragment {

    View view;

    public TestDetailsStudentFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_test_details_student, container, false);

        return view;
    }
}
