package cn.leature.istarbaby.child;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzBaseActivity;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.domain.FamilyInfo;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.network.HttpPostUtil;
import cn.leature.istarbaby.network.HttpPostUtil.OnPostProcessListener;
import cn.leature.istarbaby.network.ImageDownloadTask;
import cn.leature.istarbaby.network.ImageDownloadTask.ImageDoneCallback;
import cn.leature.istarbaby.slidingmenu.SlidingMenu;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.ResizeImage;

public class FamilyActivity extends LzBaseActivity implements OnClickListener,
		OnPostProcessListener, OnTouchListener
{

	private SlidingMenu mSlidingMenu;
	private HttpPostUtil httpUtil;
	private static final int ALBUM_LIST_DONE = 1;
	private static final int ALBUM_LIST_ERROR = -1;

	ArrayList<FamilyInfo> mData;
	private TextView mMather, mChild_Name, mFather, mFatherName, mMatherName;
	private ImageView mMather_Image, mFather_Image, mChildImage;
	private LinearLayout mLayouts;
	private int mWidth;
	private LzProgressDialog mProgressDialog;
	private HorizontalScrollView mScrollView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sliding_main);

		initView();
		inItUi();

		httpUtil = HttpPostUtil.getInstance();

		mProgressDialog = new LzProgressDialog(this);
		mProgressDialog.setCancelable(true);

		getDefaultData();
	}

	private void getDefaultData()
	{
		httpUtil.setOnPostProcessListener(this);
		mProgressDialog.show();

		Map<String, String> param = new HashMap<String, String>();
		// 登录用户的ID
		param.put("UserId", LoginInfo.getLoginUserId(this));

		httpUtil.sendPostMessage(ConstantDef.cUrlFamilyDetail, param);
	}

	private void inItUi()
	{
		// 设置主菜单
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		// 宝宝登录
		Button childAddBtn = (Button) this.findViewById(R.id.family_child_add);
		childAddBtn.setOnClickListener(this);

		mMather = (TextView) findViewById(R.id.child_mather);
		mFather = (TextView) findViewById(R.id.child_father);
		mFather_Image = (ImageView) findViewById(R.id.family_father_image);
		mMather_Image = (ImageView) findViewById(R.id.family_mather_image);
		mLayouts = (LinearLayout) findViewById(R.id.family_child_layout_show);
		mScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView1);
		mFatherName = (TextView) findViewById(R.id.child_fathername);
		mMatherName = (TextView) findViewById(R.id.child_mathername);
		mScrollView.setOnTouchListener(this);
		mWidth = dm.widthPixels;
	}

	/**
	 * @Title: initView
	 * @Description: TODO
	 * @return: void
	 */
	private void initView()
	{
		// 设置主菜单
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		mSlidingMenu = (SlidingMenu) findViewById(R.id.slidingMain);

		View leftView = getLayoutInflater()
				.inflate(R.layout.sliding_menu, null);
		View centerView = getLayoutInflater().inflate(R.layout.activity_family,
				null);

		mSlidingMenu.setLeftView(leftView);
		mSlidingMenu.setCenterView(centerView);

		// 设置顶部导航
		TextView titleBarTitle = (TextView) this
				.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("成员管理");

		ImageButton showLeftMenu = (ImageButton) centerView
				.findViewById(R.id.titlebar_leftbtn);
		showLeftMenu.setBackgroundResource(R.drawable.selector_hd_menu);
		showLeftMenu.setOnClickListener(this);

		ImageButton titleBarPost = (ImageButton) this
				.findViewById(R.id.titlebar_rightbtn1);
		titleBarPost.setVisibility(View.INVISIBLE);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.family, menu);
		return true;
	}

	/**
	 * @Title: onClick
	 * @Description: TODO
	 * @param arg0
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{

		case R.id.titlebar_leftbtn:
			mSlidingMenu.showLeftView();
			break;

		case R.id.family_child_add:
			doChildAdd();
			break;
		default:
			break;
		}
	}

	/**
	 * @Title: doChildAdd
	 * @Description: TODO
	 * @return: void
	 */
	private void doChildAdd()
	{
		Intent intent = new Intent();
		intent.setClass(this, ChildAddActivity.class);
		startActivityForResult(intent, ConstantDef.REQUEST_CODE_BABY_ADD);

		overridePendingTransition(R.anim.activity_in_from_bottom,
				R.anim.activity_nothing_in_out);
	}

	@Override
	public void onPostDone(int responseCode, String responseMessage)
	{
		Message msg = Message.obtain();
		msg.arg1 = responseCode;
		msg.obj = responseMessage;

		if (responseCode == HttpPostUtil.POST_SUCCESS_CODE)
		{
			msg.what = ALBUM_LIST_DONE;
		}
		else
		{
			msg.what = ALBUM_LIST_ERROR;
		}

		handler.sendMessage(msg);
	}

	private Handler handler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case ALBUM_LIST_DONE:
				toListDailyDone(msg.obj.toString());
				break;

			default:
				// 错误处理
				toErrorMessage(msg.obj.toString());
				break;
			}

			super.handleMessage(msg);
		}

	};

	protected void toErrorMessage(String msgString)
	{
		// // 出错，显示提示信息
		mProgressDialog.dismiss();

		Toast.makeText(this, msgString, Toast.LENGTH_LONG).show();
	}

	private void toListDailyDone(String listResult)
	{
		mProgressDialog.dismiss();

		try
		{
			mData = new ArrayList<FamilyInfo>();
			mLayouts.removeAllViews();

			JSONArray jsonArray = new JSONArray(listResult);

			for (int i = 0; i < jsonArray.length(); i++)
			{
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String family_id = jsonObject.getString("family_id");
				String user_id = jsonObject.getString("user_id");
				String child_id = jsonObject.getString("child_id");
				String child_no = jsonObject.getString("child_no");
				String relative = jsonObject.getString("relative");
				String usericon = jsonObject.getString("usericon");
				String userName = jsonObject.getString("user_name");
				String childicon = jsonObject.getString("childicon");
				String childName = jsonObject.getString("child_name");
				FamilyInfo info = new FamilyInfo(family_id, userName, user_id,
						child_id, child_no, relative, usericon, childicon,
						childName);
				mData.add(info);

			}
			for (int j = 0; j < mData.size(); j++)
			{
				FamilyInfo info = mData.get(j);

				if ("1".equals(info.getRelative()))
				{
					mFather.setText("爸爸");
					getimage(info.getUsericon(), mFather_Image, 1.26f);

					mFatherName.setText(info.getUserName());

				}
				else if ("2".equals(info.getRelative()))
				{
					mMather.setText("妈妈");

					mMatherName.setText(info.getUserName());

					getimage(info.getUsericon(), mMather_Image, 1.26f);
				}
				else if ("3".equals(info.getRelative()))
				{

					View layout = getLayoutInflater().inflate(
							R.layout.family_child, null);

					mChildImage = (ImageView) layout
							.findViewById(R.id.family_child_image);
					mChild_Name = (TextView) layout
							.findViewById(R.id.family_child_name);
					mChild_Name.setText(info.getChild_name());
					getimage(info.getChildicon(), mChildImage, 1.1136f);

					mLayouts.addView(layout);

				}

			}
			if (mData.size() < 3)
			{
				View layout = getLayoutInflater().inflate(
						R.layout.family_child, null);
				mChildImage = (ImageView) layout
						.findViewById(R.id.family_child_image);
				mChild_Name = (TextView) layout
						.findViewById(R.id.family_child_name);

				mChild_Name.setText("未登录");
				mLayouts.addView(layout);
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

	}

	public void getimage(String url, final ImageView icon, float radio)
	{

		LayoutParams params = icon.getLayoutParams();
		params.width = mWidth / 4;
		params.height = (int) (params.width * radio);

		icon.setLayoutParams(params);
		// 显示头像（登录图片）

		ImageDownloadTask imageTask = new ImageDownloadTask(
				new ImageDoneCallback()
				{

					@Override
					public void imageLoaded(Bitmap result)
					{
						icon.setImageBitmap(ResizeImage.resizeBitmapWithRadio(
								result, 1, 1));
					}
				});
		imageTask.execute(url);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{

		// TODO Auto-generated method stub
		if (v == mScrollView && event.getAction() == MotionEvent.ACTION_DOWN)
		{
			mSlidingMenu.getSlidView().setmCtrue();
			return true;
		}
		if (v == mScrollView && event.getAction() == MotionEvent.ACTION_UP)
		{
			mSlidingMenu.getSlidView().setmCfalse();
		}

		return false;

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK)
		{
			// 刷新菜单
			mSlidingMenu.reloadAdapterData();

			// 重新加载
			getDefaultData();
		}
	}

}
