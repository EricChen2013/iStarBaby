package cn.leature.istarbaby.daily;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.child.EditTextListener;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.domain.DailyDetailInfo;
import cn.leature.istarbaby.domain.DailyDetailInfo.ChildDataInfo;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.domain.UserInfo;
import cn.leature.istarbaby.network.HttpGetUtil;
import cn.leature.istarbaby.network.HttpGetUtil.RequestGetDoneCallback;
import cn.leature.istarbaby.network.HttpPostUtil;
import cn.leature.istarbaby.network.HttpPostUtil.OnPostProcessListener;
import cn.leature.istarbaby.network.ImageDownloadTask;
import cn.leature.istarbaby.network.ImageDownloadTask.ImageDoneCallback;
import cn.leature.istarbaby.selecttime.DateTimePicker;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.DateUtilsDef;

public class DailyEditActivity extends Activity implements OnClickListener,
		OnPostProcessListener {

	// 用户登录信息
	private UserInfo loginUserInfo;

	// 使用相册中的图片（Activity跳转用）
	public static final int SELECT_PIC_BY_PICK_PHOTO = 1;

	// 图片上传成功（Message用）
	protected static final int UPLOAD_FILE_DONE = 3;
	// 图片上传出错（Message用）
	protected static final int UPLOAD_FILE_ERROR = -1;

	// 文章发表成功（Message用）
	protected static final int POST_DAILY_DONE = 4;
	// 文章发表出错（Message用）
	protected static final int POST_DAILY_ERROR = -2;

	// 年，月，日
	private int mYear, mMonth, mDay;

	// TitleBar的控件
	private TextView mTitleBarTitle, mShowText;
	private ImageButton mTitleBarCancel, mTitleBarPost;

	// 主画面的控件
	private EditText mDetailEditText; // 内容
	private ImageButton mUploadImageBtn;
	private EditText mPostDateTextView;
	private LzProgressDialog mProgressDialog;
	private Intent mIntent;
	private ImageView mShow_Image;
	private Bundle mBundle;
	private EditText mHeight, mWeight, mTouWei, mXiongWei;
	private View mLayouts;
	private TextView mLable;
	private String childId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_daily_post);

		mIntent = this.getIntent();

		// 检查用户登录状况
		checkUserLogin();

		inItUi();
		mBundle = mIntent.getExtras();
		mShowText = (TextView) findViewById(R.id.post_show_text);
		mShow_Image = (ImageView) findViewById(R.id.post_show_image);
		mLayouts = findViewById(R.id.layouts_hwtx);

		mProgressDialog = new LzProgressDialog(this);
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();

		Map<String, String> param = new HashMap<String, String>();
		// 登录用户的ID
		param.put("UserId", LoginInfo.getLoginUserId(this));

		param.put("Daily_Id", mBundle.getString(ConstantDef.ccDaily_Id));
		HttpGetUtil httpGetUtil = new HttpGetUtil(new RequestGetDoneCallback() {

			@Override
			public void requestWithGetDone(String result) {

				mProgressDialog.dismiss();

				if ((result == null) || (result.equals("0"))) {
					// 处理出错
					return;
				}

				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(result);
					DailyDetailInfo detailInfo = new DailyDetailInfo(jsonObject);
					mDetailEditText.setText(detailInfo.getDetail());
					mPostDateTextView.setText(DateUtilsDef.dateFormatString(
							detailInfo.getEventDate(), "yyyy/MM/dd"));

					ChildDataInfo childDataInfo = detailInfo.getChildDataInfo();
					mTouWei.setText(childDataInfo.getChildTouwei());
					mXiongWei.setText(childDataInfo.getChildXiongwei());
					mHeight.setText(childDataInfo.getChildHeight());
					mWeight.setText(childDataInfo.getChildWeight());
					childId = childDataInfo.getChildId();
					String tagName = detailInfo.getTagName();
					if ("".equals(tagName)) {
						// list列表
						mLayouts.setVisibility(View.GONE);
						mShow_Image.setVisibility(View.GONE);
						mShowText.setVisibility(View.GONE);
					} else if ("98".equals(tagName)) {
						// 成长记录
						mShow_Image.setVisibility(View.GONE);
						mShowText.setVisibility(View.GONE);
						mLayouts.setVisibility(View.VISIBLE);
						mLable.setText("成长记录");
					} else {
						// 相册
						mShow_Image.setVisibility(View.VISIBLE);
						mShowText.setVisibility(View.VISIBLE);
						mShowText.setText(detailInfo.getTagName());
						mLable.setText(detailInfo.getTagName());
						mLayouts.setVisibility(View.GONE);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		httpGetUtil.execute(ConstantDef.cUrlDailyDetail, param);
	}

	private void checkUserLogin() {
		// 取得用户登录信息
		loginUserInfo = LoginInfo.loadLoginUserInfo(this);

		// 用户名或密码为空，需要重新登录
		if (loginUserInfo.getUserSId().equals("")
				|| loginUserInfo.getPassword().equals("")) {

		}
	}

	private void inItUi() {
		mPostDateTextView = (EditText) this.findViewById(R.id.post_datetext);
		mPostDateTextView.setOnClickListener(this);

		mDetailEditText = (EditText) this.findViewById(R.id.detailEditText);
		mUploadImageBtn = (ImageButton) this
				.findViewById(R.id.daily_upload_image);
		mDetailEditText
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
						500) });

		mUploadImageBtn.setBackgroundResource(R.drawable.entry_photo_off);
		mUploadImageBtn.setEnabled(false);

		mLable = (TextView) findViewById(R.id.post_label1);
		mHeight = (EditText) findViewById(R.id.shengao_edit);
		mWeight = (EditText) findViewById(R.id.tizhong_edit);
		mTouWei = (EditText) findViewById(R.id.touwei_edit);
		mXiongWei = (EditText) findViewById(R.id.xiongwei_edit);

		// 限制小数点只有一位
		EditTextListener.addTextListener().Listener(mHeight, this);
		EditTextListener.addTextListener().Listener(mWeight, this);
		EditTextListener.addTextListener().Listener(mXiongWei, this);
		EditTextListener.addTextListener().Listener(mTouWei, this);

		// 提示照片不能点击
		LinearLayout layout = (LinearLayout) this
				.findViewById(R.id.daily_post_layout);
		TextView text = new TextView(this);
		text.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		text.setGravity(Gravity.CENTER_VERTICAL);
		text.setPadding(20, 0, 0, 0);
		text.setText("不能编辑照片");
		text.setTextColor(getResources().getColor(R.color.istar_h));
		text.setTextSize(20);
		layout.addView(text);
		// 显示头像
		final ImageView userIcon = (ImageView) this
				.findViewById(R.id.post_user_icon);
		ImageDownloadTask imageTask = new ImageDownloadTask(
				new ImageDoneCallback() {

					@Override
					public void imageLoaded(Bitmap result) {
						if (result != null) {
							userIcon.setImageBitmap(result);
						}
					}
				});
		imageTask.execute(loginUserInfo.getUserIcon());

		// 自定义title bar
		setCustomTitleBar();

		// 取得当前日期
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());

		mYear = cal.get(Calendar.YEAR);
		mMonth = cal.get(Calendar.MONTH);
		mDay = cal.get(Calendar.DAY_OF_MONTH);
		mPostDateTextView.setText(String.format("%d/%02d/%02d", mYear,
				mMonth + 1, mDay));
	}

	public void setCustomTitleBar() {
		mTitleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		mTitleBarTitle.setText("编辑");

		mTitleBarCancel = (ImageButton) this
				.findViewById(R.id.titlebar_leftbtn);
		mTitleBarCancel.setBackgroundResource(R.drawable.selector_hd_ccl);
		mTitleBarCancel.setOnClickListener(this);

		mTitleBarPost = (ImageButton) this
				.findViewById(R.id.titlebar_rightbtn1);
		mTitleBarPost.setBackgroundResource(R.drawable.selector_hd_cont);
		mTitleBarPost.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.titlebar_leftbtn:
			cancelPostDaily();
			break;
		case R.id.titlebar_rightbtn1:
			// 发表文章
			postNewDaily();

			break;
		case R.id.post_datetext:
			// 选择日期
			DateTimePicker.settime().getdate(mPostDateTextView,
					DailyEditActivity.this);
			break;
		default:
			break;
		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case POST_DAILY_DONE:
				reloadDailyList();
				break;
			default:
				// 错误处理
				toErrorMessage(msg.obj.toString());
				break;
			}

			super.handleMessage(msg);
		}

	};

	protected void toErrorMessage(String msgString) {
		// 取得出错，显示提示信息
		mProgressDialog.dismiss();
		Toast.makeText(this, msgString, Toast.LENGTH_LONG).show();
	}

	protected void reloadDailyList() {

		// 从发表新文章页面返回时，重新加载
		this.setResult(Activity.RESULT_OK, mIntent);
		this.finish();

		// 设定退出动画
		overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_bottom);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 取消时候，直接返回主页面
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			cancelPostDaily();
		}
		return true;
	}

	private void cancelPostDaily() {
		// 取消时候，直接返回
		this.setResult(Activity.RESULT_CANCELED, mIntent);
		this.finish();

		// 设定退出动画
		overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_bottom);
	}

	private void postNewDaily() {
		// 没有发表内容时候，提示输入发表内容
		String detail = mDetailEditText.getText().toString().trim();
		if (detail.equals("")) {
			Toast.makeText(this, "请说几句话吧，亲。", Toast.LENGTH_LONG).show();
			return;
		}

		// 没有添付图片，直接发表新信息
		doPostNewDaily();

	}

	protected void doPostNewDaily() {

		// （发表文章）后台处理参数设定
		Map<String, String> params = new HashMap<String, String>();
		params.put("UserId", loginUserInfo.getUserSId());

		// 发表日期编辑 YYYY/MM/DD -> YYYYMMDD
		String postDate = mPostDateTextView.getText().toString();

		params.put("EventDate", postDate.replaceAll("/", ""));
		params.put("Detail", mDetailEditText.getText().toString());
		
		params.put("Daily_Id", mBundle.getString(ConstantDef.ccDaily_Id));
		// 身高体重头围胸围
		if (!"".equals(childId)) {
			params.put("ChildId", childId);
		}

		params.put("Height", mHeight.getText().toString());
		params.put("Weight", mWeight.getText().toString());
		params.put("Touwei", mTouWei.getText().toString());
		params.put("Xiongwei", mXiongWei.getText().toString());

		HttpPostUtil httpUtil = HttpPostUtil.getInstance();
		httpUtil.setOnPostProcessListener(this);
		httpUtil.sendPostMessage(ConstantDef.cUrlDailyEdit, params);
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

}
