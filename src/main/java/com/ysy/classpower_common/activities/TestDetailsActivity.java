package com.ysy.classpower_common.activities;

import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.ysy.classpower.R;
import com.ysy.classpower_common.adapters.TestDetailsFragmentAdapter;
import com.ysy.classpower_common.fragments.TestDetailsListFragment;
import com.ysy.classpower_common.fragments.TestDetailsStudentFragment;
import com.ysy.classpower_common.fragments.TestDetailsSummaryFragment;
import com.ysy.classpower_utils.DividerItemDecoration;
import com.ysy.classpower_utils.swipe_back.SwipeBackActivity;

import java.util.ArrayList;
import java.util.List;

public class TestDetailsActivity extends SwipeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar(); //无此则无法实现返回键功能

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        List<Fragment> fragments = new ArrayList<>();
        Fragment list_fragment = new TestDetailsListFragment();
        Fragment summary_fragment = new TestDetailsSummaryFragment();
        Fragment student_fragment = new TestDetailsStudentFragment();
        fragments.add(list_fragment);
        fragments.add(summary_fragment);
        fragments.add(student_fragment);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        TestDetailsFragmentAdapter fragmentAdapter = new TestDetailsFragmentAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(fragmentAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabsFromPagerAdapter(fragmentAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
        getMenuInflater().inflate(R.menu.menu_test_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
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
