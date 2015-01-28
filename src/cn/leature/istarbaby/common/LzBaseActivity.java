package cn.leature.istarbaby.common;

import cn.leature.istarbaby.monitor.MonitorShareModel;
import android.app.Activity;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.Toast;

public class LzBaseActivity extends Activity {
	private boolean ispress;

	private MonitorShareModel mShareMonitor = MonitorShareModel.getInstance();

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (ispress) {
				mShareMonitor.logout(this);

				finish();
			} else {
				Toast.makeText(LzBaseActivity.this, "请再按一次退出",
						Toast.LENGTH_LONG).show();
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						ispress = false;
					}
				}, 5000);

				ispress = true;
			}
		}
		return true;

	}
}
