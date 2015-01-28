/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: SlidingMenu.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.slidingmenu
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-6-11 下午12:10:04
 * @version: V1.0  
 */
package cn.leature.istarbaby.slidingmenu;

/**
 * @ClassName: SlidingMenu
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-6-11 下午12:10:04
 */

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import cn.leature.istarbaby.GuideFromMenuActivity;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.child.FamilyActivity;
import cn.leature.istarbaby.daily.DailyListActivity;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.domain.UserInfo;
import cn.leature.istarbaby.friend.FriendListActivity;
import cn.leature.istarbaby.gallery.GalleryActivity;
import cn.leature.istarbaby.monitor.MonitorFragmentActivity;
import cn.leature.istarbaby.questions.QuestionListActivity;
import cn.leature.istarbaby.setting.SettingUpActivity;
import cn.leature.istarbaby.utils.DensityUtil;

public class SlidingMenu extends RelativeLayout implements OnItemClickListener {

	// (左侧划出的)主菜单宽度： 减去屏幕宽度的结果
	public static final int SlidingMenu_Width = 45;

	// 菜单栏 位置标识
	public static final int SLIDINGMENU_NONE = 0;
	public static final int SLIDINGMENU_MONITOR = 1;
	public static final int SLIDINGMENU_FRIENDLIST = 2;
	public static final int SLIDINGMENU_DAILYLIST = 3;
	public static final int SLIDINGMENU_GALLERYLIST = 4;
	public static final int SLIDINGMENU_QUESTIONLIST = 5;
	public static final int SLIDINGMENU_FAMILY = 6;
	public static final int SLIDINGMENU_SPLSH = 7;
	public static final int SLIDINGMENU_SETTINGUP = 8;
	private SlidingView mSlidingView;
	private View mLeftView;
	private Context mContext;

	private SlidingMenuAdapter adapter = null;
	private ListView listView = null;

	private List<SlidingMenuItem> listItems;

	public SlidingMenu(Context context) {
		super(context);
	}

	public SlidingMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setLeftView(View view) {
		mContext = this.getContext();

		DisplayMetrics dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);

		int width = dm.widthPixels
				- DensityUtil.dip2px(mContext, SlidingMenu_Width);

		LayoutParams behindParams = new LayoutParams(width,
				LayoutParams.MATCH_PARENT);

		addView(view, behindParams);

		mLeftView = view;

		setData();

		adapter = new SlidingMenuAdapter(this.getContext(), 0);
		adapter.setListItems(listItems);
		listView = (ListView) this.findViewById(R.id.slidingmenu_list);
		listView.setOnItemClickListener(this);
		listView.setAdapter(adapter);

	}

	public void setCenterView(View view) {
		LayoutParams aboveParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);

		mSlidingView = new SlidingView(getContext());
		addView(mSlidingView, aboveParams);
		mSlidingView.setView(view);
		mSlidingView.invalidate();
		mSlidingView.setLeftView(mLeftView);
		mSlidingView.buildDrawingCache(true);
		mSlidingView.setDrawingCacheEnabled(true);
	}

	public SlidingView getSlidView() {
		return mSlidingView;
	}

	public void showLeftView() {
		mSlidingView.showLeftView();
	}

	public void setData() {

		listItems = new ArrayList<SlidingMenuItem>();

		// header
		listItems.add(new SlidingMenuItem(true, false));

		// 第一组： 主菜单
		listItems.add(new SlidingMenuItem(true, 0, "主菜单", SLIDINGMENU_NONE)); // 菜单组标题
		listItems.add(new SlidingMenuItem(false, R.drawable.mn_icon_babymoni,
				"宝宝监护", SLIDINGMENU_MONITOR));
		listItems.add(new SlidingMenuItem(false, R.drawable.mn_icon_friends,
				"好友列表", SLIDINGMENU_FRIENDLIST));
		listItems.add(new SlidingMenuItem(false, R.drawable.mn_icon_diary,
				"成长日记", SLIDINGMENU_DAILYLIST));
		listItems.add(new SlidingMenuItem(false, R.drawable.mn_icon_photo,
				"精彩写真", SLIDINGMENU_GALLERYLIST));
		listItems.add(new SlidingMenuItem(false, R.drawable.mn_icon_info,
				"育儿百科", SLIDINGMENU_QUESTIONLIST));

		// 第二组： 母子手册
		listItems.add(new SlidingMenuItem(true, 0, "母子手册", SLIDINGMENU_NONE)); // 菜单组标题

		// 取得用户登录信息
		UserInfo loginUserInfo = LoginInfo.loadLoginUserInfo(mContext);

		listItems.add(new SlidingMenuItem(false, true, loginUserInfo
				.getChildList()));

		// 第三组： 亲友
		listItems.add(new SlidingMenuItem(true, 0, "家庭成员", SLIDINGMENU_NONE)); // 菜单组标题
		listItems.add(new SlidingMenuItem(false, R.drawable.mn_icon_family,
				"成员管理", SLIDINGMENU_FAMILY));

		// 第四组： 设定
		listItems.add(new SlidingMenuItem(true, 0, "设定", SLIDINGMENU_NONE)); // 菜单组标题
		listItems.add(new SlidingMenuItem(false, R.drawable.mn_icon_howto,
				"应用说明", SLIDINGMENU_SPLSH));
		listItems.add(new SlidingMenuItem(false, R.drawable.mn_icon_setup,
				"设定", SLIDINGMENU_SETTINGUP));

		// footer
		listItems.add(new SlidingMenuItem(false, true));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long id) {

		SlidingMenuItem item = listItems.get(position);

		if (item.getItemValue() == SLIDINGMENU_NONE) {
			// 未定义子菜单
			return;
		}

		Intent intent = new Intent();
		switch (item.getItemValue()) {
		case SLIDINGMENU_MONITOR:
			intent.setClass(mContext, MonitorFragmentActivity.class);
			break;

		case SLIDINGMENU_FRIENDLIST:
			intent.setClass(mContext, FriendListActivity.class);
			break;

		case SLIDINGMENU_DAILYLIST:
			intent.setClass(mContext, DailyListActivity.class);
			break;
		case SLIDINGMENU_GALLERYLIST:
			intent.setClass(mContext, GalleryActivity.class);
			break;
		case SLIDINGMENU_QUESTIONLIST:
			intent.setClass(mContext, QuestionListActivity.class);
			break;
		case SLIDINGMENU_FAMILY:
			intent.setClass(mContext, FamilyActivity.class);
			break;
		case SLIDINGMENU_SPLSH:
			intent.setClass(mContext, GuideFromMenuActivity.class);
			break;
		case SLIDINGMENU_SETTINGUP:
			intent.setClass(mContext, SettingUpActivity.class);
			break;
		default:
			break;
		}

		mContext.startActivity(intent);

		((Activity) mContext).finish();
		// 设定启动动画
		((Activity) mContext).overridePendingTransition(
				R.anim.activity_in_from_right, R.anim.activity_nothing_in_out);
	}

	public void reloadAdapterData() {
		setData();
		adapter.setListItems(listItems);
		adapter.reloadData();
	}
}
