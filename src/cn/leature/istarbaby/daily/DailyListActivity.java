package cn.leature.istarbaby.daily;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.child.ChildDetailActivity;
import cn.leature.istarbaby.common.LzBaseActivity;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.domain.ChildrenInfo;
import cn.leature.istarbaby.domain.DailyDetailInfo;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.domain.UserInfo;
import cn.leature.istarbaby.domain.DailyDetailInfo.ChildDataInfo;
import cn.leature.istarbaby.network.HttpGetUtil;
import cn.leature.istarbaby.network.HttpGetUtil.RequestGetDoneCallback;
import cn.leature.istarbaby.network.HttpPostUtil;
import cn.leature.istarbaby.network.HttpPostUtil.OnPostProcessListener;
import cn.leature.istarbaby.slidingmenu.SlidingMenu;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.view.XListView;
import cn.leature.istarbaby.view.XListView.IXListViewListener;

public class DailyListActivity extends LzBaseActivity implements
		OnPostProcessListener, OnClickListener, OnItemClickListener,
		IXListViewListener
{

	// 用户登录信息
	private UserInfo mLoginUserInfo;
	private List<ChildrenInfo> mChildrenList;

	private HttpPostUtil httpUtil;
	private DailyListViewAdapter mAdapter;
	// 屏幕
	private int mWidth;
	private int mHeight;
	private int page = 1;
	// 表示数据源
	private ArrayList<Object> listData;
	private XListView dailyListView;
	private LzProgressDialog mProgressDialog;
	private SlidingMenu mSlidingMenu;
	// 文章列表取得成功（Message用）
	protected static final int DAILY_LIST_DONE = 2;
	// 文章列表出错（Message用）
	protected static final int DAILY_LIST_ERROR = -1;
	private Bundle mBundle;
	private boolean isListRefreshing = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sliding_main);

//		// 检查用户登录状况
		checkUserLogin();
		initUIView();
