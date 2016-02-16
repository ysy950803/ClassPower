package com.ysy.classpower_common.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by 姚圣禹 on 2016/2/5.
 */
public class TestDetailsFragmentAdapter extends FragmentStatePagerAdapter {

    List<Fragment> mFragments;

    public TestDetailsFragmentAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "题目列表";
            case 1:
                return "测试概况";
            case 2:
                return "学生详情";
        }
        return null;
    }
}
