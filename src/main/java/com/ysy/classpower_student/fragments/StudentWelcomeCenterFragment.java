package com.ysy.classpower_student.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import com.loopj.android.http.Base64;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.ysy.classpower.R;
import com.ysy.classpower_student.activities.base.StudentLoginActivity;
import com.ysy.classpower_common.constant.ServerUrlConstant;
import com.ysy.classpower_student.activities.base.StudentWelcomeActivity;
import com.ysy.classpower_utils.CardTurnAnimation;
import com.ysy.classpower_utils.PostJsonAndGetCallback;
import com.ysy.classpower_utils.ReadJsonByGson;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by 姚圣禹 on 2016/2/5.
 */
public class StudentWelcomeCenterFragment extends Fragment {

    View view;
    private static final String USER_ME_URL = ServerUrlConstant.USER_ME_URL;
    private static final String USER_AVATAR_URL = ServerUrlConstant.USER_AVATAR_URL;
    private static final String IMAGE_UNSPECIFIED = "image/*";
    private static final int ALBUM_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private static final int CROP_REQUEST_CODE = 4;
    private ImageView studentAvatar;
    private ImageView studentUpAvatar;
    private String userId;
    private String token;
    private byte[] studentAvatarBytes = null;
    private Bitmap photo = null;
    private FloatingActionButton refreshFab;

    private RelativeLayout studentWelcomeCenterLayout;
    private RelativeLayout studentUpAvatarLayout;

    private TextView studentNameContentTextView;
    private TextView studentNumberContentTextView;
    private TextView studentSexContentTextView;
    private TextView studentClassContentTextView;
    private TextView studentEmailContentTextView;
    private TextView studentTelContentTextView;
    private TextView studentLessonContentTextView;

    public StudentWelcomeCenterFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        SharedPreferences token_sp = getContext().getSharedPreferences("token", Context.MODE_PRIVATE);
        SharedPreferences userId_sp = getContext().getSharedPreferences("userId", Context.MODE_PRIVATE);
        SharedPreferences name_sp = getContext().getSharedPreferences("name", Context.MODE_PRIVATE);
        SharedPreferences gender_sp = getContext().getSharedPreferences("gender", Context.MODE_PRIVATE);
        SharedPreferences className_sp = getContext().getSharedPreferences("className", Context.MODE_PRIVATE);
        SharedPreferences email_sp = getContext().getSharedPreferences("email", Context.MODE_PRIVATE);
        SharedPreferences tel_sp = getContext().getSharedPreferences("tel", Context.MODE_PRIVATE);
        token = token_sp.getString("token", "");
        userId = userId_sp.getString("userId", "");
        String name = name_sp.getString("name", "");
        boolean gender = gender_sp.getBoolean("gender", true);
        String className = className_sp.getString("className", "");
        String email = email_sp.getString("email", "");
        String tel = tel_sp.getString("tel", "");

        view = inflater.inflate(R.layout.fragment_student_welcome_center, container, false);

        studentAvatar = (ImageView) view.findViewById(R.id.student_avatar_imageView);
        studentUpAvatar = (ImageView) view.findViewById(R.id.student_up_avatar_imageView);
        studentWelcomeCenterLayout = (RelativeLayout) view.findViewById(R.id.student_welcome_center_layout);
        studentUpAvatarLayout = (RelativeLayout) view.findViewById(R.id.student_up_avatar_layout);
        studentNameContentTextView = (TextView) view.findViewById(R.id.student_name_content_text_view);
        studentNumberContentTextView = (TextView) view.findViewById(R.id.student_number_content_text_view);
        studentSexContentTextView = (TextView) view.findViewById(R.id.student_sex_content_text_view);
        studentClassContentTextView = (TextView) view.findViewById(R.id.student_class_content_text_view);
        studentEmailContentTextView = (TextView) view.findViewById(R.id.student_email_content_textView);
        studentTelContentTextView = (TextView) view.findViewById(R.id.student_tel_content_textView);
        studentLessonContentTextView = (TextView) view.findViewById(R.id.student_lesson_content_text_view);
        RelativeLayout studentAvatarLayout = (RelativeLayout) view.findViewById(R.id.student_avatar_layout);
        Button giveUpAvatarButton = (Button) view.findViewById(R.id.give_up_avatar_btn);
        Button choosePicAsAvatarBtn = (Button) view.findViewById(R.id.choose_pic_as_avatar_btn);
        Button takePhotoAsAvatarBtn = (Button) view.findViewById(R.id.take_photo_as_avatar_btn);
        Button confirmUpAvatarBtn = (Button) view.findViewById(R.id.confirm_up_avatar_btn);

