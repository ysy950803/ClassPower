package com.ysy.classpower_student.activities.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.kenumir.materialsettings.items.DividerItem;
import com.kenumir.materialsettings.items.HeaderItem;
import com.kenumir.materialsettings.items.TextItem;
import com.kenumir.materialsettings.storage.PreferencesStorageInterface;
import com.kenumir.materialsettings.storage.StorageInterface;
import com.ysy.classpower_student.activities.base.StudentLoginActivity;
import com.ysy.classpower_utils.DataCleanManager;
import com.ysy.classpower_utils.DestroyAllActivities;
import com.ysy.classpower_utils.for_design.OwnMaterialSettings;

/**
 * Created by 姚圣禹 on 2016/4/8.
 */
public class SettingsActivity extends OwnMaterialSettings {

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public StorageInterface initStorageInterface() {
//        Log.d("TEST", this.getPackageName());
        return new PreferencesStorageInterface(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        DestroyAllActivities.getInstance().addActivity(this);
//        addItem(new HeaderItem(this).setTitle("Sample title 1"));
//        addItem(new CheckboxItem(this, "key1").setTitle("Checkbox item 1").setSubtitle("Subtitle text 1").setOnCheckedChangeListener(new CheckboxItem.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChange(CheckboxItem cbi, boolean isChecked) {
//                Toast.makeText(SettingsActivity.this, "CHECKED: " + isChecked, Toast.LENGTH_SHORT).show();
//            }
//        }));
//        addItem(new DividerItem(this));
//        addItem(new SwitcherItem(this, "key1a").setTitle("Switcher item 3 - no subtitle"));
//        addItem(new DividerItem(this));
//        addItem(new SwitcherItem(this, "key1b").setTitle("Switcher item 3").setSubtitle("With subtitle"));
//        addItem(new DividerItem(this));
//        addItem(new CheckboxItem(this, "key2").setTitle("Checkbox item 2").setSubtitle("Subtitle text 2 with long text and more txt and more and more ;-)").setDefaultValue(true));
//        addItem(new DividerItem(this));
//        addItem(new CheckboxItem(this, "key1c").setTitle("Checkbox item 3 - no subtitle"));
//        addItem(new DividerItem(this));
//        addItem(new TextItem(this, "key3").setTitle("Simple text item 1").setSubtitle("Subtitle of simple text item 1").setOnclick(new TextItem.OnClickListener() {
//            @Override
//            public void onClick(TextItem v) {
//                Toast.makeText(SettingsActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
//            }
//        }));
        addItem(new HeaderItem(this).setTitle("默认"));
        addItem(new TextItem(this, "reset_all_tips").setTitle("重置设置").setSubtitle("重置“不再提醒”等类似提示").setOnclick(new TextItem.OnClickListener() {
            @Override
            public void onClick(TextItem v) {
                Toast.makeText(SettingsActivity.this, "重置成功，相关提示将恢复显示。", Toast.LENGTH_SHORT).show();
                getStorageInterface().save("reset_all_tips", true);
            }
        }));
        addItem(new DividerItem(this));
        addItem(new HeaderItem(this).setTitle("清理"));
        String totalCache = "未知";
        try {
            totalCache = DataCleanManager.getTotalCacheSize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        addItem(new TextItem(this, "logout").setTitle("清除缓存").setSubtitle(totalCache).setOnclick(new TextItem.OnClickListener() {
            @Override
            public void onClick(TextItem item) {
                DataCleanManager.clearAllCache(getApplicationContext());
                String totalCache = "未知";
                try {
                    totalCache = DataCleanManager.getTotalCacheSize(getApplicationContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                item.setSubtitle(totalCache);
            }
        }));
        addItem(new DividerItem(this));
//        addItem(new TextItem(this, "key5a").setTitle("Simple text item with icon - no subtitle").setIcon(R.drawable.ic_check_circle_black_24dp));
//        addItem(new DividerItem(this));
//        addItem(new TextItem(this, "key5b").setTitle("Simple text item with icon - no subtitle").setSubtitle("Subtitle of item with icon").setIcon(R.drawable.ic_check_circle_black_24dp));
        addItem(new HeaderItem(this).setTitle("账户"));
        addItem(new TextItem(this, "logout").setTitle("注销用户").setSubtitle("退出当前账号，返回登录页面").setOnclick(new TextItem.OnClickListener() {
            @Override
            public void onClick(TextItem item) {
                logoutTips();
            }
        }));
    }

    private void logoutTips() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("提示").setMessage("确定要注销当前用户吗？").setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(SettingsActivity.this, StudentLoginActivity.class));
                        finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        final android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();

        //设置透明度
        Window window = alert.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.75f;
        window.setAttributes(lp);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        scrollToFinishActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //覆盖整个Activity的返回按钮
        int id = item.getItemId();
        if (id == android.R.id.home) {
//            onBackPressed(); //调用onKeyDown内部方法
            scrollToFinishActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
