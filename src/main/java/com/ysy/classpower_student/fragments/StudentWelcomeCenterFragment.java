package com.ysy.classpower_student.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.ysy.classpower.R;
import com.ysy.classpower_common.activities.LoginActivity;
import com.ysy.classpower_utils.CardTurnAnimation;
import com.ysy.classpower_utils.PostJsonAndGetCallback;
import com.ysy.classpower_utils.ReadJsonByGson;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by 姚圣禹 on 2016/2/5.
 */
public class StudentWelcomeCenterFragment extends Fragment {

    private static final String USER_GETUSER_URL = "http://10.0.2.2:5000/user/getUser";
    private static final String AVATAR_URL = "http://10.0.2.2:5000/user/avatar/";
    View view;


    public StudentWelcomeCenterFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        SharedPreferences token_sp = getContext().getSharedPreferences("token", Context.MODE_PRIVATE);
        SharedPreferences name_sp = getContext().getSharedPreferences("name", Context.MODE_PRIVATE);
        SharedPreferences userId_sp = getContext().getSharedPreferences("userId", Context.MODE_PRIVATE);
        SharedPreferences gender_sp = getContext().getSharedPreferences("gender", Context.MODE_PRIVATE);
        SharedPreferences major_sp = getContext().getSharedPreferences("major", Context.MODE_PRIVATE);
        SharedPreferences classNo_sp = getContext().getSharedPreferences("classNo", Context.MODE_PRIVATE);
        String token = token_sp.getString("token", "");
        final String name = name_sp.getString("name", "");
        final String userId = userId_sp.getString("userId", "");
        final String gender = gender_sp.getString("gender", "");
        final String major = major_sp.getString("major", "");
        final String classNo = classNo_sp.getString("classNo", "");

        view = inflater.inflate(R.layout.fragment_student_welcome_center, container, false);

        RelativeLayout studentAvatarLayout = (RelativeLayout) view.findViewById(R.id.student_avatar_layout);
        final RelativeLayout studentWelcomeCenterLayout = (RelativeLayout) view.findViewById(R.id.student_welcome_center_layout);
        final RelativeLayout studentUpAvatarLayout = (RelativeLayout) view.findViewById(R.id.student_up_avatar_layout);
        final ImageView studentAvatar = (ImageView) view.findViewById(R.id.student_avatar_imageView);
        final TextView studentNameContentTextView = (TextView) view.findViewById(R.id.student_name_content_text_view);
        final TextView studentNumberContentTextView = (TextView) view.findViewById(R.id.student_number_content_text_view);
        final TextView studentSexContentTextView = (TextView) view.findViewById(R.id.student_sex_content_text_view);
        final TextView studentClassContentTextView = (TextView) view.findViewById(R.id.student_class_content_text_view);
        final TextView studentLessonContentTextView = (TextView) view.findViewById(R.id.student_lesson_content_text_view);
        Button giveUpAvatarButton = (Button) view.findViewById(R.id.give_up_avatar_btn);

        new AsyncHttpClient().get(AVATAR_URL + userId + ".jpg", new FileAsyncHttpResponseHandler(getContext()) {
            @Override
            public void onFailure(int i, Header[] headers, Throwable throwable, File file) {
                studentAvatar.setImageResource(R.drawable.ic_account_circle_black_48dp);
            }

            @Override
            public void onSuccess(int i, Header[] headers, File file) {

            }
        });
        studentNameContentTextView.setText(name);
        studentNumberContentTextView.setText(userId);
        if (gender.equals("0"))
            studentSexContentTextView.setText("男");
        else if (gender.equals("1"))
            studentSexContentTextView.setText("女");
        studentClassContentTextView.setText(major);
        studentLessonContentTextView.setText(classNo);

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
                        studentNameContentTextView.setText(jsonByGson.getValue("name"));
                        studentNumberContentTextView.setText(jsonByGson.getValue("student_id"));
                        if (jsonByGson.getValue("gender").equals("0"))
                            studentSexContentTextView.setText("男");
                        else if (jsonByGson.getValue("gender").equals("1"))
                            studentSexContentTextView.setText("女");
                        studentClassContentTextView.setText(jsonByGson.getValue("major"));
                        studentLessonContentTextView.setText(jsonByGson.getValue("class_no"));
                    }
                });
                new AsyncHttpClient().get(AVATAR_URL + userId + ".jpg", new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

                    }
                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {

                    }
                });
            }
        });

        Button logoutButton = (Button) view.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogoutTips();
            }
        });

        final CardTurnAnimation animation = new CardTurnAnimation(studentWelcomeCenterLayout, studentUpAvatarLayout);

        studentAvatarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studentWelcomeCenterLayout.startAnimation(animation.getSato(0));
            }
        });

        giveUpAvatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studentUpAvatarLayout.startAnimation(animation.getSato(0));
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
