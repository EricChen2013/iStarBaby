package cn.leature.istarbaby.questions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzBaseActivity;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.domain.ChildrenInfo;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.domain.QuestionDetailInfo;
import cn.leature.istarbaby.domain.UserInfo;
import cn.leature.istarbaby.domain.QuestionDetailInfo.QuestionListModel;
import cn.leature.istarbaby.login.LoginActivity;
import cn.leature.istarbaby.network.HttpPostUtil;
import cn.leature.istarbaby.network.HttpPostUtil.OnPostProcessListener;
import cn.leature.istarbaby.slidingmenu.SlidingMenu;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.DateUtilsDef;

public class QuestionListActivity extends LzBaseActivity implements
		OnPostProcessListener, OnClickListener, OnItemClickListener
{

	// 表示数据对象模型
	private QuestionDetailInfo mQuestionDetailInfo;

	private HttpPostUtil httpUtil;
	private SlidingMenu mSlidingMenu;

	private LzProgressDialog progressDialog;

	// 列表取得成功（Message用）
	protected static final int QUESTION_LIST_DONE = 1;
	// 列表取得出错（Message用）
	protected static final int QUESTION_LIST_ERROR = -1;
	public static ImageButton showLeftMenu;
	// 识别是不是第一页 ，在别的fragment中修改
	public static boolean isFirstPage = true;
	private ListView listView;
	private List<String> listItems;
	private QuestionAdapter adapter;
	private UserInfo loginUserInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sliding_main);

		// 查询数据库
		checkUserLogin();
		initView();

		getQuestionList();

	}

	private void checkUserLogin()
	{
		loginUserInfo = LoginInfo.loadLoginUserInfo(this);

		// 用户名或密码为空，需要重新登录
		if (loginUserInfo.getUserSId().equals("")
				|| loginUserInfo.getPassword().equals(""))
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
			case QUESTION_LIST_DONE:
				toQuestionListDone(msg.obj.toString());
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
		// List取得出错，显示提示信息
		progressDialog.dismiss();
		Toast.makeText(this, msgString, Toast.LENGTH_LONG).show();
	}

	private void initView()
	{
		// 设置主菜单
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		mSlidingMenu = (SlidingMenu) findViewById(R.id.slidingMain);

		View leftView = getLayoutInflater()
				.inflate(R.layout.sliding_menu, null);
		View centerView = getLayoutInflater().inflate(
				R.layout.activity_question_list, null);

		mSlidingMenu.setLeftView(leftView);
		mSlidingMenu.setCenterView(centerView);

		// 设置顶部导航
		TextView titleBarTitle = (TextView) this
				.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("育儿百科");

		showLeftMenu = (ImageButton) centerView
				.findViewById(R.id.titlebar_leftbtn);
		showLeftMenu.setBackgroundResource(R.drawable.selector_hd_menu);
		showLeftMenu.setOnClickListener(this);

		listView = (ListView) centerView.findViewById(R.id.question_listview);
		listView.setOnItemClickListener(this);
		ImageButton titleBarPost = (ImageButton) this
				.findViewById(R.id.titlebar_rightbtn1);
		titleBarPost.setVisibility(View.INVISIBLE);

		// 进度条
		progressDialog = new LzProgressDialog(this);

		httpUtil = HttpPostUtil.getInstance();
		httpUtil.setOnPostProcessListener(this);
	}

	private void getQuestionList()
	{

		progressDialog.setCancelable(true);
		progressDialog.show();

		// 宝典List取得
		httpUtil.sendPostMessage(ConstantDef.cUrlQuestionList, null);
	}

	protected void toQuestionListDone(String listResult)
	{
		progressDialog.dismiss();

		try
		{
			JSONObject jsonObject = new JSONObject(listResult);
			mQuestionDetailInfo = new QuestionDetailInfo(jsonObject);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		listItems = new ArrayList<String>();
		// 设置标题
		listItems.add(mQuestionDetailInfo.getTitle());

		List<QuestionListModel> listData = mQuestionDetailInfo.getList();
		// 设置内容
		for (QuestionListModel listModel : listData)
		{
			listItems.add(listModel.getCatalog1_text());
		}

		List<ChildrenInfo> mChildList = loginUserInfo.getChildList();
		String minAgeday = "";
		ArrayList<String> mAgedayList = new ArrayList<String>();
		for (int i = 0; i < mChildList.size(); i++)
		{

			String birthday = mChildList.get(i).getBirthday();
			if ("".equals(minAgeday))
			{
				minAgeday = birthday;

			}
			else if (minAgeday.compareTo(birthday) < 0)
			{
				minAgeday = birthday;
			}

		}

		int getindex = getindex(DateUtilsDef
				.calculateAgeWithBirthday(minAgeday));

		adapter = new QuestionAdapter(QuestionListActivity.this, 0);
		adapter.setListItems(listItems);
		adapter.setbirthday(getindex);
		listView.setAdapter(adapter);
	}

	private int getindex(int birthday)

	{

		int index = 0;

		if (birthday <= 7)
		{
			index = 1;
		}
		else if (birthday <= 15)
		{
			index = 2;
		}
		else if (birthday <= 30)
		{
			index = 3;
		}
		else if (birthday <= 60)
		{
			index = 4;
		}
		else if (birthday <= 90)
		{
			index = 5;
		}
		else if (birthday <= 120)
		{
			index = 6;
		}
		else if (birthday <= 150)
		{
			index = 7;
		}
		else if (birthday <= 180)
		{
			index = 8;
		}
		else if (birthday <= 210)
		{
			index = 9;
		}
		else if (birthday <= 240)
		{
			index = 10;
		}
		else if (birthday <= 270)
		{
			index = 11;
		}
		else if (birthday <= 300)
		{
			index = 12;
		}
		else if (birthday <= 330)
		{
			index = 13;
		}
		else if (birthday <= 365)
		{
			index = 14;
		}
		else if (birthday <= 547)
		{
			index = 15;
		}
		else if (birthday <= 730)
		{
			index = 16;
		}

		return index;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		return true;
	}

	@Override
	public void onPostDone(int responseCode, String responseMessage)
	{

		Message msg = Message.obtain();
		msg.arg1 = responseCode;
		msg.obj = responseMessage;

		if (responseCode == HttpPostUtil.POST_SUCCESS_CODE)
		{
			msg.what = QUESTION_LIST_DONE;
		}
		else
		{
			msg.what = QUESTION_LIST_ERROR;
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
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id)
	{
		Intent intent = new Intent();
		intent.putExtra("Info", mQuestionDetailInfo);

		Bundle bundle = new Bundle();
		bundle.putInt("list_position", position);
		intent.putExtras(bundle);

		intent.setClass(QuestionListActivity.this,
				QuestionCatalogActivity.class);
		this.startActivityForResult(intent, ConstantDef.REQUEST_CODE_QA_CATALOG);

		// 设定启动动画
		this.overridePendingTransition(R.anim.activity_in_from_right,
				R.anim.activity_nothing_in_out);
	}
}
