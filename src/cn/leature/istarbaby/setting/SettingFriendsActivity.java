package cn.leature.istarbaby.setting;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts.Photo;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.network.HttpClientUtil;
import cn.leature.istarbaby.utils.LcLog;

import com.setting.app.friend.MySideBar;
import com.setting.app.friend.MyUserInfoAdapter;
import com.setting.app.friend.PinyinComparator;
import com.setting.app.friend.PinyinUtils;
import com.setting.app.friend.UserInfo;
import com.setting.app.friend.MySideBar.OnTouchingLetterChangedListener;

public class SettingFriendsActivity extends Activity implements
		OnTouchingLetterChangedListener, OnClickListener {

	private ListView mFriend_listview;
	private List<UserInfo> userInfos;
	private TextView mOverlay;
	private MySideBar mMySideBar;
	private MyUserInfoAdapter adapter;
	private OverlayThread overlayThread = new OverlayThread();

	private static final String[] PHONES_PROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID };
	/** 联系人显示名称 **/
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;
	/** 电话号码 **/
	private static final int PHONES_NUMBER_INDEX = 1;

	/** 头像ID **/
	private static final int PHONES_PHOTO_ID_INDEX = 2;

	/** 联系人的ID **/
	private static final int PHONES_CONTACT_ID_INDEX = 3;
	private TextView titleBarTitle;
	private ImageButton titleBarBack;
	private ImageButton titleBarEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settingfriends);

		mFriend_listview = (ListView) findViewById(R.id.friend_listview);
		mMySideBar = (MySideBar) findViewById(R.id.myView);
		mOverlay = (TextView) findViewById(R.id.tvLetter);
		mFriend_listview.setTextFilterEnabled(true);
		mOverlay.setVisibility(View.INVISIBLE);

		setCustomTitleBar();

		getPhoneContacts();
		adapter = new MyUserInfoAdapter(this, userInfos);
		if (userInfos.size() <= 0) {
			Toast.makeText(this, "通讯录未添加", Toast.LENGTH_LONG).show();
			return;
		}
		mFriend_listview.setAdapter(adapter);

		mMySideBar.setOnTouchingLetterChangedListener(this);
	}

	private void setCustomTitleBar() {
		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("添加联系人");

		titleBarBack = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarBack.setOnClickListener((this));
		titleBarBack.setBackgroundResource(R.drawable.selector_hd_bk);
		titleBarBack.setOnClickListener(this);

		titleBarEdit = (ImageButton) this.findViewById(R.id.titlebar_rightbtn1);
		titleBarEdit.setBackgroundResource(R.drawable.selector_hd_share);
		titleBarEdit.setOnClickListener(this);

	}

	private void getPhoneContacts() {

		ContentResolver resolver = this.getContentResolver();
		userInfos = new ArrayList<UserInfo>();

		// 获取手机联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				PHONES_PROJECTION, null, null, null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {

				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;

				// 得到联系人名称
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);
				if (contactName == null) {
					contactName = "";
				}
				
				// 得到联系人ID
				Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);

				// 得到联系人头像ID
				Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);

				// 得到联系人头像Bitamp
				Bitmap contactPhoto = null;

				// photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
				if (photoid > 0) {
					Uri uri = ContentUris.withAppendedId(
							ContactsContract.Contacts.CONTENT_URI, contactid);
					InputStream input = ContactsContract.Contacts
							.openContactPhotoInputStream(resolver, uri);
					contactPhoto = BitmapFactory.decodeStream(input);
				} else {
					contactPhoto = BitmapFactory.decodeResource(getResources(),
							R.drawable.yuan);
				}

				UserInfo userInfo = new UserInfo(contactName, phoneNumber,
						PinyinUtils.getAlpha(contactName));
				userInfos.add(userInfo);
			}
			phoneCursor.close();
		}

		// 排序
		UserInfo[] user = new UserInfo[userInfos.size()];
		for (int i = 0; i < userInfos.size(); i++) {
			user[i] = userInfos.get(i);
		}

		Arrays.sort(user, new PinyinComparator());

		userInfos = Arrays.asList(user);
	}

	private Handler handler = new Handler() {
	};

	public int alphaIndexer(String s) {

		int position = 0;
		for (int i = 0; i < userInfos.size(); i++) {
			LcLog.e("SettingFriendsActivity", "s=" + s + ",py:"
					+ userInfos.get(i).getPy());
			if (userInfos.get(i).getPy().toUpperCase().startsWith(s)) {
				position = i;
				break;
			}
		}
		return position;
	}

	private class OverlayThread implements Runnable {

		public void run() {
			mOverlay.setVisibility(View.GONE);
		}

	}

	@Override
	public void onTouchingLetterChanged(String s) {

		mOverlay.setText(s);
		mOverlay.setVisibility(View.VISIBLE);
		handler.removeCallbacks(overlayThread);
		handler.postDelayed(overlayThread, 1000);

		if (alphaIndexer(s) > 0) {
			int position = alphaIndexer(s);
			mFriend_listview.setSelection(position);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_leftbtn:
			backPrePage();
			break;
		case R.id.titlebar_rightbtn1:

			ArrayList<UserInfo> mData = adapter.getselect();

			Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < mData.size(); i++) {
				buffer.append(mData.get(i).getPhoneNum());
				if (i < mData.size() - 1) {
					buffer.append(",");
				}
			}
			sendIntent.setData(Uri.parse("smsto:" + buffer.toString().trim()));
			sendIntent.putExtra("sms_body", "育儿宝全程关注宝宝成长，分享宝宝快乐时光。你有在用吗? "
					+ HttpClientUtil.PACKAGE_SERVER_URL_MESSAGE);
			startActivity(sendIntent);
			buffer.setLength(0);
			mData.clear();

			try {
				Thread.sleep(500);
				adapter.notifyDataSetChanged();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;
		}
	}

	private void backPrePage() {
		Intent intent = new Intent();
		this.setResult(Activity.RESULT_CANCELED, intent);
		this.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backPrePage();
		}

		return false;
	}
}