        new AsyncHttpClient().get(USER_AVATAR_URL + "/" + userId + ".jpg", new BinaryHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                studentAvatar.setImageBitmap(bm);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
        studentNameContentTextView.setText(name);
        studentNumberContentTextView.setText(userId);
        if (gender)
            studentSexContentTextView.setText("男");
        else if (!gender)
            studentSexContentTextView.setText("女");
        studentClassContentTextView.setText(className);
        studentEmailContentTextView.setText(email);
        studentTelContentTextView.setText(tel);
        studentLessonContentTextView.setText("");

        refreshFab = (FloatingActionButton) getActivity().findViewById(R.id.student_welcome_refresh_fab);
        JSONObject token_obj = new JSONObject();
        try {
            token_obj.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String json = token_obj.toString();
        // 刷新按钮
        refreshFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PostJsonAndGetCallback(new AsyncHttpClient(), getContext(), USER_ME_URL, json, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                        Toast.makeText(getContext(), "网络错误，刷新失败！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {
                        Toast.makeText(getContext(), "个人信息刷新成功！", Toast.LENGTH_SHORT).show();
                        ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                        if (jsonByGson.getArrayBoolValue("info", "gender"))
                            studentSexContentTextView.setText("男");
                        else
                            studentSexContentTextView.setText("女");
                        studentNameContentTextView.setText(jsonByGson.getArrayValue("info", "name"));
                        studentNumberContentTextView.setText(jsonByGson.getArrayValue("info", "user_id"));
                        studentEmailContentTextView.setText(jsonByGson.getArrayValue("info", "email"));
                        studentTelContentTextView.setText(jsonByGson.getArrayValue("info", "tel"));
                        studentClassContentTextView.setText(jsonByGson.getArrayValue("info", "class_name"));
                        studentLessonContentTextView.setText(""); // 当前课堂获取方法待定
                    }
                });
                new AsyncHttpClient().get(USER_AVATAR_URL + "/" + userId + ".jpg", new BinaryHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        studentAvatar.setImageBitmap(bm);
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                    }
                });
            }
        });

        Button updateInfoButton = (Button) view.findViewById(R.id.update_info_button);
        updateInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                refreshFab.setVisibility(View.GONE);
                StudentWelcomeActivity.isUpAvatarOpen = true;
            }
        });

        choosePicAsAvatarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
                startActivityForResult(intent, ALBUM_REQUEST_CODE);
            }
        });

        takePhotoAsAvatarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.
                        getExternalStorageDirectory(), "/student_" + userId + "_avatar_temp.jpg")));
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });

        confirmUpAvatarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upAvatar();
            }
        });

        giveUpAvatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studentUpAvatarLayout.startAnimation(animation.getSato(0));
                refreshFab.setVisibility(View.VISIBLE);
                StudentWelcomeActivity.isUpAvatarOpen = false;
            }
        });

        return view;
    }

    private void startCrop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");//调用Android系统自带的一个图片剪裁页面,
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");//进行修剪
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 128);
        intent.putExtra("outputY", 128);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0) {
            return;
        }
        switch (requestCode) {
            case ALBUM_REQUEST_CODE:
                if (data == null) {
                    return;
                }
                startCrop(data.getData());
                break;
            case CAMERA_REQUEST_CODE:
                File picture = new File(Environment.getExternalStorageDirectory()
                        + "/student_" + userId + "_avatar_temp.jpg");
                startCrop(Uri.fromFile(picture));
                break;
            case CROP_REQUEST_CODE:
                if (data == null) {
                    // TODO 如果之前以后有设置过显示之前设置的图片 否则显示默认的图片
                    return;
                }
                Bundle extras = data.getExtras();
                if (extras != null) {
                    photo = extras.getParcelable("data");
                    ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, bAOS); // 百分比（0-100）压缩文件
                    studentAvatarBytes = bAOS.toByteArray();
                    try {
                        bAOS.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    studentUpAvatar.setImageBitmap(photo); //把图片显示在ImageView控件上
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void LogoutTips() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示").setMessage("确定要注销当前用户吗？").setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getActivity(), StudentLoginActivity.class));
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

    private void upAvatar() {
        if (studentAvatarBytes == null) {
            Toast.makeText(getContext(), "未设置头像！", Toast.LENGTH_SHORT).show();
        } else {
            String base64_code = Base64.encodeToString(studentAvatarBytes, 0, studentAvatarBytes.length, Base64.DEFAULT);
            JSONObject up_avatar_obj = new JSONObject();
            try {
                up_avatar_obj.put("token", token);
                up_avatar_obj.put("img", base64_code);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String json = up_avatar_obj.toString();
            new PostJsonAndGetCallback(new AsyncHttpClient(), getContext(), USER_AVATAR_URL, json, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    Toast.makeText(getContext(), "头像上传失败！", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    studentAvatar.setImageBitmap(photo);
                    Toast.makeText(getContext(), "头像上传成功！", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}