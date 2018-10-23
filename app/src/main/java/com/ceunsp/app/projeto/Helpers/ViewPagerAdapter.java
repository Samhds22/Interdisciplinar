package com.ceunsp.app.projeto.Helpers;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ceunsp.app.projeto.Fragments.CalendarFragment;
import com.ceunsp.app.projeto.Fragments.HistoricFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private CharSequence titles[];
    private int numbOfTabs;
    private String classID;

    public ViewPagerAdapter(FragmentManager fm, CharSequence titles[], int mNumbOfTabs, String classID) {
        super(fm);
        this.titles = titles;
        this.numbOfTabs = mNumbOfTabs;
        this.classID = classID;
    }

    public ViewPagerAdapter(FragmentManager fm, CharSequence titles[], int mNumbOfTabs) {
        super(fm);
        this.titles = titles;
        this.numbOfTabs = mNumbOfTabs;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            if (classID != null){
                return new CalendarFragment(classID);
            } else {
                return new CalendarFragment();
            }

        } else {
            if (classID != null) {
                return new HistoricFragment(classID);
            } else {
                return new HistoricFragment();
            }
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return numbOfTabs;
    }
}