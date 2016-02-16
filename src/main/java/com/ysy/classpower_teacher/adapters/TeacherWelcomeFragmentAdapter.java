package com.ysy.classpower_teacher.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by 姚圣禹 on 2016/2/5.
 */
public class TeacherWelcomeFragmentAdapter extends FragmentStatePagerAdapter {

    List<Fragment> mFragments;

    public TeacherWelcomeFragmentAdapter(FragmentManager fm, List<Fragment> fragments) {
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
                return "个人中心";
            case 1:
                return "讲台列表";
        }
        return null;
    }
}
