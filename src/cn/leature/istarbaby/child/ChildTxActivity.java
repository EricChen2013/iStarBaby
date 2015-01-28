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
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.daily.DailyDetailActivity;
import cn.leature.istarbaby.daily.DailyPostActivity;
import cn.leature.istarbaby.domain.ChildGrowInfo;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.network.HttpGetUtil;
import cn.leature.istarbaby.network.HttpGetUtil.RequestGetDoneCallback;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.DateUtilsDef;
import cn.leature.istarbaby.utils.DensityUtil;

public class ChildTxActivity extends Activity implements OnClickListener,
		OnItemClickListener
{

	private TextView titleBarTitle;
	private ImageButton titleBarBack;
	private ImageButton titleBarEdit;
	private LzProgressDialog mProgressDialog;
	private Intent mIntent;
	private Bundle mBundle;
	private ListView mListview;
	private ArrayList<ChildGrowInfo> mDataTx;
	private ChildDataAdapter mAdapter;

	private int mAgeWidth;
	private int mDataWidth;
	private boolean isRefreshData;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_child_tx);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mAgeWidth = (dm.widthPixels - DensityUtil.dip2px(this, 80)) / 3;
		mDataWidth = (dm.widthPixels - mAgeWidth - DensityUtil
				.dip2px(this, 150)) / 2;

		setCustomTitleBar();
		inItUi();

		mIntent = this.getIntent();
		mBundle = mIntent.getExtras();
		// 显示名字
		if (null != mBundle) {
			titleBarTitle.setText(mBundle.getString(ConstantDef.ccChildName));			
		}
		
		mProgressDialog = new LzProgressDialog(this);
		mProgressDialog.setCancelable(true);

		showView();
	}

	private void showView()
	{
		mProgressDialog.show();

		mDataTx = new ArrayList<ChildGrowInfo>();
		Map<String, String> param = new HashMap<String, String>();
		// 登录用户的ID
		param.put("UserId", LoginInfo.getLoginUserId(this));
		param.put("ChildId", mBundle.getString(ConstantDef.ccChildId));

		HttpGetUtil httpGetUtil = new HttpGetUtil(new RequestGetDoneCallback()
		{

			@Override
			public void requestWithGetDone(String result)
			{

				mProgressDialog.dismiss();

				if ((result == null) || (result.equals("0")))
				{
					// 处理出错
					return;
				}
				try
				{
					JSONArray jsonArray = new JSONArray(result);
					for (int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						ChildGrowInfo detailInfo = new ChildGrowInfo(jsonObject);

						mDataTx.add(detailInfo);
					}
				}
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				mAdapter = new ChildDataAdapter();
				mListview.setAdapter(mAdapter);
			}
		});
		httpGetUtil.execute(ConstantDef.cUrlChildGrowData, param);
	}

	private void inItUi()
	{
		mListview = (ListView) findViewById(R.id.tx_list);
		mAdapter = new ChildDataAdapter();
		mListview.setOnItemClickListener(this);
	}

	private class ChildDataAdapter extends BaseAdapter
	{

		@Override
		public int getCount()
		{
			return mDataTx.size();
		}

		@Override
		public Object getItem(int position)
		{
			return null;
		}

		@Override
		public long getItemId(int position)
		{
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ViewHolder holder;
			if (convertView == null)
			{
				convertView = getLayoutInflater().inflate(
						R.layout.child_tx_item, null);
				holder = new ViewHolder();
				if (position == 0)
				{
					convertView.setPadding(0, 15, 0, 0);
				}
				holder.eventDay = (TextView) convertView
						.findViewById(R.id.child_hc_eventday);

				holder.date = (TextView) convertView
						.findViewById(R.id.child_hc_date);

				holder.touWei = (TextView) convertView
						.findViewById(R.id.child_hc_touwei);
				holder.xiongWei = (TextView) convertView
						.findViewById(R.id.child_hc_xiongwei);
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}
			ChildGrowInfo childGrowInfo = mDataTx.get(position);

			holder.date.setText(DateUtilsDef.calAgeBetweenString(childGrowInfo
					.getBirthday(),
					childGrowInfo.getEvent_date().substring(0, 8)));

			holder.eventDay.setText(DateUtilsDef.dateFormatString(
					childGrowInfo.getEvent_date(), "yyyy/MM/dd"));

			holder.touWei.setText(childGrowInfo.getTouwei());
			titleBarTitle.setText(childGrowInfo.getChild_name());
			holder.xiongWei.setText(childGrowInfo.getXiongwei());

			// 设置位置
			LayoutParams param = (LayoutParams) holder.date.getLayoutParams();
			param.width = mAgeWidth;
			holder.date.setLayoutParams(param);

			LayoutParams param1 = (LayoutParams) holder.eventDay
					.getLayoutParams();
			param1.width = mAgeWidth;
			holder.eventDay.setLayoutParams(param1);

			LayoutParams param2 = (LayoutParams) holder.touWei
					.getLayoutParams();
			param2.width = mDataWidth;
			holder.touWei.setLayoutParams(param2);

			LayoutParams param3 = (LayoutParams) holder.xiongWei
					.getLayoutParams();
			param3.width = mDataWidth;
			holder.xiongWei.setLayoutParams(param3);

			return convertView;
		}

	}

	public class ViewHolder
	{
		TextView eventDay;

		TextView date;

		TextView touWei;

		TextView xiongWei;
	}

	private void setCustomTitleBar()
	{

		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);

		titleBarBack = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarBack.setOnClickListener((this));
		titleBarBack.setBackgroundResource(R.drawable.selector_hd_bk);

		titleBarEdit = (ImageButton) this.findViewById(R.id.titlebar_rightbtn1);
		titleBarEdit.setOnClickListener(this);
		titleBarEdit.setBackgroundResource(R.drawable.child_hw_right);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.child_hw, menu);
		return true;
	}

	private void backPrePage()
	{

		// 取消时候，直接返回主页面
		if (isRefreshData)
		{
			// 信息被修改过，需要返回更新数据
			this.setResult(ConstantDef.RESULT_CODE_CHILD_TXDATA, mIntent);
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
		case R.id.titlebar_rightbtn1:
			postGrowData();
			break;
		default:
			break;
		}
	}

	private void postGrowData()
	{
		Intent intent = new Intent();
		mBundle.putString("Channel", "hw");
		intent.putExtras(mBundle);
		intent.setClass(this, DailyPostActivity.class);
		startActivityForResult(intent, ConstantDef.REQUEST_CODE_DAILY_POST);

		this.overridePendingTransition(R.anim.activity_in_from_bottom,
				R.anim.activity_nothing_in_out);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{

		Intent intent = new Intent();
		intent.setClass(this, DailyDetailActivity.class);
		ChildGrowInfo childGrowInfo = mDataTx.get(arg2);
		mBundle.putString(ConstantDef.ccDaily_Id, childGrowInfo.getDaily_id());
		mBundle.putString("Channel", "hx");
		intent.putExtras(mBundle);

		startActivityForResult(intent, ConstantDef.REQUEST_CODE_DAILY_POST);
		// 设定启动动画
		this.overridePendingTransition(R.anim.activity_in_from_right,
				R.anim.activity_nothing_in_out);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if ((resultCode == Activity.RESULT_OK)
				|| (resultCode == ConstantDef.RESULT_CODE_DAILY_EDIT))
		{
			isRefreshData = true;
			// 重新加载
			showView();
		}
	}
}
