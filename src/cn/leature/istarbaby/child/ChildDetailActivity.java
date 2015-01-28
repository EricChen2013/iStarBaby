package cn.leature.istarbaby.child;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.domain.ChildrenInfo;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.gallery.GalleyShowActivity;
import cn.leature.istarbaby.network.HttpGetUtil;
import cn.leature.istarbaby.network.HttpGetUtil.RequestGetDoneCallback;
import cn.leature.istarbaby.network.HttpPostUtil;
import cn.leature.istarbaby.network.HttpPostUtil.OnPostProcessListener;
import cn.leature.istarbaby.network.ImageDownloadTask;
import cn.leature.istarbaby.network.ImageDownloadTask.ImageDoneCallback;
import cn.leature.istarbaby.selecttime.TimeCycleUtil;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.DateUtilsDef;
import cn.leature.istarbaby.utils.DensityUtil;
import cn.leature.istarbaby.utils.ResizeImage;

public class ChildDetailActivity extends Activity implements OnClickListener,
		OnPostProcessListener {
	private ImageButton titleBarBack, titleBarEdit;
	private HttpPostUtil httpUtil;
	private static final int ALBUM_LIST_DONE = 1;
	private static final int ALBUM_LIST_ERROR = -1;
	// 图片的宽高比例
	private static final int CHILD_IMAGE_WIDTH = 29;
	private static final int CHILD_IMAGE_HEIGHT = 20;
	// 编辑后返回时的编号
	private TextView mChild_name, mGender, mBirthday, mBirthday_time, mWeight,
			mHeight, mXiongwei, mTouwei, mChild_no, mFenwan_qijian,
			mFenain_shijian, mFenmian_difang, mFenmian_yuding;
	private ImageView micon;

	private Intent mIntent;
	private Bundle mBundle;
	private LzProgressDialog mProgressDialog;
	private int picWidth;
	private DisplayMetrics dm;
	private AlertDialog dialog;
	private Button mQueding, mQuxiao;
	private PopupWindow mPopupWindow;
	private boolean openWindow = false;
	private String child_id;
	private boolean isDetailModified = false;
	private String[] mImageFiles;
	private View mInculd_layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_child_detail);
		setCustomTitleBar();
		inItUi();

		mProgressDialog = new LzProgressDialog(this);
		mProgressDialog.setCancelable(true);
		mIntent = this.getIntent();
		mBundle = mIntent.getExtras();
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		picWidth = (int) (dm.widthPixels - DensityUtil.dip2px(this, 16) * 2);
		mImageFiles = new String[1];
		getDefaultData();
	}

	private void getDefaultData() {
		mProgressDialog.show();

		httpUtil = HttpPostUtil.getInstance();
		httpUtil.setOnPostProcessListener(this);

		Map<String, String> param = new HashMap<String, String>();
		// 登录用户的ID
		param.put("UserId", LoginInfo.getLoginUserId(this));
		// 宝宝排行childNo
		param.put("ChildNo", mBundle.getString(ConstantDef.ccChildNo));

		httpUtil.sendPostMessage(ConstantDef.cUrlChildrenDetail, param);
	}

	private void inItUi() {
		micon = (ImageView) findViewById(R.id.detail_child_icon);
		mChild_name = (TextView) findViewById(R.id.detail_child_name);
		micon.setOnClickListener(this);

		mGender = (TextView) findViewById(R.id.detail_child_sex);
		mBirthday = (TextView) findViewById(R.id.detail_child_date);
		mBirthday_time = (TextView) findViewById(R.id.detail_child_hour);
		mWeight = (TextView) findViewById(R.id.detail_child_weight);

		mHeight = (TextView) findViewById(R.id.detail_child_hight);
		mXiongwei = (TextView) findViewById(R.id.detail_child_xiongwei);
		mTouwei = (TextView) findViewById(R.id.detail_child_touwei);
		mChild_no = (TextView) findViewById(R.id.detail_child_number);

		mFenwan_qijian = (TextView) findViewById(R.id.detail_child_danchenshijian);
		mFenain_shijian = (TextView) findViewById(R.id.detail_child_fenwanshijian);
		mFenmian_difang = (TextView) findViewById(R.id.detail_child_place);
		mFenmian_yuding = (TextView) findViewById(R.id.detail_child_chushenyuding);
		mchildDetail = findViewById(R.id.childdetail);
		mchildDetail.setOnClickListener(this);

	}

	private void setCustomTitleBar() {

		titleBarBack = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarBack.setOnClickListener((this));
		titleBarBack.setBackgroundResource(R.drawable.selector_hd_bk);

		titleBarEdit = (ImageButton) this.findViewById(R.id.titlebar_rightbtn1);
		titleBarEdit.setOnClickListener(this);
		titleBarEdit.setBackgroundResource(R.drawable.selector_hd_edt);
		mInculd_layout = findViewById(R.id.include_layout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.child_edit_detail, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backPrePage();
		}
		return true;

	}

	private void backPrePage() {
		// 取消时候，直接返回
		if (mPopupWindow != null) {
			mPopupWindow.dismiss();
		}
		if (isDetailModified) {
			// 信息被修改过，需要返回更新数据
			mIntent.putExtras(mBundle);
			this.setResult(ConstantDef.RESULT_CODE_CHILD_EDIT, mIntent);
		} else {
			this.setResult(Activity.RESULT_CANCELED, mIntent);
		}

		this.finish();

		// 设定退出动画
		this.overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_right);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_leftbtn:

			backPrePage();
			break;
		case R.id.childdetail:
			if (mPopupWindow != null) {
				openWindow = false;
				mPopupWindow.dismiss();
			}
			break;
		case R.id.detail_child_icon:
			showImage();
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
					LoginInfo.getLoginUserId(ChildDetailActivity.this));
			param.put("ChildId", child_id);
			HttpGetUtil httpGetUtil = new HttpGetUtil(
					new RequestGetDoneCallback() {

						@Override
						public void requestWithGetDone(String result) {

							if ((result == null) || (result.equals("0"))) {
								// 处理出错
								return;
							}
							mProgressDialog.dismiss();
							mPopupWindow.dismiss();
							dialog.dismiss();
							// 登录成功，保存登录用户
							Map<String, String> param = new HashMap<String, String>();
							param.put("UserId", LoginInfo
									.getLoginUserId(ChildDetailActivity.this));
							param.put("UserString", result);
							LoginInfo.saveLoginUser(ChildDetailActivity.this,
									param);

							// 从发表新文章页面返回时，重新加载。 通知菜单数据更新。
							reloadDailyList();

						}
					});
			httpGetUtil.execute(ConstantDef.cUrlChildrenDelete, param);

			break;
		case R.id.delete_quxiao:
			mProgressDialog.dismiss();
			mPopupWindow.dismiss();
			dialog.dismiss();

			break;
		default:
			break;
		}
	}

	private void showImage() {
		if (mImageFiles[0].length() == 0) {
			// 没有图片不能点击
			return;
		}

		Intent intent = new Intent();
		Bundle bundle = new Bundle();

		bundle.putString("SD", "ChildDetail");
		bundle.putInt("StartIndex", 0);
		bundle.putStringArray("Dailyimage", mImageFiles);
		intent.putExtras(bundle);
		intent.setClass(ChildDetailActivity.this, GalleyShowActivity.class);
		startActivityForResult(intent, ConstantDef.REQUEST_CODE_CHILD_PHOTO);
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
			msg.what = ALBUM_LIST_DONE;
		} else {
			msg.what = ALBUM_LIST_ERROR;
		}

		handler.sendMessage(msg);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
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
	private View mchildDetail;

	protected void toErrorMessage(String msgString) {
		// 取得出错，显示提示信息
		mProgressDialog.dismiss();

		Toast.makeText(this, msgString, Toast.LENGTH_LONG).show();
	}

	protected void toListDailyDone(String listResult) {

		mProgressDialog.dismiss();
		try {
			JSONObject jsonObject = new JSONObject(listResult)
					.getJSONObject("children_detail");
			ChildrenInfo manageinfo = new ChildrenInfo(jsonObject);

			mChild_name.setText(manageinfo.getChildName());
			if (manageinfo.getGender().equals("1")) {

				mGender.setText("男");

			} else if (manageinfo.getGender().equals("2")) {
				mGender.setText("女");
			}

			// 时间转换2014/07/17
			mBirthday.setText(DateUtilsDef.dateFormatString(
					manageinfo.getBirthday(), "yyyy/MM/dd"));
			TimeCycleUtil.showtime().showHourTime(
					manageinfo.getBirthday_time(), mBirthday_time);
			if ("".equals(manageinfo.getFenmian_yuding())) {
				mFenmian_yuding.setText("-");
			} else {
				String yudingdate = DateUtilsDef.dateFormatString(
						manageinfo.getFenmian_yuding(), "yyyy/MM/dd");
				if ("".equals(yudingdate)) {
					yudingdate = "-";
				}
				mFenmian_yuding.setText(yudingdate);
			}

			mWeight.setText(stringForDisplay(manageinfo.getWeight(), "", "kg"));
			mHeight.setText(stringForDisplay(manageinfo.getHeight(), "", "cm"));
			mXiongwei.setText(stringForDisplay(manageinfo.getXiongwei(), "",
					"cm"));
			mTouwei.setText(stringForDisplay(manageinfo.getTouwei(), "", "cm"));

			mChild_no.setText(stringForDisplay(manageinfo.getChild_no(), "第 ",
					" 子"));
			mFenwan_qijian.setText(stringForDisplay(
					manageinfo.getHuaiyun_qijian(), "满 ", " 周"));
			mFenain_shijian.setText(stringForDisplay(
					manageinfo.getFenmian_shijian(), "", " 小时"));
			mFenmian_difang.setText(stringForDisplay(
					manageinfo.getFenmian_difang(), "", ""));

			child_id = manageinfo.getChild_id();
			mImageFiles[0] = manageinfo.getImage1();
			ImageDownloadTask imageTask = new ImageDownloadTask(
					new ImageDoneCallback() {

						@Override
						public void imageLoaded(Bitmap result) {
							if (null == result) {
								// 加载默认图片
								result = BitmapFactory.decodeResource(
										getResources(),
										R.drawable.noimage_child);
							}

							// 重新设置图片大小
							LayoutParams lParams = micon.getLayoutParams();
							lParams.width = picWidth;
							micon.setLayoutParams(lParams);

							micon.setImageBitmap(ResizeImage
									.resizeBitmapWithRadio(result,
											CHILD_IMAGE_WIDTH,
											CHILD_IMAGE_HEIGHT));
						}
					});
			imageTask.execute(manageinfo.getImage1());

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String stringForDisplay(String inString, String pre, String suf) {
		if (null == inString || inString.trim().length() == 0) {
			// 空字符串
			return "-";
		}

		return pre + inString + suf;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ConstantDef.REQUEST_CODE_CHILD_PHOTO) {
			return;
		}

		if (resultCode == Activity.RESULT_OK) {
			isDetailModified = true;
		}

		mBundle = data.getExtras();
		// 重新加载
		getDefaultData();

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

		mPopupWindow.showAtLocation(findViewById(R.id.childdetail),
				Gravity.RIGHT | Gravity.TOP, 0,
				Height + DensityUtil.dip2px(this, 20));

		popunwindwow.findViewById(R.id.item1).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						openWindow = !openWindow;
						Intent intent = new Intent();
						intent.setClass(ChildDetailActivity.this,
								ChildEditActivity.class);
						intent.putExtras(mBundle);

						startActivityForResult(intent,
								ConstantDef.REQUEST_CODE_DAILY_EDIT);

						ChildDetailActivity.this.overridePendingTransition(
								R.anim.activity_in_from_bottom,
								R.anim.activity_nothing_in_out);

						mPopupWindow.dismiss();

					}
				});

		popunwindwow.findViewById(R.id.item2).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						openWindow = !openWindow;
						// 显示删除进度
						mProgressDialog.show();
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

	private void dialog() {

		View layout = getLayoutInflater().inflate(R.layout.delete_dialog, null);
		mQueding = (Button) layout.findViewById(R.id.delete_queding);
		mQuxiao = (Button) layout.findViewById(R.id.delete_quxiao);

		// 提示信息
		TextView title = (TextView) layout.findViewById(R.id.alert_text);
		title.setText("确定要删除吗？母子手册等相关数据将会被删除，且无法恢复。");

		mQuxiao.setOnClickListener(this);
		mQueding.setOnClickListener(this);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(layout);
	}
}
