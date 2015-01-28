/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: ChildAlbumAdapter.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.child
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-6-12 下午2:22:07
 * @version: V1.0  
 */
package cn.leature.istarbaby.child;

/**
 * @ClassName: ChildAlbumAdapter
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-6-12 下午2:22:07
 */

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;
import cn.leature.istarbaby.domain.ChildAlbumInfo;

public class ChildAlbumAdapter extends FragmentStatePagerAdapter implements
		TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {

	private final Context mContext;
	private final TabHost mTabHost;
	private final ViewPager mViewPager;
	private ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
	private boolean isRefreshData;

	static final class TabInfo {
		private final Class<?> clss;
		private final Bundle args;

		TabInfo(Class<?> _class, Bundle _args) {
			clss = _class;
			args = _args;
		}
	}

	static class DummyTabFactory implements TabHost.TabContentFactory {
		private final Context mContext;

		public DummyTabFactory(Context context) {
			mContext = context;
		}

		@Override
		public View createTabContent(String tag) {
			View v = new View(mContext);
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);

			return v;
		}
	}

	public ChildAlbumAdapter(FragmentActivity activity, TabHost tabHost,
			ViewPager pager) {
		super(activity.getSupportFragmentManager());

		mContext = activity;
		mTabHost = tabHost;
		mTabHost.setOnTabChangedListener(this);

		mViewPager = pager;
		mViewPager.setAdapter(this);
		mViewPager.setOnPageChangeListener(this);
	}

	public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
		tabSpec.setContent(new DummyTabFactory(mContext));

		TabInfo info = new TabInfo(clss, args);
		mTabs.add(info);
		if (isRefreshData == false) {
			mTabHost.addTab(tabSpec);

		}
		notifyDataSetChanged();
	}

	public void notifyBunblesChanged(Class<?> clss, Bundle bundle1,
			Bundle bundle2) {

		mTabs = new ArrayList<TabInfo>();

		TabInfo info1 = new TabInfo(clss, bundle1);
		mTabs.add(info1);

		TabInfo info2 = new TabInfo(clss, bundle2);
		mTabs.add(info2);

		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mTabs.size();
	}

	@Override
	public Fragment getItem(int position) {

		TabInfo info = mTabs.get(position);
		ChildAlbumTabsFragment frg = (ChildAlbumTabsFragment) Fragment
				.instantiate(mContext, info.clss.getName(), info.args);
		Bundle args = info.args;

		@SuppressWarnings("unchecked")
		ArrayList<ChildAlbumInfo> data = (ArrayList<ChildAlbumInfo>) args
				.getSerializable("info");
		if ((args.getString("page")).equals("1")) {
			frg.settab("CurrentTab1");
		} else {
			frg.settab("CurrentTab2");
		}
		frg.setlist(data);
		return frg;
	}

	@Override
	public void onTabChanged(String tabId) {
		int position = mTabHost.getCurrentTab();
		mViewPager.setCurrentItem(position);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		// Unfortunately when TabHost changes the current tab, it kindly
		// also takes care of putting focus on it when not in touch mode.
		// The jerk.
		// This hack tries to prevent this from pulling focus out of our
		// ViewPager.
		TabWidget widget = mTabHost.getTabWidget();
		int oldFocusability = widget.getDescendantFocusability();
		widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		mTabHost.setCurrentTab(position);
		widget.setDescendantFocusability(oldFocusability);
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	public void setIsreFresh() {
		this.isRefreshData = true;
	}
}