//
		mBundle = getIntent().getExtras();

		getDailyList();
	}

	private void checkUserLogin()
	{
		// 取得用户登录信息
		mLoginUserInfo = LoginInfo.loadLoginUserInfo(this);
		if (mLoginUserInfo == null)
		{
			return;
		}

		// 宝宝列表
		mChildrenList = mLoginUserInfo.getChildList();

		// 用户名或密码为空，需要重新登录
		if (mLoginUserInfo.getUserSId().equals("")
				|| mLoginUserInfo.getPassword().equals(""))
		{

		}
	}

	private Handler handler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case DAILY_LIST_DONE:
				toListDailyDone(msg.obj.toString());
				break;
			default :
				// 错误处理
				toErrorMessage(msg.obj.toString());
				break;
			}

			super.handleMessage(msg);
		}
	};

	private void initUIView()
	{

		// 设置主菜单
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		mSlidingMenu = (SlidingMenu) findViewById(R.id.slidingMain);

		View leftView = getLayoutInflater()
				.inflate(R.layout.sliding_menu, null);
		View centerView = getLayoutInflater().inflate(
				R.layout.activity_daily_list, null);

		mSlidingMenu.setLeftView(leftView);
		mSlidingMenu.setCenterView(centerView);

		// 设置顶部导航
		TextView titleBarTitle = (TextView) this
				.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("成长日记");

		ImageButton showLeftMenu = (ImageButton) centerView
				.findViewById(R.id.titlebar_leftbtn);
		showLeftMenu.setBackgroundResource(R.drawable.selector_hd_menu);
		showLeftMenu.setOnClickListener(this);

		ImageButton titleBarPost = (ImageButton) this
				.findViewById(R.id.titlebar_rightbtn1);
		titleBarPost.setBackgroundResource(R.drawable.selector_hd_ety);
		titleBarPost.setOnClickListener(this);

		dailyListView = (XListView) this.findViewById(R.id.xListView1);
		dailyListView.setOnItemClickListener(this);
		dailyListView.setPullLoadEnable(true);
		dailyListView.setXListViewListener(this);
		mProgressDialog = new LzProgressDialog(this);

		// 屏幕宽高
		mWidth = dm.widthPixels;
		mHeight = dm.heightPixels;
	}

	// 关闭刷新控件
	private void closeListRefresh()
	{

		dailyListView.stopRefresh();
		dailyListView.stopLoadMore();

		// 显示更新时间
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		dailyListView.setRefreshTime(df.format(calendar.getTime()));
	}

	// 下拉刷新最新20条数据
	@Override
	public void onRefresh()
	{
		if (isListRefreshing)
		{
			// 禁止重复刷新
			return;
		}

		// 刷新：取得第一页
		isListRefreshing = true;
		page = 1;
		loadListData();
	}

	// 上拉刷新更多数据
	@Override
	public void onLoadMore()
	{
		if (isListRefreshing)
		{
			// 禁止重复刷新
			return;
		}

		isListRefreshing = true;
		page++;
		loadListData();
	}

	protected void toErrorMessage(String msgString)
	{
		// List取得出错，显示提示信息
		mProgressDialog.dismiss();
		Toast.makeText(this, msgString, Toast.LENGTH_LONG).show();
	}

	private void getDailyList()
	{
		httpUtil = HttpPostUtil.getInstance();
		httpUtil.setOnPostProcessListener(this);

		if (mBundle != null)
		{
			String back = mBundle.getString("Not");
			if ("-1".equals(back))
			{

			}
			else
			{

				mProgressDialog.setMessage("正在刷新列表...");
				mProgressDialog.setCancelable(true);
				mProgressDialog.show();
			}
		}
		else
		{

			mProgressDialog.setMessage("正在取得列表...");
			mProgressDialog.setCancelable(true);
			mProgressDialog.show();
		}

		// 取得日志第一页
		page = 1;
		loadListData();
	}

	protected void toListDailyDone(String listResult)
	{
		if (page == 1)
		{
			// 第一页：重新生成
			listData = new ArrayList<Object>();
		}

		try
		{
			JSONArray jsonArray = new JSONArray(listResult);
			for (int i = 0; i < jsonArray.length(); i++)
			{
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				DailyDetailInfo detailInfo = new DailyDetailInfo(jsonObject);
				// 设置标题栏
				listData.add(detailInfo.getEventDate());
				// 日记内容
				listData.add(detailInfo);
			}

			// 取得件数不足20件，无需显示 更多
			if (jsonArray.length() < 20)
			{
				dailyListView.setgonefooter(true);
			}
			else
			{
				dailyListView.setgonefooter(false);
			}
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mProgressDialog.dismiss();
		// 显示内容
		if (page == 1)
		{
			mAdapter = new DailyListViewAdapter(this, 0, listData, mWidth,
					mHeight, mSlidingMenu);
			dailyListView.setAdapter(mAdapter);
		}
		else
		{
			mAdapter.notifyDataSetChanged();
		}

		// 关闭刷新栏
		if (isListRefreshing)
		{
			isListRefreshing = false;
			closeListRefresh();
		}
	}

	@Override
	public void onPostDone(int responseCode, String responseMessage)
	{

		Message msg = Message.obtain();
		msg.arg1 = responseCode;
		msg.obj = responseMessage;

		if (responseCode == HttpPostUtil.POST_SUCCESS_CODE)
		{
			msg.what = DAILY_LIST_DONE;
		}
		else
		{
			msg.what = DAILY_LIST_ERROR;
		}
		handler.sendMessage(msg);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.titlebar_leftbtn:
			// 打开主菜单（从左边划出）
			mSlidingMenu.showLeftView();
			break;
		case R.id.titlebar_rightbtn1:
			// 发表新文章
			postNewDaily();
			break;
		default:
			break;
		}
	}

	private void postNewDaily()
	{
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("Channel", "list");
		intent.putExtras(bundle);
		intent.setClass(DailyListActivity.this, DailyPostActivity.class);
		startActivityForResult(intent, ConstantDef.REQUEST_CODE_DAILY_POST);

		this.overridePendingTransition(R.anim.activity_in_from_bottom,
				R.anim.activity_nothing_in_out);
	}

	/**
	 * @Title: onItemClick
	 * @Description: TODO
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView,
	 *      android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3)
	{

		Intent intent = new Intent();
		// 传递参数：Image URL
		Bundle bundle = new Bundle();

		DailyDetailInfo detailInfo = (DailyDetailInfo) listData
				.get(position - 1);

		if ("99".equals(detailInfo.getTagName()))
		{
			// 新生宝宝
			intent.setClass(this, ChildDetailActivity.class);

			// 从宝宝列表中取得宝宝排行
			String dailyId = detailInfo.getDailyId();
			String childNo = "1";
			for (int i = 0; i < mChildrenList.size(); i++)
			{
				if (dailyId.equals(mChildrenList.get(i).getDaily_id()))
				{
					// 宝宝排行取得
					childNo = mChildrenList.get(i).getChild_no();
					break;
				}
			}
			// 设定参数
			bundle.putString(ConstantDef.ccChildNo, childNo);
		}
		else if ("96".equals(detailInfo.getTagName()))
		{
			intent.setClass(this, DailyPreunEditActivity.class);

			bundle.putString(ConstantDef.ccDaily_Id, detailInfo.getDailyId());
			bundle.putString("childDetail",detailInfo.getDetail());
			bundle.putString("date", detailInfo.getEventDate());
			bundle.putString("userName", detailInfo.getUserName());
			bundle.putString("iconPath", detailInfo.getUserIcon());
			ChildDataInfo childInfo = detailInfo.getChildDataInfo();
			bundle.putString("childName", childInfo.getChidlName());
			bundle.putString("childBirthDay", childInfo.getBirthDay());

			
		}
		else
		{
			intent.setClass(this, DailyDetailActivity.class);
			bundle.putString("Channel", "list");
			bundle.putString(ConstantDef.ccDaily_Id, detailInfo.getDailyId());
		}

		intent.putExtras(bundle);
		startActivityForResult(intent, ConstantDef.REQUEST_CODE_DAILY_DETAIL);

		// 设定启动动画
		this.overridePendingTransition(R.anim.activity_in_from_right,
				R.anim.activity_nothing_in_out);
	}

	private void loadListData()
	{

		// 日志List取得的参数设定
		Map<String, String> param = new HashMap<String, String>();
		param.put("UserId", mLoginUserInfo.getUserSId());

		// PageNo取得改变后的page
		param.put("PageNo", "" + page);

		HttpGetUtil httpGetUtil = new HttpGetUtil(new RequestGetDoneCallback()
		{

			@Override
			public void requestWithGetDone(String result)
			{

				// 刷新提示
				Message msg = Message.obtain();

				if (result == null || result.length() == 0)
				{
					// 取得失败
					msg.obj = "网络异常，请确定您当前网络。";
					msg.arg1 = 0;
					msg.what = DAILY_LIST_ERROR;
				}
				else
				{
					msg.obj = result;
					msg.arg1 = 1;
					msg.what = DAILY_LIST_DONE;
				}
				handler.sendMessage(msg);
			}
		});
		httpGetUtil.execute(ConstantDef.cUrlDailyList, param);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if ((resultCode == Activity.RESULT_OK)
				|| (resultCode == ConstantDef.RESULT_CODE_DAILY_EDIT)
				|| (resultCode == ConstantDef.RESULT_CODE_CHILD_EDIT))
		{
			// 重新加载
			getDailyList();
			mSlidingMenu.reloadAdapterData();
		}
	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();

		// 关闭刷新栏
		if (isListRefreshing)
		{
			isListRefreshing = false;
			closeListRefresh();
		}
	}

}
