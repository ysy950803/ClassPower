package com.ysy.classpower_student.activities.base;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Toast;

import com.ysy.classpower.R;
import com.ysy.classpower_student.adapters.StudentWelcomeFragmentAdapter;
import com.ysy.classpower_student.fragments.StudentWelcomeListFragment;

import java.util.ArrayList;
import java.util.List;

public class StudentWelcomeActivity extends AppCompatActivity {

    private long exitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_welcome);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        final FloatingActionButton addFab = (FloatingActionButton) findViewById(R.id.student_welcome_add_fab);

        List<Fragment> fragments = new ArrayList<>();
        Fragment list_fragment = new StudentWelcomeListFragment();
        fragments.add(list_fragment);

        final ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        StudentWelcomeFragmentAdapter fragmentAdapter = new StudentWelcomeFragmentAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(fragmentAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabsFromPagerAdapter(fragmentAdapter);
//        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                mViewPager.setCurrentItem(tab.getPosition());
//                if (tab.getPosition() == 1) {
//                    addFab.setVisibility(View.VISIBLE);
//                    invalidateOptionsMenu();
//                } else if (tab.getPosition() == 0) {
//                    addFab.setVisibility(View.GONE);
//                    invalidateOptionsMenu();
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });

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
        getMenuInflater().inflate(R.menu.menu_student_welcome, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search_class); //在菜单中找到对应控件的item
        MenuItem menuItem1 = menu.findItem(R.id.action_student_message);
        menuItem1.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(StudentWelcomeActivity.this, StudentMessageActivity.class));
                return true;
            }
        });
        MenuItem menuItem2 = menu.findItem(R.id.action_student_personal_center);
        menuItem2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(StudentWelcomeActivity.this, StudentPersonalCenterActivity.class));
                return true;
            }
        });
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setQueryHint("找课堂"); //设置搜索框内的hint文字
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

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            startActivity(new Intent(StudentWelcomeActivity.this, SettingsActivity.class));
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

}
