package com.asiaonline.tobaccoassistant.adapter;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.asiaonline.tobaccoassistant.entity.Contract;
import com.asiaonline.tobaccoassistant.frag.FarmerShowFragment;

public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter{
	private List<Contract> list;
	public MyFragmentPagerAdapter(FragmentManager fm, List<Contract> list) {
		super(fm);
		this.list = list;
	}
	public MyFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}
	@Override
	public FarmerShowFragment getItem(int position) {
		FarmerShowFragment frag = new FarmerShowFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable("contract", list.get(position));
		bundle.putInt("from", FarmerShowFragment.FROM_LIST);
		frag.setArguments(bundle);
		return frag;
	}
	@Override
	public int getCount() {
		return list.size();
	}
}
