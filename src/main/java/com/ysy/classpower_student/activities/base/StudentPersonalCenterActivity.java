package com.ysy.classpower_student.activities.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.ysy.classpower.R;
import com.ysy.classpower_common.constant.ServerUrlConstant;
import com.ysy.classpower_student.activities.home.SettingsActivity;
import com.ysy.classpower_student.activities.home.StudentHomeActivity;
import com.ysy.classpower_utils.OwnApp;
import com.ysy.classpower_utils.for_design.CardTurnAnimation;
import com.ysy.classpower_utils.for_design.CircularImageView;
import com.ysy.classpower_utils.json_processor.PostJsonAndGetCallback;
import com.ysy.classpower_utils.json_processor.ReadJsonByGson;
import com.ysy.classpower_utils.swipe_back.SwipeBackActivity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class StudentPersonalCenterActivity extends SwipeBackActivity {

    private static final String IMAGE_UNSPECIFIED = "image/*";
    private static final int ALBUM_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private static final int CROP_REQUEST_CODE = 4;
    private CircularImageView studentAvatar;
    private String userId;
    private String token;
    private byte[] studentAvatarBytes = null;
    private Bitmap photo = null;
    private FloatingActionButton refreshFab;

    private CardView studentPersonalInfoCardView;
    private CardView studentUpAvatarCardView;

    private TextView studentNameContentTextView;
    private TextView studentNumberContentTextView;
    private TextView studentSexContentTextView;
    private TextView studentClassContentTextView;
    private TextView studentEmailContentTextView;
    private TextView studentTelContentTextView;
    private TextView studentLessonContentTextView;

    private boolean isStudentAvatarClicked;

    private OwnApp ownApp;

    private ProgressDialog waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_personal_center);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();
        ownApp = (OwnApp) getApplication();

        isStudentAvatarClicked = false;

        SharedPreferences token_sp = getSharedPreferences("token", Context.MODE_PRIVATE);
        SharedPreferences userId_sp = getSharedPreferences("userId", Context.MODE_PRIVATE);
        SharedPreferences name_sp = getSharedPreferences("name", Context.MODE_PRIVATE);
        SharedPreferences gender_sp = getSharedPreferences("gender", Context.MODE_PRIVATE);
        SharedPreferences className_sp = getSharedPreferences("className", Context.MODE_PRIVATE);
        SharedPreferences email_sp = getSharedPreferences("email", Context.MODE_PRIVATE);
        SharedPreferences tel_sp = getSharedPreferences("tel", Context.MODE_PRIVATE);
        token = token_sp.getString("token", "");
        userId = userId_sp.getString("userId", "");
        String name = name_sp.getString("name", "");
        boolean gender = gender_sp.getBoolean("gender", true);
        String className = className_sp.getString("className", "");
        String email = email_sp.getString("email", "");
        String tel = tel_sp.getString("tel", "");

        studentAvatar = (CircularImageView) findViewById(R.id.student_avatar_imageView);
        studentNameContentTextView = (TextView) findViewById(R.id.student_name_content_text_view);
        studentNumberContentTextView = (TextView) findViewById(R.id.student_number_content_text_view);
        studentSexContentTextView = (TextView) findViewById(R.id.student_sex_content_text_view);
        studentClassContentTextView = (TextView) findViewById(R.id.student_class_content_text_view);
        studentEmailContentTextView = (TextView) findViewById(R.id.student_email_content_textView);
        studentTelContentTextView = (TextView) findViewById(R.id.student_tel_content_textView);
        studentLessonContentTextView = (TextView) findViewById(R.id.student_lesson_content_text_view);
        studentPersonalInfoCardView = (CardView) findViewById(R.id.student_personal_info_cardView);
        studentPersonalInfoCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        studentUpAvatarCardView = (CardView) findViewById(R.id.student_up_avatar_cardView);
        studentUpAvatarCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        CircularImageView giveUpAvatarButton = (CircularImageView) findViewById(R.id.give_up_avatar_btn);
        Button choosePicAsAvatarBtn = (Button) findViewById(R.id.choose_pic_as_avatar_btn);
        Button takePhotoAsAvatarBtn = (Button) findViewById(R.id.take_photo_as_avatar_btn);
        CircularImageView confirmUpAvatarBtn = (CircularImageView) findViewById(R.id.confirm_up_avatar_btn);

        studentNameContentTextView.setText(name);
        studentNumberContentTextView.setText(userId);
        if (gender)
            studentSexContentTextView.setText("男");
        else
            studentSexContentTextView.setText("女");
        studentClassContentTextView.setText(className);
        studentEmailContentTextView.setText(email);
        studentTelContentTextView.setText(tel);
        studentLessonContentTextView.setText("");

        JSONObject token_obj = new JSONObject();
        try {
            token_obj.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String json = token_obj.toString();
        // 刷新按钮
        refreshFab = (FloatingActionButton) findViewById(R.id.student_personal_center_refresh_fab);
        refreshFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), ServerUrlConstant.getUserMeUrl(ownApp.getURL_FIGURE()), json, new TextHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        waitDialog = ProgressDialog.show(StudentPersonalCenterActivity.this, "正在刷新个人信息", "请稍等…");
                        super.onStart();
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                        waitDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "网络错误，刷新失败！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {
                        waitDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "个人信息刷新成功！", Toast.LENGTH_SHORT).show();
                        ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                        String name = jsonByGson.getArrayValue("info", "name");
                        String userId = jsonByGson.getArrayValue("info", "user_id");
                        String email = jsonByGson.getArrayValue("info", "email");
                        String tel = jsonByGson.getArrayValue("info", "tel");
                        String className = jsonByGson.getArrayValue("info", "class_name");
                        if (jsonByGson.getArrayBoolValue("info", "gender")) {
                            studentSexContentTextView.setText("男");
                            updateInfoFromRefresh(email, true, name, tel, userId, className);
                        } else {
                            studentSexContentTextView.setText("女");
                            updateInfoFromRefresh(email, false, name, tel, userId, className);
                        }
                        studentNameContentTextView.setText(name);
                        studentNumberContentTextView.setText(userId);
                        studentEmailContentTextView.setText(email);
                        studentTelContentTextView.setText(tel);
                        studentClassContentTextView.setText(className);
                        studentLessonContentTextView.setText(""); // 当前课堂获取方法待定
                    }
                });
                new AsyncHttpClient().get(ServerUrlConstant.getUserAvatarUrl(ownApp.getURL_FIGURE()) + "/" + userId + ".jpg", new BinaryHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        Log.d("HLength/BLength", headers.length + "/" + bytes.length);
//                        for (int j = 0; j < headers.length; ++j) {
//                            Log.d("HContent" + j, headers[j] + "");
//                        }
                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        studentAvatar.setImageBitmap(bm);
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        studentAvatar.setImageResource(R.drawable.ic_account_circle_white_48dp);
                    }
                });
            }
        });

        final CardTurnAnimation animation = new CardTurnAnimation(studentPersonalInfoCardView, studentUpAvatarCardView);
        studentAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isStudentAvatarClicked) {
                    isStudentAvatarClicked = true;
                    studentPersonalInfoCardView.startAnimation(animation.getSato(0));
                    refreshFab.setVisibility(View.GONE);
                }
            }
        });

        assert choosePicAsAvatarBtn != null;
        choosePicAsAvatarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
                startActivityForResult(intent, ALBUM_REQUEST_CODE);
            }
        });

        assert takePhotoAsAvatarBtn != null;
        takePhotoAsAvatarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.
                        getExternalStorageDirectory(), "/student_" + userId + "_avatar_temp.jpg")));
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });

        assert confirmUpAvatarBtn != null;
        confirmUpAvatarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upAvatar();
            }
        });

        assert giveUpAvatarButton != null;
        giveUpAvatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStudentAvatarClicked) {
                    isStudentAvatarClicked = false;
                    studentUpAvatarCardView.startAnimation(animation.getSato(0));
                    refreshFab.setVisibility(View.VISIBLE);
                }
            }
        });

        if (ownApp.getBitmap() != null)
            studentAvatar.setImageBitmap(ownApp.getBitmap());
        else
            new AsyncHttpClient().get(ServerUrlConstant.getUserAvatarUrl(ownApp.getURL_FIGURE()) + "/" + userId + ".jpg", new BinaryHttpResponseHandler() {
                @Override
                public void onStart() {
                    waitDialog = ProgressDialog.show(StudentPersonalCenterActivity.this, "正在获取头像", "请稍等…");
                    super.onStart();
                }

                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    waitDialog.dismiss();
                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    studentAvatar.setImageBitmap(bm);
                    ownApp.setBitmap(bm);
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    waitDialog.dismiss();
                    studentAvatar.setImageResource(R.drawable.ic_account_circle_white_48dp);
                }

            });

    }

    @Override
    protected void onResume() {
        if (studentAvatarBytes == null) {
            // 分段延时两次，保证动效后头像按正常大小显示
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (ownApp.getBitmap() != null)
                        studentAvatar.setImageBitmap(ownApp.getBitmap());
                    else
                        studentAvatar.setImageResource(R.drawable.ic_account_circle_white_48dp);
                }
            }, 512);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (ownApp.getBitmap() != null)
                        studentAvatar.setImageBitmap(ownApp.getBitmap());
                    else
                        studentAvatar.setImageResource(R.drawable.ic_account_circle_white_48dp);
                }
            }, 1024);
        }
        super.onResume();
    }

    private void updateInfoFromRefresh(String email, boolean gender, String name, String tel, String userId, String className) {
        SharedPreferences email_sp = getSharedPreferences("email", Context.MODE_PRIVATE);
        SharedPreferences gender_sp = getSharedPreferences("gender", Context.MODE_PRIVATE);
        SharedPreferences name_sp = getSharedPreferences("name", Context.MODE_PRIVATE);
        SharedPreferences tel_sp = getSharedPreferences("tel", Context.MODE_PRIVATE);
        SharedPreferences userId_sp = getSharedPreferences("userId", Context.MODE_PRIVATE);
        SharedPreferences className_sp = getSharedPreferences("className", Context.MODE_PRIVATE);

        SharedPreferences.Editor email_editor = email_sp.edit();
        SharedPreferences.Editor gender_editor = gender_sp.edit();
        SharedPreferences.Editor name_editor = name_sp.edit();
        SharedPreferences.Editor tel_editor = tel_sp.edit();
        SharedPreferences.Editor userId_editor = userId_sp.edit();
        SharedPreferences.Editor className_editor = className_sp.edit();

        email_editor.putString("email", email);
        gender_editor.putBoolean("gender", gender);
        name_editor.putString("name", name);
        tel_editor.putString("tel", tel);
        userId_editor.putString("userId", userId);
        className_editor.putString("className", className);

        email_editor.apply();
        gender_editor.apply();
        name_editor.apply();
        tel_editor.apply();
        userId_editor.apply();
        className_editor.apply();
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
                    studentAvatar.setImageBitmap(photo); // 把图片显示在ImageView控件上
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void upAvatar() {
        if (studentAvatarBytes == null) {
            Toast.makeText(getApplicationContext(), "未设置头像！", Toast.LENGTH_SHORT).show();
        } else {
            JSONObject up_avatar_obj = new JSONObject();
            try {
                up_avatar_obj.put("token", token);
                up_avatar_obj.put("img", Base64.encodeToString(studentAvatarBytes, Base64.DEFAULT));
                Log.d("SABLength", studentAvatarBytes.length + "");
//                Log.d("Base64Code", Base64.encodeToString(studentAvatarBytes, Base64.NO_WRAP));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String json = up_avatar_obj.toString();
//            Log.d("Json", json);
            new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), ServerUrlConstant.getUserAvatarUrl(ownApp.getURL_FIGURE()), json, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    waitDialog = ProgressDialog.show(StudentPersonalCenterActivity.this, "正在上传头像", "请稍等…");
                    super.onStart();
                }

                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    waitDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "头像上传失败！", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    waitDialog.dismiss();
                    studentAvatar.setImageBitmap(photo);
                    Toast.makeText(getApplicationContext(), "头像上传成功！", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_student_personal_center, menu);
        MenuItem menuItem = menu.findItem(R.id.action_student_settings);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(StudentPersonalCenterActivity.this, SettingsActivity.class));
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            scrollToFinishActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        scrollToFinishActivity();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

}
