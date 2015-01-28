package cn.leature.istarbaby;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.Window;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.login.LoginActivity;

public class SplashActivity extends Activity {

	Handler mhandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);

		// 检查登录状态
		checkLoginStatus();

		mhandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent Intent = new Intent();

				if (LoginInfo.isFirstStartApp(SplashActivity.this)) {
					Intent.setClass(SplashActivity.this, GuideActivity.class);
				} else {
					Bundle bundle=new Bundle();
					bundle.putString("modle", "start");
					Intent.putExtras(bundle);
					Intent.setClass(SplashActivity.this, LoginActivity.class);

				}

				startActivity(Intent);
				finish();
			}
		}, 1000);

	}

	private void checkLoginStatus() {
		//
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}
}
