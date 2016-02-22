package com.ysy.classpower_student.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by 姚圣禹 on 2016/2/22.
 */
public class StudentMessageFragmentAdapter extends FragmentStatePagerAdapter {
    List<Fragment> mFragments;

    public StudentMessageFragmentAdapter(FragmentManager fm, List<Fragment> fragments) {
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
                return "未读通知";
            case 1:
                return "未做测试";
        }
        return null;
    }
}
