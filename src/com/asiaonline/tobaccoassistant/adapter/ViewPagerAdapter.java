package com.asiaonline.tobaccoassistant.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter{
	private List<Fragment> list;
	public ViewPagerAdapter(FragmentManager fm, List<Fragment> list){
		super(fm);
		this.list = list;
	}
	public ViewPagerAdapter(FragmentManager fm) {
		super(fm);
	}
	@Override
	public Fragment getItem(int position) {
		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = list.get(0);
			break;
		case 1:
			fragment = list.get(1);
			break;
		case 2:
			fragment = list.get(2);
			break;
		}
		return fragment;
		
	}
	@Override
	public int getCount() {
		
		return 3;
	}

}
