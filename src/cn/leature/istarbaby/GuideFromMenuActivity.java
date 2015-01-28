package cn.leature.istarbaby;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.leature.istarbaby.common.LzBaseActivity;
import cn.leature.istarbaby.slidingmenu.SlidingMenu;
import cn.leature.istarbaby.utils.DensityUtil;

public class GuideFromMenuActivity extends LzBaseActivity implements
		OnPageChangeListener, OnClickListener, OnTouchListener
{

	private static final int[] mDrawables = new int[]
	{ R.drawable.tutorial01, R.drawable.tutorial02, R.drawable.tutorial03,
			R.drawable.tutorial04, R.drawable.tutorial05 };

	private ViewPager mPager;
	private LinearLayout mIndetorLayout;
	private ArrayList<View> mLayouts;
	private Button mNextbtn;

	private int mPosition = 0;
	private RelativeLayout viewPagerContainer;
	private SlidingMenu mSlidingMenu;

	private int mPicWidth, mPicHeight;

	private static final int Pic_Margin_Horizontal = 20;
	private static final int Pic_Margin_Vertical = 115;

	private static final int Page_Margin = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sliding_main);

		initView();

		mLayouts = new ArrayList<View>();
		mNextbtn.setOnClickListener(this);
		viewPagerContainer.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				// dispatch the events to the ViewPager, to solve the problem
				// that we can swipe only the middle view.
				return mPager.dispatchTouchEvent(event);
			}
		});

		LayoutInflater inflater = getLayoutInflater();

		for (int i = 0; i < mDrawables.length; i++)
		{

			View indicatorView = inflater.inflate(R.layout.gudie_intor, null);
			mIndetorLayout.addView(indicatorView);

			indicatorView.setId(i);

			indicatorView.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					int id = v.getId();
					mPager.setCurrentItem(id);
				}

			});

			View layout = inflater.inflate(R.layout.gudie_pageitem, null);
			ImageView imageView = (ImageView) layout
					.findViewById(R.id.page_image);

			// 重新设置图片大小
			LayoutParams lParams = imageView.getLayoutParams();
			lParams.width = mPicWidth;
			lParams.height = mPicHeight;
			imageView.setLayoutParams(lParams);

			imageView.setImageResource(mDrawables[i]);
			View layout1 = mIndetorLayout.getChildAt(i);
			ImageView img = (ImageView) layout1.findViewById(R.id.little_image);
			img.setImageResource(R.drawable.tut_current2);
			if (i == 0)
			{

				img.setImageResource(R.drawable.tut_current1);

			}
			else
			{

				img.setImageResource(R.drawable.tut_current2);

			}

			mLayouts.add(layout);
		}

		mPager.setOnPageChangeListener(this);
		mPager.setOnTouchListener(this);

		mPager.setOffscreenPageLimit(mDrawables.length);
		mPager.setPageMargin(Page_Margin); // 两图片的距离
		// 设置大小
		LayoutParams lParam = mPager.getLayoutParams();
		lParam.width = mPicWidth;
		mPager.setLayoutParams(lParam);

		mPager.setAdapter(new PagerAdapter()
		{

			@Override
			public Object instantiateItem(ViewGroup container, int position)
			{

				View layout = mLayouts.get(position % mDrawables.length);

				mPager.addView(layout);

				return layout;

			}

			@Override
			public void destroyItem(View container, int position, Object object)
			{
				View layout = mLayouts.get(position % mDrawables.length);

				mPager.removeView(layout);

			}

			@Override
			public float getPageWidth(int position)
			{
				// TODO Auto-generated method stub
				return 1;
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1)
			{
				// TODO Auto-generated method stub
				return arg0 == arg1;
			}

			@Override
			public int getCount()
			{
				// TODO Auto-generated method stub
				return mLayouts.size();
			}
		});
	}

	private void initView()
	{
		// 设置主菜单
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		mSlidingMenu = (SlidingMenu) findViewById(R.id.slidingMain);

		View leftView = getLayoutInflater()
				.inflate(R.layout.sliding_menu, null);
		View centerView = getLayoutInflater().inflate(R.layout.activity_guide,
				null);

		mSlidingMenu.setLeftView(leftView);
		mSlidingMenu.setCenterView(centerView);

		// 设置顶部导航
		TextView titleBarTitle = (TextView) this
				.findViewById(R.id.titlebar_title);
		titleBarTitle.setVisibility(View.INVISIBLE);

		ImageButton showLeftMenu = (ImageButton) centerView
				.findViewById(R.id.titlebar_leftbtn);
		showLeftMenu.setBackgroundResource(R.drawable.selector_hd_menu);
		showLeftMenu.setOnClickListener(this);

		ImageButton titleBarPost = (ImageButton) this
				.findViewById(R.id.titlebar_rightbtn1);
		titleBarPost.setVisibility(View.INVISIBLE);

		ImageView frontImage = (ImageView) this
				.findViewById(R.id.titlebar_front_image);
		frontImage.setVisibility(View.VISIBLE);

		mPager = (ViewPager) centerView.findViewById(R.id.guide_pager);
		mIndetorLayout = (LinearLayout) centerView
				.findViewById(R.id.gudie_indetor);
		mNextbtn = (Button) centerView.findViewById(R.id.into_nextbtn);
		viewPagerContainer = (RelativeLayout) centerView
				.findViewById(R.id.into_rl);

		// 计算图片宽高
		mPicWidth = dm.widthPixels
				- DensityUtil.dip2px(this, Pic_Margin_Horizontal);
		mPicHeight = dm.heightPixels
				- DensityUtil.dip2px(this, Pic_Margin_Vertical);
	}

	@Override
	public void onPageScrollStateChanged(int arg0)
	{

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2)
	{
		if (viewPagerContainer != null)
		{
			viewPagerContainer.invalidate();
		}
	}

	@Override
	public void onPageSelected(int position)
	{

		mPosition = position;
		// 页面更改时，对应的小红点也要更改
		for (int i = 0; i < mDrawables.length; i++)
		{
			View childAt = mIndetorLayout.getChildAt(i);
			ImageView img1 = (ImageView) childAt
					.findViewById(R.id.little_image);
			img1.setImageResource(R.drawable.tut_current2);

		}
		View layout1 = mIndetorLayout.getChildAt(position % mDrawables.length);
		ImageView img = (ImageView) layout1.findViewById(R.id.little_image);
		img.setImageResource(R.drawable.tut_current1);

		if (position == mLayouts.size() - 1) // 控制箭头按钮的去留
		{
			mNextbtn.setVisibility(View.INVISIBLE);
		}
		else
		{
			mNextbtn.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v)
	{ // 箭头按钮的下一步的点击事件

		switch (v.getId())
		{
		case R.id.titlebar_leftbtn:
			mSlidingMenu.showLeftView();
			break;
		case R.id.into_nextbtn:
			mPager.setCurrentItem(mPosition + 1);
			break;
		default:
			break;
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) // 解决viewpager和slidingmenu的冲突
	{

		if (v == mPager && event.getAction() == MotionEvent.ACTION_DOWN)
		{
			mSlidingMenu.getSlidView().setmCtrue();
			return true;
		}
		if (v == mPager && event.getAction() == MotionEvent.ACTION_UP)
		{
			mSlidingMenu.getSlidView().setmCfalse();
		}

		return false;

	}

}
