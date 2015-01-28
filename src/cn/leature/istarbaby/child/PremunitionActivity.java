package cn.leature.istarbaby.child;

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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.domain.VaccineInfo;
import cn.leature.istarbaby.network.HttpGetUtil;
import cn.leature.istarbaby.network.HttpGetUtil.RequestGetDoneCallback;
import cn.leature.istarbaby.network.HttpPostUtil;
import cn.leature.istarbaby.network.HttpPostUtil.OnPostProcessListener;
import cn.leature.istarbaby.selecttime.DateTimePicker;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.DensityUtil;
import cn.leature.istarbaby.utils.LzViewHolder;

public class PremunitionActivity extends Activity implements OnClickListener,
		OnPostProcessListener {

	private ListView listView1;
	private ImageButton titleBarBack, titleBarEdit;
	private TextView titleBarTitle;
	private int mYear, mMonth, mDay;
	EditText mPremun_datetext;
	ArrayList<VaccineInfo> mData = new ArrayList<VaccineInfo>();
	List<String> mVacSelected = new ArrayList<String>();
	List<String> mCulateInfoList = new ArrayList<String>();
	private premunitionTask mAdapter;
	private boolean isEditMode = false;
	private HttpPostUtil mHttpUtil;
	private Bundle mBundle;
	private int mVacIconWidthHeight;
	private Intent mIntent;
	// 图片上传出错（Message用）
	protected static final int UPLOAD_ICON_ERROR = -1;
	// 修改成功（Message用）
	protected static final int PROFILE_EDIT_DONE = 4;
	// 修改出错（Message用）
	protected static final int PROFILE_EDIT_ERROR = -2;
	boolean isRefreshData = false;
	private LzProgressDialog mProgressDialog;

	private int[] mPpre_Image = new int[]

	{ R.drawable.bste_bc1_inful0age, R.drawable.bste_bc1_inful1age,
			R.drawable.bste_bc1_inful2age, R.drawable.bste_bc1_inful3age,
			R.drawable.bste_bc1_inful4age, R.drawable.bste_bc1_inful5age,
			R.drawable.bste_bc1_inful6age };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_premunition);
		mIntent = this.getIntent();
		mBundle = mIntent.getExtras();
		mHttpUtil = HttpPostUtil.getInstance();
		mProgressDialog = new LzProgressDialog(this);
		mProgressDialog.setCancelable(true);
		mVacIconWidthHeight = DensityUtil.dip2px(this, 40);
		initUI();
		setCustomTitleBar();
		getDetail();

	}

	private void getDetail() {
		mProgressDialog.show();
		Map<String, String> param = new HashMap<String, String>();
		param.put("UserId", LoginInfo.getLoginUserId(PremunitionActivity.this));
		param.put("ChildId", mBundle.getString(ConstantDef.ccChildId));
		HttpGetUtil httpGetUtil = new HttpGetUtil(new RequestGetDoneCallback() {

			@Override
			public void requestWithGetDone(String result) {
				mProgressDialog.dismiss();

				if ((result == null) || (result.equals("0"))) {
					// 处理出错
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					JSONArray json_detail = jsonObject
							.getJSONArray("listVaccineMasterInfo");

					if ((json_detail == null) || (json_detail.equals("0"))) {
						// 处理出错
						return;
					}
					for (int i = 0; i < json_detail.length(); i++) {
						VaccineInfo vacInfo = new VaccineInfo(json_detail
								.getJSONObject(i));
						mData.add(vacInfo);
					}

					String InoculateInfo = (String) jsonObject
							.get("InoculateInfo");

					String[] culateInfo = InoculateInfo.split("\\|");
					for (int i = 0; i < culateInfo.length; i = i + 2) {
						mCulateInfoList.add(culateInfo[i]);
					}

				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				listView1.setAdapter(mAdapter);

			}
		});
		httpGetUtil.execute(ConstantDef.cUrlVaccineInfo, param);
	}

	private void initUI() {
		listView1 = (ListView) findViewById(R.id.immunity_listview);
		mAdapter = new premunitionTask();

		listView1.setDividerHeight(0);

	}

	private void setCustomTitleBar() {
		// 设置主菜单
		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		// 显示名字
		if (null != mBundle) {
			titleBarTitle.setText(mBundle.getString(ConstantDef.ccChildName));
		}
		titleBarBack = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarBack.setOnClickListener((this));
		titleBarBack.setBackgroundResource(R.drawable.selector_hd_bk);

		titleBarEdit = (ImageButton) this.findViewById(R.id.titlebar_rightbtn1);
		titleBarEdit.setOnClickListener(this);
		titleBarEdit.setBackgroundResource(R.drawable.selector_hd_edt);
	}

	class premunitionTask extends BaseAdapter {

		private ImageView imgLesson;
		private int itmes;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mData.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (position == 0) {
				convertView = getLayoutInflater().inflate(
						R.layout.premunition_item1, null);
				mPremun_datetext = LzViewHolder.get(convertView,
						R.id.premun_datetext);
				mPremun_datetext.setOnClickListener(PremunitionActivity.this);
				// 取得当前日期
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(System.currentTimeMillis());

				mYear = cal.get(Calendar.YEAR);
				mMonth = cal.get(Calendar.MONTH);
				mDay = cal.get(Calendar.DAY_OF_MONTH);
				mPremun_datetext.setText(String.format("%d/%02d/%02d", mYear,
						mMonth + 1, mDay));
				if (isEditMode) {
					mPremun_datetext.setVisibility(View.VISIBLE);
				} else {
					mPremun_datetext.setVisibility(View.GONE);
				}

			} else {
				VaccineInfo vaccineInfo = mData.get(position - 1);

				String times = vaccineInfo.getInoculate_times();
				itmes = Integer.parseInt(times);
				if (vaccineInfo.getVaccine_type().equals("2")) {
					// 流感疫苗
					convertView = getLayoutInflater().inflate(
							R.layout.premunition_item_text1, null);

					LinearLayout layoutimage = LzViewHolder.get(convertView,
							R.id.premunition_item_view);

					layoutimage.removeAllViewsInLayout();
					for (int i = 1; i <= itmes; i++) {
						View view = getLayoutInflater().inflate(
								R.layout.premunition_item2, null);

						ImageView textTitle = (ImageView) view
								.findViewById(R.id.premunition_item_text2);
						TextView textName = (TextView) view
								.findViewById(R.id.premunition_itme_name2);
						LinearLayout layoutimage2 = (LinearLayout) view
								.findViewById(R.id.premunition_item_image2);
						ImageView imageViewBack = (ImageView) view
								.findViewById(R.id.premunition_itme_imageview2);
						if (i == 1) {
							textTitle.setImageResource(R.drawable.lab_nini);
							textName.setVisibility(View.VISIBLE);
							imageViewBack.setVisibility(View.GONE);
							textName.setText(vaccineInfo.getVaccine_name());

						} else {
							textName.setVisibility(View.GONE);
							textTitle.setVisibility(View.INVISIBLE);
							imageViewBack.setVisibility(View.VISIBLE);
							textName.setBackgroundResource(R.drawable.bste_bc1_infulsame);
						}

						ImageView imageView = new ImageView(
								PremunitionActivity.this);
						ImageView getlesson = getLesson(vaccineInfo, i);

						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
								mVacIconWidthHeight, mVacIconWidthHeight);
						params.setMargins(10, 0, 0, 0);
						getlesson.setLayoutParams(params);

						// 打针的高度
						imageView.setBackgroundResource(mPpre_Image[i - 1]);
						LinearLayout.LayoutParams imagebtn_params = new LinearLayout.LayoutParams(
								LayoutParams.WRAP_CONTENT, mVacIconWidthHeight);
						imageView.setLayoutParams(imagebtn_params);

						layoutimage2.addView(imageView);
						layoutimage2.addView(getlesson);
						layoutimage.addView(view);
					}
				} else {
					convertView = getLayoutInflater().inflate(
							R.layout.premunition_item, null);
					TextView texttitle = LzViewHolder.get(convertView,
							R.id.premunition_item_text);

					TextView textname = LzViewHolder.get(convertView,
							R.id.premunition_itme_name);
					LinearLayout layoutimage = LzViewHolder.get(convertView,
							R.id.premunition_item_image);
					if (vaccineInfo.getVaccine_type().equals("0")) {
						texttitle.setBackgroundResource(R.drawable.lab_teiki);

					} else if (vaccineInfo.getVaccine_type().equals("1")) {

						texttitle.setBackgroundResource(R.drawable.lab_nini);
					}

					textname.setText(vaccineInfo.getVaccine_name());

					layoutimage.removeAllViewsInLayout();
					for (int i = 1; i <= itmes; i++) {
						layoutimage.addView(getLesson(vaccineInfo, i));
					}
				}
			}
			return convertView;
		}

		private ImageView getLesson(VaccineInfo vaccineInfo, int time) {

			String vacString = String.format("%s%s%d",
					vaccineInfo.getVaccine_type(), vaccineInfo.getVaccine_id(),
					time);

			ImageView resultImage = new ImageView(PremunitionActivity.this);
			LinearLayout.LayoutParams imagebtn_params = new LinearLayout.LayoutParams(
					mVacIconWidthHeight, mVacIconWidthHeight);
			resultImage.setLayoutParams(imagebtn_params);

			if (isEditMode) {
				if (isVaccineSelected(vacString)) {
					resultImage.setImageResource(R.drawable.btn_bc2_on);
				} else {
					boolean isChecked = isVaccineChecked(vacString);
					if (isChecked) {
						resultImage.setImageResource(R.drawable.btn_bc2_check);
					} else {
						resultImage.setImageResource(R.drawable.btn_bc2_off);
					}
					resultImage
							.setOnClickListener(new imgListener(resultImage,
									isChecked, vacString, vaccineInfo
											.getVaccine_name()));
				}
			} else {
				if (isVaccineSelected(vacString)) {
					resultImage.setImageResource(R.drawable.btn_bc1_on);
				} else {
					resultImage.setImageResource(R.drawable.btn_bc1_off);
				}
			}
			return resultImage;
		}

	}

	class imgListener implements OnClickListener {
		// 定义一个 Button 类型的变量
		private ImageView btn;
		private boolean isChecked;
		private String vacString;
		private String name;

		/*
		 * 一个构造方法, 将Button对象做为参数
		 */
		private imgListener(ImageView btn, boolean ischeck, String vacString,
				String name) {
			this.btn = btn;// 将引用变量传递给实体变量
			this.isChecked = ischeck;
			this.vacString = vacString;
			this.name = name;
		}

		public void onClick(View v) {
			isChecked = !isChecked;

			if (isChecked) {
				btn.setImageResource(R.drawable.btn_bc2_check);
				mVacSelected.add(vacString + "|" + name);

			} else {

				btn.setImageResource(R.drawable.btn_bc2_off);
				mVacSelected.remove(vacString + "|" + name);
			}
		}
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

			if (isEditMode) {
				isEditMode = false;
				// 菜单按钮变成 编辑
				titleBarEdit.setBackgroundResource(R.drawable.selector_hd_edt);
				// 编辑状态，保存信息
				startSavepremunition();
			} else {
				isEditMode = true;
				// 菜单按钮变成 保存
				titleBarEdit.setBackgroundResource(R.drawable.child_edit_cont);

				mAdapter.notifyDataSetChanged();
			}
			break;
		case R.id.premun_datetext:
			// 选择日期
			DateTimePicker.settime().getAllDate(mPremun_datetext, this);

			break;
		default:
			break;
		}

	}

	public boolean isVaccineSelected(String vacString) {
		for (int i = 0; i < mCulateInfoList.size(); i++) {
			if (vacString.equals(mCulateInfoList.get(i))) {
				return true;
			}
		}

		return false;
	}

	private boolean isVaccineChecked(String vacString) {
		for (int i = 0; i < mVacSelected.size(); i++) {
			if (mVacSelected.get(i).indexOf(vacString) >= 0) {
				return true;
			}
		}

		return false;
	}

	private void startSavepremunition() {
		mProgressDialog.set_Text("保存中...", true);
		mProgressDialog.show();

		Map<String, String> param = new HashMap<String, String>();
		String Date = mPremun_datetext.getText().toString().trim();
		param.put("UserId", LoginInfo.getLoginUserId(PremunitionActivity.this));
		param.put("ChildId", mBundle.getString(ConstantDef.ccChildId));
		param.put("EventDate", Date.replaceAll("/", ""));

		if (mVacSelected.size() == 0) {
			param.put("Detail", "");
			mProgressDialog.dismiss();
			mAdapter.notifyDataSetChanged();
			return;

		} else {

			StringBuffer buffer = new StringBuffer();

			for (int i = 0; i < mVacSelected.size(); i++) {
				buffer.append(mVacSelected.get(i)).append("|");
			}
			buffer.deleteCharAt(buffer.length() - 1);
			param.put("Detail", buffer.toString());
		}

		mHttpUtil.setOnPostProcessListener(PremunitionActivity.this);
		mHttpUtil.sendPostMessage(ConstantDef.cUrlInoculateVaccine, param);
	}

	private void backPrePage() {

		// 取消时候，直接返回主页面
		if (isRefreshData) {
			// 信息被修改过，需要返回更新数据
			this.setResult(ConstantDef.RESULT_CODE_PREMUNITION, mIntent);
		} else {
			this.setResult(Activity.RESULT_CANCELED);
		}
		this.finish();
		// 设定退出动画
		this.overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_right);
	}

	@Override
	public void onPostDone(int responseCode, String responseMessage) {
		Message msg = Message.obtain();
		msg.arg1 = responseCode;
		msg.obj = responseMessage;

		if (responseCode == HttpPostUtil.POST_SUCCESS_CODE) {
			msg.what = PROFILE_EDIT_DONE;
		} else {
			msg.what = PROFILE_EDIT_ERROR;
		}

		handler.sendMessage(msg);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PROFILE_EDIT_DONE: // 保存
				saveDetailDone(msg.obj.toString());
				break;
			default:
				// 错误处理
				toErrorMessage(msg.obj.toString());
				break;
			}

			super.handleMessage(msg);
		}

	};

	private void toErrorMessage(String msgString) {
		mProgressDialog.dismiss();
		Toast.makeText(this, msgString, Toast.LENGTH_LONG).show();
	}

	private void saveDetailDone(String result) {

		mProgressDialog.dismiss();

		try {
			JSONObject jsonObject = new JSONObject(result);
			JSONArray json_detail = jsonObject
					.getJSONArray("listVaccineMasterInfo");

			if ((json_detail == null) || (json_detail.equals("0"))) {
				// 处理出错
				return;
			}

			mData.clear();
			mVacSelected.clear();
			mCulateInfoList.clear();

			for (int i = 0; i < json_detail.length(); i++) {
				VaccineInfo vacInfo = new VaccineInfo(
						json_detail.getJSONObject(i));
				mData.add(vacInfo);
			}

			String InoculateInfo = (String) jsonObject.get("InoculateInfo");

			String[] culateInfo = InoculateInfo.split("\\|");
			for (int i = 0; i < culateInfo.length; i = i + 2) {
				mCulateInfoList.add(culateInfo[i]);
			}

		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		mAdapter.notifyDataSetChanged();
		isRefreshData = true;
	}
}
