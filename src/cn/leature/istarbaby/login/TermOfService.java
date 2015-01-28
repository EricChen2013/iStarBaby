package cn.leature.istarbaby.login;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzWebView;
import cn.leature.istarbaby.network.HttpClientUtil;
import cn.leature.istarbaby.utils.ConstantDef;

public class TermOfService extends Activity implements OnClickListener
{

	private LzWebView mLzWebView = null;
//	private CustomWebView mLzWebView;
	private LinearLayout mWeb_layout;
	private LinearLayout mBackward;
	private LinearLayout mForward;
	private LinearLayout mRefresh;
	// private View mLayout;
	private View mInculd_layout;

	private int screenHeight;
	private String errorHtml = "<html><body><h1>Page not find!</h1></body></html>";
	private RelativeLayout mBottombar;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_webview);

		mWeb_layout = (LinearLayout) findViewById(R.id.webview_layout);
		mBackward = (LinearLayout) findViewById(R.id.image_backward);
		mForward = (LinearLayout) findViewById(R.id.image_forward);
		mRefresh = (LinearLayout) findViewById(R.id.image_refresh);
		mBottombar = (RelativeLayout) findViewById(R.id.canback_layout);

		mLzWebView = new LzWebView(this);
//		mLzWebView = new CustomWebView(this);

		mRefresh.setOnClickListener(this);
		mBackward.setOnClickListener(this);
		mForward.setOnClickListener(this);

		mWeb_layout.addView(mLzWebView);
		
	
		// 设置titile
		initTitlebar();
		mLzWebView.setWillNotCacheDrawing(false);
		mLzWebView.setAlwaysDrawnWithCacheEnabled(true);
		mLzWebView.setScrollbarFadingEnabled(true);
		mLzWebView.setSaveEnabled(true);
		mLzWebView.setProgressStyle(LzWebView.Circle);
		mLzWebView.setClickable(true);
		// mLJWebView.setUseWideViewPort(true);
		mLzWebView.setSupportZoom(true);
		mLzWebView.setBuiltInZoomControls(true);
		// mLzWebView.setJavaScriptEnabled(true);
		mLzWebView.setFocusableInTouchMode(true);
		mLzWebView.setFocusable(true);
		mLzWebView.setAnimationCacheEnabled(false);
		mLzWebView.setDrawingCacheEnabled(true);
		mLzWebView.setCacheMode(WebSettings.LOAD_NO_CACHE);
		mLzWebView.getSettings().setDefaultTextEncodingName("UTF-8");  
		mLzWebView.setWebViewClient(new WebViewClient()
		{
			public boolean shouldOverrideUrlLoading(WebView view, String url)
			{
				DisplayMetrics dm = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);
				screenHeight = dm.heightPixels;

				int height = screenHeight - mInculd_layout.getHeight()
						- mBottombar.getHeight();

				ViewGroup.LayoutParams params = mWeb_layout.getLayoutParams();
				params.height = height;
				mWeb_layout.setLayoutParams(params);
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl)
			{
				super.onReceivedError(view, errorCode, description, failingUrl);

				view.loadData(errorHtml, "text/html", "UTF-8");
				Log.i("5555555555",
						"-MyWebViewClient->onReceivedError()--\n errorCode="
								+ errorCode + " \ndescription=" + description
								+ " \nfailingUrl=" + failingUrl);
				return;

			}

		});

		mLzWebView.loadUrl(HttpClientUtil.SERVER_PATH
				+ ConstantDef.cUrlAgreement);

	}

	private void initTitlebar()
	{


		// 设置顶部导航
		TextView titleBarTitle = (TextView) this
				.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("服务条款");
		ImageView titleimage = (ImageView) this
				.findViewById(R.id.titlebar_front_image);
		titleimage.setVisibility(View.GONE);

		ImageButton backBtn = (ImageButton) this
				.findViewById(R.id.titlebar_leftbtn);
		backBtn.setBackgroundResource(R.drawable.selector_hd_close);
		backBtn.setOnClickListener(this);

		ImageButton titleBarPost = (ImageButton) this
				.findViewById(R.id.titlebar_rightbtn1);
		titleBarPost.setVisibility(View.INVISIBLE);

		mInculd_layout = findViewById(R.id.include_layout);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.titlebar_leftbtn:

			backPrePage();

			break;
		case R.id.image_refresh:

			refresh();
			break;

		case R.id.image_backward:

			backwoard();

			break;
		case R.id.image_forward:

			forwoard();

			break;
		default:
			break;
		}

	}

	private void forwoard()
	{
		mLzWebView.goForward();

	}

	private void backwoard()
	{

		if (mLzWebView != null)
		{
			mLzWebView.goBack();

		}

	}

	private void refresh()
	{
		if (mLzWebView != null)
		{

			mLzWebView.loadUrl(mLzWebView.getUrl());
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK && mLzWebView.canGoBack())
		{
			mLzWebView.goBack();// 返回前一个页面
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void backPrePage()
	{
		setResult(05);
		this.finish();
		// 结束当前页面
		this.overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_bottom);
	}
}