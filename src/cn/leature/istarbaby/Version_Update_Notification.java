package cn.leature.istarbaby;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class Version_Update_Notification extends Activity implements
		OnClickListener {

	private TextView version_update_back;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.version_update_notification); // 更新界面
		
		Version_Update mVersion_Update = new Version_Update(
				Version_Update_Notification.this);// 调用更新
		mVersion_Update.checkUpdateInfo();
		
		version_update_back = (TextView) findViewById(R.id.version_update_back);
		version_update_back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == version_update_back) {
			finish();
		}

	}
}
