package com.ceunsp.app.projeto.Calendar.Helper;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ceunsp.app.projeto.Calendar.Activity.CalendarActivity;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence titles[];
    int numbOfTabs;
    String classID;

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
        this.classID = classID;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            if (classID != null){
                CalendarActivity calendarActivity = new CalendarActivity(classID); //alterado
                return calendarActivity;
            } else {
                CalendarActivity calendarActivity = new CalendarActivity(); //alterado
                return calendarActivity;
            }

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