package com.ysy.classpower_teacher.activities.home;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ysy.classpower.R;
import com.ysy.classpower_student.activities.home.SettingsActivity;
import com.ysy.classpower_teacher.adapters.TeacherWelcomeFragmentAdapter;
import com.ysy.classpower_teacher.fragments.TeacherWelcomeCenterFragment;
import com.ysy.classpower_teacher.fragments.TeacherWelcomeListFragment;

import java.util.ArrayList;
import java.util.List;

public class TeacherWelcomeActivity extends AppCompatActivity {

    private long exitTime;
    private static boolean isCenterOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_welcome);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        List<Fragment> fragments = new ArrayList<>();
        Fragment list_fragment = new TeacherWelcomeListFragment();
        Fragment center_fragment = new TeacherWelcomeCenterFragment();
        fragments.add(center_fragment);
        fragments.add(list_fragment);

        isCenterOpen = false;
        final ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        TeacherWelcomeFragmentAdapter fragmentAdapter = new TeacherWelcomeFragmentAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(fragmentAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabsFromPagerAdapter(fragmentAdapter);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 1) {
                    isCenterOpen = true;
                    invalidateOptionsMenu();
                } else if (tab.getPosition() == 0) {
                    isCenterOpen = false;
                    invalidateOptionsMenu();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void onBackPressed() {
//            super.onBackPressed();
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "请再按一次以退出！", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_teacher_welcome, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search_platform); //在菜单中找到对应控件的item
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setQueryHint("找讲台"); //设置搜索框内的hint文字
        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() { //设置打开关闭动作监听
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });
        if (isCenterOpen) { //根据选座界面是否打开来设置search按钮的隐藏与否
            menuItem.setVisible(true);
        } else
            menuItem.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(TeacherWelcomeActivity.this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
