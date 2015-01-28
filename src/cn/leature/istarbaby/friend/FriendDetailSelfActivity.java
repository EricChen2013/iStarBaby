package cn.leature.istarbaby.friend;

import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.domain.SettingUserInfo;
import cn.leature.istarbaby.network.HttpClientUtil;
import cn.leature.istarbaby.utils.DateUtilsDef;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FriendDetailSelfActivity extends Activity implements
		OnClickListener {

	private TextView titleBarTitle;
	private ImageButton titleBarBack;
	private ImageButton titleBarEdit;

	private ImageView mUserIcon;
	private TextView mName, mFriendId, mFriendInfoMore;

	private DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	private Bundle mBundle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_detail_self);

		mBundle = getIntent().getExtras();
		initUI();

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.noimage)
				.showImageForEmptyUri(R.drawable.noimage)
				.showImageOnFail(R.drawable.noimage).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();

		// 头部导航
		setCustomTitleBar();
		// 显示要加好友信息
		getdetail();
	}

	private void initUI() {
		mUserIcon = (ImageView) findViewById(R.id.friend_detail_icon);
		mName = (TextView) findViewById(R.id.friend_detail_name);
		mFriendInfoMore = (TextView) findViewById(R.id.friend_userinfo_more);
		mFriendId = (TextView) findViewById(R.id.friend_userid);

		LinearLayout layout = (LinearLayout) findViewById(R.id.layout_user_nickname);
		View view = (View) findViewById(R.id.view_user_nickname);
		layout.setVisibility(View.GONE);
		view.setVisibility(View.GONE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			this.finish();
		}
	}

	private void setCustomTitleBar() {

		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("个人资料");

		titleBarBack = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarBack.setOnClickListener((this));
		titleBarBack.setBackgroundResource(R.drawable.selector_hd_bk);

		titleBarEdit = (ImageButton) this.findViewById(R.id.titlebar_rightbtn1);
		titleBarEdit.setVisibility(View.INVISIBLE);
	}

	private void getdetail() {
		String result = mBundle.getString("Detail");
		String[] resultinfo = result.split("\\|");

		try {

			JSONObject jsonObject = new JSONObject(resultinfo[1]);
			SettingUserInfo info = new SettingUserInfo(jsonObject);

			mName.setText(info.getName());
			mFriendId.setText(info.getUser_id());

			String friendInfoMore = "";
			if ("1".equals(info.getGender())) {
				friendInfoMore += "男性";
			} else if ("2".equals(info.getGender())) {
				friendInfoMore += "女性";
			}

			int age = DateUtilsDef.calculateUserAgeWithBirthday(info
					.getBirthday());
			if (age > 0) {
				friendInfoMore += "  " + age + "岁";
			}

			mFriendInfoMore.setText(friendInfoMore);

			// 图片加载
			imageLoader.displayImage(
					HttpClientUtil.SERVER_PATH + info.getIcon(), mUserIcon,
					options);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			backPrePage();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_leftbtn:
			backPrePage();
			break;
		}
	}

	private void backPrePage() {
		this.setResult(Activity.RESULT_CANCELED);
		this.finish();

		// 设定退出动画
		this.overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_right);
	}
}
