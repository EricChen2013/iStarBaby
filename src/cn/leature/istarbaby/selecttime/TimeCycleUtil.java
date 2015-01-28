package cn.leature.istarbaby.selecttime;

import android.widget.TextView;

public class TimeCycleUtil {
	static TimeCycleUtil timeCycleUtil = new TimeCycleUtil();

	private TimeCycleUtil() {
	}

	public static TimeCycleUtil showtime() {

		return timeCycleUtil;
	}

	public void showHourTime(String oldhourtime, TextView hourtext) {

		char[] a = oldhourtime.toCharArray();
		StringBuffer hourstr = new StringBuffer();
		for (int i = 0; i < oldhourtime.length(); i++) {
			if (i == 2) {
				hourstr.append(":");
			}
			hourstr.append(a[i]);

		}
		hourtext.setText(hourstr.toString());
	}
}
