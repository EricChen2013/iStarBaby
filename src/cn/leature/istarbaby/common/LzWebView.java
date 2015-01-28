package cn.leature.istarbaby.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import cn.leature.istarbaby.R;

public class LzWebView extends RelativeLayout
{

	public static int Circle = 0x01;
	private Context context;
	private WebView mWebView = null; //
	private RelativeLayout progressBar_circle = null; // 包含圆形进度条的布局
	private boolean isAdd = false; // 判断是否已经加入进度条

	private int progressStyle = Circle;// 进度条样式,Circle表示为圆形

	public LzWebView(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		init();
	}

	public LzWebView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = context;
		init();
	}

	public LzWebView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		this.context = context;
		init();
	}

	private void init()
	{
		mWebView = new WebView(context);
		this.addView(mWebView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);

		mWebView.setWebChromeClient(new WebChromeClient()
		{

			@Override
			public void onProgressChanged(WebView view, int newProgress)
			{
				// TODO Auto-generated method stub
				super.onProgressChanged(view, newProgress);
				if (newProgress == 100)
				{

					if (progressStyle == Circle)
					{
						if (progressBar_circle!=null)
						{
							progressBar_circle.setVisibility(View.GONE);
						}
			
					}

				}
				else
				{
					if (!isAdd)
					{
						if (progressStyle == Circle)
						{

							progressBar_circle = (RelativeLayout) LayoutInflater
									.from(context).inflate(
											R.layout.progress_circle, null);
							LzWebView.this.addView(progressBar_circle,
									LayoutParams.MATCH_PARENT,
									LayoutParams.MATCH_PARENT);
						}

						isAdd = true;
					}
					progressBar_circle.setVisibility(View.VISIBLE);

				}
			}
		});
	}


	public void setProgressStyle(int style)
	{
		progressStyle = style;
	}

	public void setClickable(boolean value)
	{
		mWebView.setClickable(value);
	}

	public void setUseWideViewPort(boolean value)
	{
		mWebView.getSettings().setUseWideViewPort(value);
	}

	public void setSupportZoom(boolean value)
	{
		mWebView.getSettings().setSupportZoom(value);
	}

	public void setBuiltInZoomControls(boolean value)
	{
		mWebView.getSettings().setBuiltInZoomControls(value);
	}

	public void setJavaScriptEnabled(boolean value)
	{
		mWebView.getSettings().setJavaScriptEnabled(value);
	}

	public void setCacheMode(int value)
	{
		mWebView.getSettings().setCacheMode(value);
	}

	public void setWebViewClient(WebViewClient value)
	{
		mWebView.setWebViewClient(value);
	}

	public void loadUrl(String url)
	{
		mWebView.loadUrl(url);
	}

	public void goBack()
	{
		mWebView.goBack();
	}

	public void goForward()
	{
		mWebView.goForward();
	}

	public String getUrl()
	{
		return mWebView.getUrl();
	}

	public boolean canGoBack()
	{
		return mWebView.canGoBack();
	}

	public WebSettings getSettings()
	{
		return mWebView.getSettings();
	}

}
