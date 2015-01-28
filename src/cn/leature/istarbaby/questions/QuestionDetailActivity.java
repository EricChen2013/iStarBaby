package cn.leature.istarbaby.questions;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzWebView;
import cn.leature.istarbaby.domain.QuestionDetailInfo;
import cn.leature.istarbaby.domain.QuestionDetailInfo.QuestionDetailModel;
import cn.leature.istarbaby.domain.QuestionDetailInfo.QuestionListModel;
import cn.leature.istarbaby.network.HttpClientUtil;

public class QuestionDetailActivity extends Activity implements OnClickListener
{

	private TextView titleBarTitle;
	private ImageButton titleBarBack;
	private ImageButton titleBarEdit;
	private Bundle mBundle;
	private QuestionDetailModel mDetailModel;

	// private TextView mTitleText;
	private QuestionDetailInfo mQuestionDetailInfo;
	private LinearLayout mWeb_layout;
	private LinearLayout mBackward;
	private LinearLayout mForward;
	private LinearLayout mRefresh;
	private LzWebView mLzWebView = null;
	private View mLayout;
	private View mInculd_layout;
	private RelativeLayout mBottombarlayout;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question_detail);

		mBundle = getIntent().getExtras();
		mQuestionDetailInfo = (QuestionDetailInfo) getIntent()
				.getSerializableExtra("CatalogInfo");

		initUI();
		setCustomTitleBar();

	}

	private void initUI()
	{

		int list_position = mBundle.getInt("list_position");
		int position = mBundle.getInt("position");
		QuestionListModel questionInfo = mQuestionDetailInfo.getList().get(
				list_position - 1);

		mDetailModel = questionInfo.getQuestion_list().get(position);
		// 设置标题栏
		// mTitleText = (TextView) findViewById(R.id.question_detail_title);
		// mTitleText.setText(mDetailModel.getCatalog2_text());

		// 显示详细内容
		mWeb_layout = (LinearLayout) findViewById(R.id.question_detail_text);
		mInculd_layout = findViewById(R.id.include_layout);
		mBottombarlayout = (RelativeLayout) findViewById(R.id.canback_layout);
		// mDetailText.setText(mDetailModel.getDetail());
		String detail = mDetailModel.getDetail();

		String url;
		if (detail.indexOf("http") == 0)
		{
			url = detail;
		}
		else
		{
			url = HttpClientUtil.SERVER_PATH + detail;
		}
		mBackward = (LinearLayout) findViewById(R.id.image_backward);
		mForward = (LinearLayout) findViewById(R.id.image_forward);
		mRefresh = (LinearLayout) findViewById(R.id.image_refresh);
		mLayout = findViewById(R.id.canback_layout);

		mLzWebView = new LzWebView(this);
		mRefresh.setOnClickListener(this);
		mBackward.setOnClickListener(this);
		mForward.setOnClickListener(this);

		mWeb_layout.addView(mLzWebView);

		// 设置titile

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
		mLzWebView.setWebViewClient(new WebViewClient()
		{
			public boolean shouldOverrideUrlLoading(WebView view, String url)
			{

				DisplayMetrics dm = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);
				int screenHeight = dm.heightPixels;

				int height = screenHeight - mInculd_layout.getHeight()
						- mBottombarlayout.getHeight();

				ViewGroup.LayoutParams params = mWeb_layout.getLayoutParams();
				params.height = height;
				mWeb_layout.setLayoutParams(params);
				
				view.loadUrl(url);
				// mLayout.setVisibility(View.VISIBLE);
				return true;
			}
		});

		mLzWebView.loadUrl(url);

	}

	private void setCustomTitleBar()
	{

		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("育儿百科");

		titleBarBack = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarBack.setOnClickListener((this));
		titleBarBack.setBackgroundResource(R.drawable.selector_hd_bk);

		titleBarEdit = (ImageButton) this.findViewById(R.id.titlebar_rightbtn1);
		titleBarEdit.setVisibility(View.INVISIBLE);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{

		if (keyCode == KeyEvent.KEYCODE_BACK)
		{

			backPrePage();
		}

		return true;

	}

	private void backPrePage()
	{
		this.setResult(Activity.RESULT_CANCELED);
		this.finish();

		// 设定退出动画
		this.overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_right);
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
}
