package com.ysy.classpower_student.activities.base;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ysy.classpower.R;
import com.ysy.classpower_student.activities.home.SettingsActivity;
import com.ysy.classpower_student.adapters.StudentMessageFragmentAdapter;
import com.ysy.classpower_student.fragments.StudentUndoTestsFragment;
import com.ysy.classpower_student.fragments.StudentUnreadNotificationsFragment;
import com.ysy.classpower_utils.swipe_back.SwipeBackActivity;

import java.util.ArrayList;
import java.util.List;

public class StudentMessageActivity extends SwipeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_message);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar(); //无此则无法实现返回键功能

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.student_message_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        List<Fragment> fragments = new ArrayList<>();
        Fragment unread_fragment = new StudentUnreadNotificationsFragment();
        Fragment undo_fragment = new StudentUndoTestsFragment();
        fragments.add(unread_fragment);
        fragments.add(undo_fragment);

        final ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        StudentMessageFragmentAdapter fragmentAdapter = new StudentMessageFragmentAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(fragmentAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabsFromPagerAdapter(fragmentAdapter);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_student_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            startActivity(new Intent(StudentMessageActivity.this, SettingsActivity.class));
//            return true;
//        } else
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

}
