package cn.leature.istarbaby.daily;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.domain.ScreenInfo;
import cn.leature.istarbaby.network.HttpClientUtil;
import cn.leature.istarbaby.network.HttpGetUtil;
import cn.leature.istarbaby.network.HttpPostUtil;
import cn.leature.istarbaby.network.HttpGetUtil.RequestGetDoneCallback;
import cn.leature.istarbaby.network.HttpPostUtil.OnPostProcessListener;
import cn.leature.istarbaby.selecttime.JudgeDate;
import cn.leature.istarbaby.selecttime.WheelMain;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.DateUtilsDef;
import cn.leature.istarbaby.utils.DensityUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class DailyPreunEditActivity extends Activity implements
		OnClickListener, OnPostProcessListener {
	// TitleBar的控件
	private TextView titleBarTitle;
	private ImageButton titleBarBack, titleBarEdit;

	private Intent mIntent;
	private Bundle mBundle;
	// 页面内容控件
	private LinearLayout mPpremunLayout;
	private TextView mPremunUserName, mPremunChildName, mPremunChildAge;;
	private ImageView mPremunUsericon;

	private DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	boolean isRefreshData = false;
	private boolean openWindow = false;
	private View mInculd_layout;
	private PopupWindow mPopupWindow;
	private Button mQueding, mQuxiao;
	private Dialog mDeleteDialog;
	private View mpremun_ChildDetail;
	// private LzProgressDialog mProgressDialog;
	// 文章发表成功（Message用）
	protected static final int POST_DAILY_DONE = 4;
	// 文章发表出错（Message用）
	protected static final int POST_DAILY_ERROR = -2;
	// 时间选择
	private DateFormat mDdateFormat = new SimpleDateFormat("yyyy/MM/dd");
	private Button mCancelBtn;
	private Button mConfirmBtn;
	private WheelMain mWheelMain;
	private Dialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preun_edit);

		mIntent = this.getIntent();
		mBundle = mIntent.getExtras();

		initUI();
		// mProgressDialog = new LzProgressDialog(this);
		// mProgressDialog.setCancelable(false);
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.nophoto)
				.showImageForEmptyUri(R.drawable.nophoto)
				.showImageOnFail(R.drawable.nophoto).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();

		// 设置titlebar
		setCustomTitleBar();
	}

	private void initUI() {
		mpremun_ChildDetail = findViewById(R.id.premun_childdetail);

		mpremun_ChildDetail.setOnClickListener(this);

		mPremunChildName = (TextView) findViewById(R.id.premun_childname);
		mPremunChildAge = (TextView) findViewById(R.id.premun_childage);
		mPpremunLayout = (LinearLayout) findViewById(R.id.child_premun_layout);
		mPremunUserName = (TextView) findViewById(R.id.premunedit_name);
		mPremunUsericon = (ImageView) findViewById(R.id.premunedit_userIcon);
		// // 宝宝接种数据显示
		mPremunChildName.setText(mBundle.getString("childName"));
		mPremunChildAge.setText(DateUtilsDef.getAgeWithBirthday(mBundle
				.getString("childBirthDay")));
		mPremunUserName.setText(mBundle.getString("userName"));
		// 头像
		String userIcon = mBundle.getString("iconPath");
		showImageWithPath(mPremunUsericon, userIcon);

		// 接种的类型（任意的还是定期）、接种名字
		ArrayList<String> mData = new ArrayList<String>();
		ArrayList<String> mDataName = new ArrayList<String>();
		String detail = mBundle.getString("childDetail");
		if (!"".equals(detail)) {

			String[] DetailSplit = detail.split("\\|");
			for (int i = 0; i < DetailSplit.length; i++) {
				String Inoculate_times = DetailSplit[i];
				String type = Inoculate_times.substring(0,
						Inoculate_times.length()
								- (Inoculate_times.length() - 1));
				mData.add(type);
				mDataName.add(DetailSplit[i = i + 1]);
			}
			for (int i = 0; i < mData.size(); i++) {
				TextView premuntype = new TextView(this);
				String typeitem = mData.get(i);
				if ("0".equals(typeitem)) {
					premuntype.setBackgroundResource(R.drawable.lab_teiki);

				} else {

					premuntype.setBackgroundResource(R.drawable.lab_nini);

				}
				TextView divImge = new TextView(this);
				divImge.setBackgroundResource(R.drawable.timeline_data_div);
				TextView preName = new TextView(this);
				preName.setTextColor(getResources().getColor(R.color.istar_h));
				preName.setTextSize(18);
				preName.setText(mDataName.get(i));
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);

				lp.setMargins(0, 0, 4, 0);
				premuntype.setLayoutParams(lp);
				preName.setLayoutParams(lp);
				mPpremunLayout.addView(premuntype);
				mPpremunLayout.addView(preName);
				if (i < mData.size() - 1) {
					divImge.setLayoutParams(lp);
					mPpremunLayout.addView(divImge);
				}
			}
		}

	}

	private void showImageWithPath(ImageView imageView, String imgPath) {
		if ((null == imageView) || (null == imgPath) || (imgPath.length() == 0)) {
			return;
		}

		// 显示图片
		imageLoader.displayImage(HttpClientUtil.SERVER_PATH + imgPath,
				imageView, options);
	}

	private void setCustomTitleBar() {

		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);

		titleBarBack = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarBack.setBackgroundResource(R.drawable.selector_hd_bk);
		titleBarBack.setOnClickListener(this);

		titleBarEdit = (ImageButton) this.findViewById(R.id.titlebar_rightbtn1);
		titleBarEdit.setBackgroundResource(R.drawable.selector_hd_edt);
		titleBarEdit.setOnClickListener(this);
		titleBarTitle.setText(DateUtilsDef.dateFormatString(
				mBundle.getString("date"), "yyyy/MM/dd"));
		mInculd_layout = findViewById(R.id.include_layout);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backPrePage();
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_leftbtn:

			backPrePage();

			break;
		case R.id.titlebar_rightbtn1:
			if (!openWindow) {
				openWindow = !openWindow;
				getpopunwindow();

			} else {
				openWindow = !openWindow;
				mPopupWindow.dismiss();
			}

			break;
		case R.id.delete_queding:
			// 删除日志
			Map<String, String> param = new HashMap<String, String>();
			param.put("UserId",
					LoginInfo.getLoginUserId(DailyPreunEditActivity.this));
			param.put("Daily_Id", mBundle.getString(ConstantDef.ccDaily_Id));

			HttpGetUtil httpGetUtil = new HttpGetUtil(
					new RequestGetDoneCallback() {

						@Override
						public void requestWithGetDone(String result) {
							mPopupWindow.dismiss();
							mDeleteDialog.dismiss();
							if ((result == null) || (result.equals("0"))) {
								// 处理出错
								return;
							}
							// 从发表新文章页面返回时，重新加载
							reloadDailyList();
						}
					});
			httpGetUtil.execute(ConstantDef.cUrlDailyDelete, param);
			break;
		case R.id.delete_quxiao:

			// mProgressDialog.dismiss();
			mPopupWindow.dismiss();
			mDeleteDialog.dismiss();

			break;
		case R.id.premun_childdetail:
			if (mPopupWindow != null) {
				openWindow = false;
				mPopupWindow.dismiss();
			}

			break;
		default:
			break;
		}

	}

	private void backPrePage() {
		if (mPopupWindow != null) {
			openWindow = false;
			mPopupWindow.dismiss();
		}
		// 信息被修改过，需要返回更新数据
		if (isRefreshData) {

			this.setResult(ConstantDef.RESULT_CODE_DAILY_EDIT, mIntent);
		} else {
			this.setResult(Activity.RESULT_CANCELED, mIntent);
		}
		this.finish();
		// 设定退出动画
		this.overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_right);
	}

	private void getpopunwindow() {
		// 编辑文章
		LayoutInflater LayoutInflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View popunwindwow = LayoutInflater.inflate(
				R.layout.dailydetail_edit_item, null);
		int Height = mInculd_layout.getHeight();
		mPopupWindow = new PopupWindow(popunwindwow, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		mPopupWindow.showAtLocation(findViewById(R.id.premun_childdetail),
				Gravity.RIGHT | Gravity.TOP, 0,
				Height + DensityUtil.dip2px(DailyPreunEditActivity.this, 20));

		popunwindwow.findViewById(R.id.item1).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						openWindow = !openWindow;

						getTextDate();

					}

				});

		popunwindwow.findViewById(R.id.item2).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						openWindow = !openWindow;

						dialog();

					}
				});
		popunwindwow.findViewById(R.id.item3).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						openWindow = !openWindow;
						mPopupWindow.dismiss();
					}
				});
	}

	private void PostNewDateTime() {
		Map<String, String> param = new HashMap<String, String>();
		param.put("UserId",
				LoginInfo.getLoginUserId(DailyPreunEditActivity.this));
		param.put("Daily_Id", mBundle.getString(ConstantDef.ccDaily_Id));
		param.put("Detail", mBundle.getString("childDetail"));

		// 更改时间
		param.put("EventDate", titleBarTitle.getText().toString().trim()
				.replaceAll("/", ""));
		Log.e("date", "date" + titleBarTitle.getText().toString().trim());
		Log.e("childDetail", "childDetail" + mBundle.getString("childDetail"));
		Log.e("Daily_Id",
				"Daily_Id" + mBundle.getString(ConstantDef.ccDaily_Id));

		// mProgressDialog.show();

		HttpPostUtil httpUtil = HttpPostUtil.getInstance();
		httpUtil.setOnPostProcessListener(this);
		httpUtil.sendPostMessage(ConstantDef.cUrlDailyEdit, param);
	}

	protected void reloadDailyList() {

		// 从发表新文章页面返回时，重新加载
		this.setResult(Activity.RESULT_OK, mIntent);
		this.finish();

		// 设定退出动画
		overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_right);
	}

	@Override
	public void onPostDone(int responseCode, String responseMessage) {
		Message msg = Message.obtain();
		msg.arg1 = responseCode;
		msg.obj = responseMessage;

		if (responseCode == HttpPostUtil.POST_SUCCESS_CODE) {
			msg.what = POST_DAILY_DONE;
		} else {
			msg.what = POST_DAILY_ERROR;
		}
		handler.sendMessage(msg);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case POST_DAILY_DONE:
				toSaveDate(msg.obj.toString());
				break;
			default:
				// 错误处理
				toErrorMessage(msg.obj.toString());
				break;
			}

			super.handleMessage(msg);
		}

	};

	private void toSaveDate(String result) {
		mPopupWindow.dismiss();
		mDialog.dismiss();
		if ((result == null) || (result.equals("0"))) {
			// 处理出错
			return;
		}

		Toast.makeText(this, "修改成功", Toast.LENGTH_LONG).show();
		// 从发表新文章页面返回时，重新加载
		this.setResult(Activity.RESULT_OK, mIntent);
		this.finish();

		// 设定退出动画
		overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_right);
	}

	private void dialog() {

		View layout = getLayoutInflater().inflate(R.layout.delete_dialog, null);
		mQueding = (Button) layout.findViewById(R.id.delete_queding);
		mQuxiao = (Button) layout.findViewById(R.id.delete_quxiao);
		mQuxiao.setOnClickListener(this);
		mQueding.setOnClickListener(this);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		mDeleteDialog = builder.create();
		mDeleteDialog.show();
		mDeleteDialog.setContentView(layout);
	}

	private void toErrorMessage(String msgString) {
		// mProgressDialog.dismiss();
		Toast.makeText(this, msgString, Toast.LENGTH_LONG).show();
	}

	public void getTextDate() {
		LayoutInflater inflater = LayoutInflater.from(this);
		final View timepickerview = inflater.inflate(R.layout.timepicker, null);
		ScreenInfo screenInfo = new ScreenInfo((Activity) this);
		mWheelMain = new WheelMain(timepickerview);
		mWheelMain.screenheight = screenInfo.getHeight();
		String time = titleBarTitle.getText().toString();
		Calendar calendar = Calendar.getInstance();

		if (JudgeDate.isDate(time, "yyyy/MM/dd")) {
			try {

				calendar.setTime(mDdateFormat.parse(time));

			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		mWheelMain.initDateTimePicker(year, month, day);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setCancelable(false);
		mDialog = builder.create();
		mDialog.show();
		mDialog.setContentView(timepickerview);

		mCancelBtn = (Button) timepickerview.findViewById(R.id.cancel);
		mConfirmBtn = (Button) timepickerview.findViewById(R.id.confirm);
		mCancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mPopupWindow.dismiss();
				mDialog.dismiss();

			}
		});

		mConfirmBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
				titleBarTitle.setText(WheelMain.getalltime());
				PostNewDateTime();

			}
		});
	}

}
