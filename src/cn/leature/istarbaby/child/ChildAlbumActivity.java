package cn.leature.istarbaby.child;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.domain.ChildAlbumInfo;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.network.HttpPostUtil;
import cn.leature.istarbaby.network.HttpPostUtil.OnPostProcessListener;
import cn.leature.istarbaby.utils.ConstantDef;

public class ChildAlbumActivity extends FragmentActivity implements
		OnPostProcessListener, OnClickListener
{

	private static final int ALBUM_LIST_DONE = 1;
	private static final int ALBUM_LIST_ERROR = -1;
	private TabHost mTabHost;
	private TextView titleBarTitle;
	private ImageButton titleBarCancel, titleBarPost;
	private ViewPager mViewPager;
	private ChildAlbumAdapter mTabsAdapter;
	private HttpPostUtil httpUtil;
	private Intent mIntent;
	private Bundle mBundle;
	private LzProgressDialog mProgressDialog;

	private boolean isRefreshData = false;
	private String tabpage;

	// private boolean isAlbumModified = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_child_album);

		setCustomTitleBar();

		httpUtil = HttpPostUtil.getInstance();

		mProgressDialog = new LzProgressDialog(this);
		mProgressDialog.setCancelable(true);
		mIntent = this.getIntent();
		mBundle = this.getIntent().getExtras();

		getDefaultData();
	}

	private void getDefaultData()
	{
		httpUtil.setOnPostProcessListener(this);

		mProgressDialog.show();

		Map<String, String> param = new HashMap<String, String>();
		param.put("UserId", LoginInfo.getLoginUserId(this));
		param.put("ChildId", mBundle.getString(ConstantDef.ccChildId));

		httpUtil.sendPostMessage(ConstantDef.cUrlAlbumList, param);
	}

	public void setCustomTitleBar()
	{
		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("宝宝秀场");
		titleBarCancel = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarCancel.setBackgroundResource(R.drawable.selector_hd_bk);

		titleBarCancel.setOnClickListener(this);
		titleBarPost = (ImageButton) this.findViewById(R.id.titlebar_rightbtn1);
		titleBarPost.setVisibility(View.INVISIBLE);
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

	private void toErrorMessage(String msgString)
	{
		httpUtil.toErrorMessage(this, msgString, mProgressDialog);
	}

	protected void toListDailyDone(String listResult)

	{
		mProgressDialog.dismiss();

		ArrayList<ChildAlbumInfo> listData1 = new ArrayList<ChildAlbumInfo>();
		ArrayList<ChildAlbumInfo> listData2 = new ArrayList<ChildAlbumInfo>();

		Bundle bundle1 = new Bundle();
		Bundle bundle2 = new Bundle();
		try
		{
			JSONArray jsonArray = new JSONArray(listResult);
			for (int i = 0; i < jsonArray.length(); i++)
			{
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				ChildAlbumInfo tabinfo = new ChildAlbumInfo(jsonObject);

				if ("1".equals(tabinfo.group_id))
				{
					listData1.add(tabinfo);
					bundle1.putSerializable("info", listData1);

					bundle1.putString("page", "1");

				}
				if ("2".equals(tabinfo.group_id))
				{
					listData2.add(tabinfo);
					bundle2.putSerializable("info", listData2);
					bundle2.putString("page", "2");
				}

			}

		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		View tabview1 = getLayoutInflater()
				.inflate(R.layout.tab_selector, null);
		ImageView image1 = (ImageView) tabview1.findViewById(R.id.select_iamge);
		image1.setBackgroundResource(R.drawable.tab_start);

		View tabview2 = getLayoutInflater()
				.inflate(R.layout.tab_selector, null);
		ImageView image2 = (ImageView) tabview2.findViewById(R.id.select_iamge);
		image2.setBackgroundResource(R.drawable.tab_edit);

		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mTabsAdapter = new ChildAlbumAdapter(this, mTabHost, mViewPager);
		if (isRefreshData)
		{
			mTabsAdapter.setIsreFresh();
		}
		mTabsAdapter.addTab(mTabHost.newTabSpec("simple")
				.setIndicator(tabview1), ChildAlbumTabsFragment.class, bundle1);

		mTabsAdapter.addTab(
				mTabHost.newTabSpec("contacts").setIndicator(tabview2),
				ChildAlbumTabsFragment.class, bundle2);
		if (isRefreshData)
		{
			if (tabpage.equals("CurrentTab1"))
			{
				mTabHost.setCurrentTab(0);
			}
			else
			{
				mTabHost.setCurrentTab(0);
				mTabHost.setCurrentTab(1);
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{

			backPrePage();
		}
		return true;
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{

		case R.id.titlebar_leftbtn:

			backPrePage();
			break;

		default:
			break;
		}
	}

	private void backPrePage()
	{
		// 取消时候，直接返回主页面
		if (mProgressDialog != null)
		{
			mProgressDialog.dismiss();
		}

		if (isRefreshData)
		{
			// 信息被修改过，需要返回更新数据
			this.setResult(ConstantDef.RESULT_CODE_ALBUM_EDIT, mIntent);
		}
		else
		{
			this.setResult(Activity.RESULT_CANCELED, mIntent);
		}

		this.finish();

		// 设定退出动画
		this.overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_right);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{

		super.onActivityResult(requestCode, resultCode, data);

		if ((resultCode == Activity.RESULT_OK)
				|| (resultCode == ConstantDef.RESULT_CODE_DAILY_EDIT))
		{
			// 重新加载
			Bundle bundle = data.getExtras();
			tabpage = bundle.getString("tabpage");
			isRefreshData = true;
			getDefaultData();

		}
	}
}
