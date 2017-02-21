package com.saiyi.framework.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件描述：
 * 创建作者：黎丝军
 * 创建时间：2016/8/29 17:27
 */
public class BaseFragmentAdapter extends FragmentPagerAdapter{

    private List<Fragment> mFragments;

    public BaseFragmentAdapter(FragmentManager fm) {
        super(fm);
        mFragments = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    public void addFragment(Fragment fragment) {
        mFragments.add(fragment);
    }
}
