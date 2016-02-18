package com.ysy.classpower_teacher.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.ysy.classpower.R;
import com.ysy.classpower_common.activities.LoginActivity;
import com.ysy.classpower_utils.PostJsonAndGetCallback;
import com.ysy.classpower_utils.ReadJsonByGson;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 姚圣禹 on 2016/2/5.
 */
public class TeacherWelcomeCenterFragment extends Fragment {

    View view;
    private static final String USER_GETUSER_URL = "http://10.0.2.2:5000/user/getUser";

    public TeacherWelcomeCenterFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        SharedPreferences token_sp = getContext().getSharedPreferences("token", Context.MODE_PRIVATE);
        SharedPreferences name_sp = getContext().getSharedPreferences("name", Context.MODE_PRIVATE);
        SharedPreferences userId_sp = getContext().getSharedPreferences("userId", Context.MODE_PRIVATE);
        SharedPreferences gender_sp = getContext().getSharedPreferences("gender", Context.MODE_PRIVATE);
        String token = token_sp.getString("token", "");
        final String name = name_sp.getString("name", "");
        final String userId = userId_sp.getString("userId", "");
        final String gender = gender_sp.getString("gender", "");

        view = inflater.inflate(R.layout.fragment_teacher_welcome_center, container, false);
        Button logoutButton = (Button) view.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogoutTips();
            }
        });

        final TextView teacherNameContentTextView = (TextView) view.findViewById(R.id.teacher_name_content_text_view);
        final TextView teacherNumberContentTextView = (TextView) view.findViewById(R.id.teacher_number_content_text_view);
        final TextView teacherSexContentTextView = (TextView) view.findViewById(R.id.teacher_sex_content_text_view);
        final TextView teacherOfficeContentTextView = (TextView) view.findViewById(R.id.teacher_office_content_text_view);
        final TextView teacherPlatformContentTextView = (TextView) view.findViewById(R.id.teacher_platform_content_text_view);

        teacherNameContentTextView.setText(name);
        teacherNumberContentTextView.setText(userId);
        if (gender.equals("0"))
            teacherSexContentTextView.setText("男");
        else if (gender.equals("1"))
            teacherSexContentTextView.setText("女");
        teacherOfficeContentTextView.setText("");
        teacherPlatformContentTextView.setText("");

        JSONObject token_obj = new JSONObject();
        try {
            token_obj.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String json = token_obj.toString();

        FloatingActionButton refreshFab = (FloatingActionButton) view.findViewById(R.id.refresh_fab);
        refreshFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PostJsonAndGetCallback(new AsyncHttpClient(), getContext(), USER_GETUSER_URL, json, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

                    }
                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {
                        ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                        teacherNameContentTextView.setText(jsonByGson.getValue("name"));
                        teacherNumberContentTextView.setText(jsonByGson.getValue("teacher_id"));
                        if (jsonByGson.getValue("gender").equals("0"))
                            teacherSexContentTextView.setText("男");
                        else if (jsonByGson.getValue("gender").equals("1"))
                            teacherSexContentTextView.setText("女");
                        teacherOfficeContentTextView.setText("");
                        teacherPlatformContentTextView.setText("");
                    }
                });
            }
        });

        return view;
    }

    private void LogoutTips() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示").setMessage("确定要注销当前用户吗？").setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

        //设置透明度
        Window window = alert.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.75f;
        window.setAttributes(lp);
    }

}
