package com.ysy.classpower_student.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.ysy.classpower.R;
import com.ysy.classpower_common.activities.LoginActivity;
import com.ysy.classpower_utils.PostJsonAndGetCallback;
import com.ysy.classpower_utils.ReadJsonByGson;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 姚圣禹 on 2016/2/5.
 */
public class StudentWelcomeCenterFragment extends Fragment {

    private static final String USER_GETUSER_URL = "http://10.0.2.2:5000/user/getUser";
    View view;
    String token = "";

    public StudentWelcomeCenterFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_student_welcome_center, container, false);

        Button logoutButton = (Button) view.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });

        final TextView studentNameContentTextView = (TextView) view.findViewById(R.id.student_name_content_text_view);
        final TextView studentNumberContentTextView = (TextView) view.findViewById(R.id.student_number_content_text_view);
        final TextView studentSexContentTextView = (TextView) view.findViewById(R.id.student_sex_content_text_view);
        final TextView studentClassContentTextView = (TextView) view.findViewById(R.id.student_class_content_text_view);
        final TextView studentLessonContentTextView = (TextView) view.findViewById(R.id.student_lesson_content_text_view);

        JSONObject object = new JSONObject();
        try {
            object.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new PostJsonAndGetCallback(new AsyncHttpClient(), getContext(), USER_GETUSER_URL, object.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                studentNameContentTextView.setText(jsonByGson.getValue("name"));
                studentNumberContentTextView.setText(jsonByGson.getValue("student_id"));
                studentSexContentTextView.setText(jsonByGson.getValue("gender"));
                studentClassContentTextView.setText(jsonByGson.getValue("major"));
                studentLessonContentTextView.setText(jsonByGson.getValue("class_no"));
            }
        });

        return view;

    }
}
