package com.ceunsp.app.projeto.Calendar.Helper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ceunsp.app.projeto.Calendar.Activity.CalendarActivity;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence titles[];
    int numbOfTabs;

    public ViewPagerAdapter(FragmentManager fm, CharSequence titles[], int mNumbOfTabs) {
        super(fm);
        this.titles = titles;
        this.numbOfTabs = mNumbOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            CalendarActivity calendarActivity = new CalendarActivity();
            return calendarActivity;
        } else {
            Tab2 tab2 = new Tab2();
            return tab2;
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