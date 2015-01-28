package cn.leature.istarbaby.monitor;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.R.layout;
import cn.leature.istarbaby.R.menu;
import cn.leature.istarbaby.domain.ChildrenInfo;
import cn.leature.istarbaby.network.HttpClientUtil;
import cn.leature.istarbaby.network.HttpGetUtil;
import cn.leature.istarbaby.network.ImageDownloadTask;
import cn.leature.istarbaby.network.HttpGetUtil.RequestGetDoneCallback;
import cn.leature.istarbaby.network.ImageDownloadTask.ImageDoneCallback;
import cn.leature.istarbaby.selecttime.TimeCycleUtil;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.DateUtilsDef;
import cn.leature.istarbaby.utils.InputStreamTransferUtil;
import cn.leature.istarbaby.utils.ResizeImage;
import android.os.Bundle;
import android.os.Message;
import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;

public class MonitorTimeZoneActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor_time_zone);
		 Calendar calendar = new GregorianCalendar();
		 TimeZone tz = TimeZone.getTimeZone("America/Los_Angeles");

//		String fromAssets = getFromAssets("timezones.xml");
//		calendar.setTimeZone(tz);
//		SimpleDateFormat df1 = new SimpleDateFormat(
//				"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//		String format = df1.format(tz);
//		Log.e("format", "format==="+format);
	}

	public String getFromAssets(String fileName)
	{
		try
		{
			InputStreamReader inputReader = new InputStreamReader(
					getResources().getAssets().open(fileName));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = "";
			String Result = "";
			while ((line = bufReader.readLine()) != null)
				Result += line;

			return Result;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return fileName;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.monitor_time_zone, menu);
		return true;
	}

}
