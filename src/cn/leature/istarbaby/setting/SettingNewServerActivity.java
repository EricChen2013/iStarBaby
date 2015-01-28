package cn.leature.istarbaby.setting;

import java.util.ArrayList;
import java.util.List;

import com.setting.app.friend.UserInfo;

import cn.leature.istarbaby.R;
import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class SettingNewServerActivity extends Activity implements
		OnClickListener
{
	private TextView titleBarTitle;
	private ImageButton titleBarBack;
	private ImageButton titleBarEdit;

	private ArrayList<UserInfo> mData;
	private TextView mResult_edittext;
	private Button mBtv_Send;
	private EditText mMessage_content, mServer_Name;
	private String mCatenateString = "我是zone超人";
	private StringBuffer mUserinfoBuffer;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_new_server);

		setCustomTitleBar();
		mData = (ArrayList<UserInfo>) getIntent().getSerializableExtra(
				"userInfo");
		initUI();

	}

	private void initUI()
	{

		mBtv_Send = (Button) findViewById(R.id.btv_send);
		mServer_Name = (EditText) findViewById(R.id.newserver_name);
		mResult_edittext = (TextView) findViewById(R.id.result_edittext);
		mMessage_content = (EditText) findViewById(R.id.message_content);
		mMessage_content.setText(mCatenateString);
		mUserinfoBuffer = new StringBuffer();
		for (int i = 0; i < mData.size(); i++)
		{
			UserInfo userInfo = mData.get(i);
			String userName = userInfo.getUserName();
			mUserinfoBuffer.append(userName);
			if (i < mData.size() - 1)
			{
				mUserinfoBuffer.append(",");
			}
		}
		mServer_Name.setText(mUserinfoBuffer.toString());

		// 判断是否有收件人
		if (sendmassage())
		{

			mBtv_Send.setEnabled(false);
		}

		mServer_Name.addTextChangedListener(new TextWatcher()
		{

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s)
			{
				
				if (!"".equals(s.toString()))
				{
					mBtv_Send.setEnabled(true);
					
				}
			}
		});
		mBtv_Send.setOnClickListener(this);
	}

	private boolean sendmassage()
	{
		if ("".equals(mServer_Name.getText().toString().trim()))
		{
			return true;
		}
		else
		{
			return false;

		}
	}

	private void setCustomTitleBar()
	{

		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("新消息");

		titleBarBack = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarBack.setVisibility(View.INVISIBLE);

		titleBarEdit = (ImageButton) this.findViewById(R.id.titlebar_rightbtn1);
		titleBarEdit.setOnClickListener(this);
		titleBarEdit.setBackgroundResource(R.drawable.selector_hd_ccl);
	}

	/**
	 * 直接调用短信接口发短信
	 * 
	 * @param phoneNumber
	 * @param message
	 */
	public void sendSMS(String phoneNumber, String message)
	{

		String DELIVERED = "sms_delivered";
		String SENT_SMS_ACTION = "SENT_SMS_ACTION";

		Intent sentIntent = new Intent(SENT_SMS_ACTION);

		PendingIntent sentPI = PendingIntent.getBroadcast(
				SettingNewServerActivity.this, 0, sentIntent, 0);

		PendingIntent deliveredPI = PendingIntent.getActivity(this, 0,
				new Intent(DELIVERED), 0);
		// 获取短信管理器
		android.telephony.SmsManager smsManager = android.telephony.SmsManager
				.getDefault();
		// 拆分短信内容（手机短信长度限制）

		List<String> divideContents = smsManager.divideMessage(message);
		for (String text : divideContents)
		{

			smsManager.sendTextMessage(phoneNumber, null, text, sentPI,
					deliveredPI);

		}

		SettingNewServerActivity.this.registerReceiver(new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context _context, Intent _intent)
			{

				switch (getResultCode())
				{
				case Activity.RESULT_OK:
					mResult_edittext.setText(mCatenateString);
					Toast.makeText(SettingNewServerActivity.this, "短信发送成功",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					break;
				}
			}
		}, new IntentFilter(SENT_SMS_ACTION));

	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.titlebar_rightbtn1:

			Intent intent = new Intent();
			intent.setClass(this, SettingFriendsActivity.class);
			startActivity(intent);
			this.finish();
			break;
		case R.id.btv_send:
			UserInfo userInfo;
			// 添加了新的号码
			Toast.makeText(this, "我点击", Toast.LENGTH_LONG).show();

			String newServer_Name = mServer_Name.getText().toString().trim();
			Log.e("", "newServer_Name:=" + newServer_Name);
			if (!"".equals(newServer_Name))
			{
				mBtv_Send.setEnabled(true);
			}

			String[] phonename = newServer_Name.split(",");

			for (int i = 0; i < phonename.length; i++)
			{
				if (i < mData.size())
				{
					// 没有新号码的时候
					userInfo = mData.get(i);
					sendSMS(userInfo.getPhoneNum(), mMessage_content.getText()
							.toString().trim());
				}
				else
				{
					// 有添加号码的时候
					sendSMS(phonename[i], mMessage_content.getText().toString()
							.trim());

				}
				Toast.makeText(this, "发送 " + i, Toast.LENGTH_LONG).show();

			}

			break;
		default:
			break;
		}
	}
}
