package cn.leature.istarbaby.wxapi;

import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboShareException;
import com.sina.weibo.sdk.utils.Utility;

import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.network.HttpClientUtil;
import cn.leature.istarbaby.setting.SettingFriendsActivity;
import cn.leature.istarbaby.setting.SettingMySurfaceActivity;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.WxPhotoUtil;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class WXEntryActivity extends Activity implements OnClickListener,
		IWXAPIEventHandler, IWeiboHandler.Response {

	private int mQQShareType = QQShare.SHARE_TO_QQ_TYPE_DEFAULT;
	private int mExtarFlag = 0x00;
	public static Tencent mTencent;
	private static final String mWeiShareTitle = "iStar育儿宝";
	private static final String mWeiShareMessage = "iStar育儿宝是以宝宝成长为主题的一款手机应用，结合M1婴儿监视器的使用，能够随时随地关注宝宝举动，远程照顾宝宝，还能让亲友一起分享宝宝的欢乐时刻，成长时刻；通过育儿百科，能找到针对婴幼儿方式状况时候的解答，还能在线订购安全的婴儿产品。";

	private TextView titleBarTitle;
	private ImageButton titleBarBack;
	private ImageButton titleBarEdit;
	private TextView mMySurface, mDuanxin, mWeixin, mWeixinfriends;
	private TextView mXinLangweibo, mQqfenxiang;

	/** 微信分享 */
	private IWXAPI api;
	/* 新浪微博 */
	private IWeiboShareAPI mWeiboShareAPI;
	private WBShareItemView mShareWebPageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_recommend);

		initUI();
		setCustomTitleBar();

		// 通过WXAPIFactory工厂，获取IWXAPI的实例
		api = WXAPIFactory.createWXAPI(this, ConstantDef.WXAPP_ID, false);
		api.registerApp(ConstantDef.WXAPP_ID);
		api.handleIntent(getIntent(), this);
		// 新浪微博
		// 创建微博分享接口实例
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this,
				ConstantDef.WBAPP_ID);
		// 如果未安装微博客户端，设置下载微博对应的回调
		if (!mWeiboShareAPI.isWeiboAppInstalled()) {
			mWeiboShareAPI
					.registerWeiboDownloadListener(new IWeiboDownloadListener() {
						@Override
						public void onCancel() {
							// Toast.makeText(WXEntryActivity.this, "分享失败",
							// Toast.LENGTH_SHORT).show();
						}
					});
		}

		// 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
		// 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
		// 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
		// 失败返回 false，不调用上述回调
		if (savedInstanceState != null) {
			mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
		}

		/*
		 * qq控件分享
		 */
		mTencent = Tencent.createInstance(ConstantDef.QQAPP_ID, this);
		// setTitle(null);
		// setLeftButton("返回", new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// setResult(ConstantDef.RESULT_CODE_QQSHAREACTIVITY);
		// finish();
		// }
		// });
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mTencent != null) {
			mTencent.releaseResource();
		}
		QQShareUtil.release();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		// 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
		// 来接收微博客户端返回的数据；执行成功，返回 true，并调用
		// {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
		mWeiboShareAPI.handleWeiboResponse(intent, this);
	}

	private void initUI() {
		mMySurface = (TextView) findViewById(R.id.myface_textview);
		mDuanxin = (TextView) findViewById(R.id.duanxin_textview);
		mWeixin = (TextView) findViewById(R.id.wxallfrends_textview);
		mWeixinfriends = (TextView) findViewById(R.id.wxfrend_textview);
		mXinLangweibo = (TextView) findViewById(R.id.weibo_textview);
		mQqfenxiang = (TextView) findViewById(R.id.qqshare_textview);

		mQqfenxiang.setOnClickListener(this);
		mMySurface.setOnClickListener(this);
		mDuanxin.setOnClickListener(this);
		mWeixin.setOnClickListener(this);
		mWeixinfriends.setOnClickListener(this);
		mXinLangweibo.setOnClickListener(this);

		mShareWebPageView = new WBShareItemView(this);
		mShareWebPageView.initWithRes(R.string.weibosdk_share_webpage_title,
				R.drawable.ic_launcher, R.string.weibosdk_share_webpage_title,
				R.string.weibosdk_share_webpage_desc,
				R.string.weibosdk_share_webpage_url);
	}

	private void setCustomTitleBar() {

		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("好友分享");

		titleBarBack = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarBack.setOnClickListener((this));
		titleBarBack.setBackgroundResource(R.drawable.selector_hd_bk);

		titleBarEdit = (ImageButton) this.findViewById(R.id.titlebar_rightbtn1);
		titleBarEdit.setVisibility(View.INVISIBLE);

	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.titlebar_leftbtn:
			backPrePage();
			break;
		case R.id.myface_textview:

			intent.setClass(this, SettingMySurfaceActivity.class);
			startActivityForResult(intent,
					ConstantDef.REQUEST_CODE_SETTINGMYSURFACE);

			break;
		case R.id.duanxin_textview:
			intent.setClass(this, SettingFriendsActivity.class);
			startActivityForResult(intent,
					ConstantDef.REQUEST_CODE_SETTINGMYSURFACE);
			break;
		case R.id.wxallfrends_textview:// 分享到朋友圈
			fenxiangpengyouquan();
			break;
		case R.id.wxfrend_textview:// 分享到朋友
			fenxiangpengyou();
			break;
		case R.id.weibo_textview:
			xinlangweibofenxiang();// 新浪微博分享
			break;
		case R.id.qqshare_textview:
			qqFriendsShare();
			break;
		default:
			break;
		}
	}

	private void xinlangweibofenxiang() {
		try {
			// 检查微博客户端环境是否正常，如果未安装微博，弹出对话框询问用户下载微博客户端
			if (mWeiboShareAPI.checkEnvironment(true)) {

				// 注册第三方应用 到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
				// 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
				mWeiboShareAPI.registerApp();

				if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
					int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();

					// 1. 初始化微博的分享消息
					// 用户可以分享文本、图片、网页、音乐、视频中的一种
					WeiboMessage weiboMessage = new WeiboMessage();

					weiboMessage.mediaObject = getWebpageObj();

					// 2. 初始化从第三方到微博的消息请求
					SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
					// 用transaction唯一标识一个请求
					request.transaction = String.valueOf(System
							.currentTimeMillis());
					request.message = weiboMessage;

					// 3. 发送请求消息到微博，唤起微博分享界面
					mWeiboShareAPI.sendRequest(request);
				}
			}
		} catch (WeiboShareException e) {
			e.printStackTrace();
			Toast.makeText(WXEntryActivity.this, e.getMessage(),
					Toast.LENGTH_LONG).show();
		}

	}

	private WebpageObject getWebpageObj() {

		WebpageObject mediaObject = new WebpageObject();
		mediaObject.identify = Utility.generateGUID();
		mediaObject.title = mShareWebPageView.getTitle();
		mediaObject.description = mShareWebPageView.getShareDesc();

		// 设置 Bitmap 类型的图片到视频对象里
		mediaObject.setThumbImage(mShareWebPageView.getThumbBitmap());
		mediaObject.actionUrl = mShareWebPageView.getShareUrl();
		mediaObject.defaultText = "iStar育儿宝";
		return mediaObject;
	}

	private void fenxiangpengyouquan() {
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = HttpClientUtil.SHARE_PATH;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = mWeiShareTitle;
		msg.description = mWeiShareMessage;
		Bitmap thumb = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);
		msg.thumbData = WxPhotoUtil.bmpToByteArray(thumb, true);

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.scene = SendMessageToWX.Req.WXSceneTimeline;
		req.message = msg;
		api.sendReq(req);

	}

	private void fenxiangpengyou() {
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = HttpClientUtil.SHARE_PATH;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = mWeiShareTitle;
		msg.description = mWeiShareMessage;
		Bitmap thumb = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);
		msg.thumbData = WxPhotoUtil.bmpToByteArray(thumb, true);

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.scene = SendMessageToWX.Req.WXSceneSession;
		req.message = msg;
		api.sendReq(req);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backPrePage();
		}

		return false;
	}

	private void backPrePage() {
		Intent intent = new Intent();
		this.setResult(Activity.RESULT_CANCELED, intent);
		this.finish();

		// 设定退出动画
		this.overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_right);
	}

	@Override
	public void onReq(BaseReq req) {
		this.finish();
	}

	@Override
	public void onResp(BaseResp resp) {
		String result = "";

		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			result = "发送成功";
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:

			result = "发送取消";
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = "发送被拒绝";
			break;
		default:
			result = "发送返回";
			break;
		}

		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
		// TODO 微信分享 成功之后调用接口
		this.finish();
	}

	@Override
	public void onResponse(BaseResponse baseResp) {
		String result = "";
		switch (baseResp.errCode) {
		case WBConstants.ErrorCode.ERR_OK:
			result = "分享成功。";
			break;
		case WBConstants.ErrorCode.ERR_CANCEL:
			result = "取消分享。";
			break;
		case WBConstants.ErrorCode.ERR_FAIL:
			result = "分享失败。";
			break;
		}
		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
	}

	private void qqFriendsShare() {
		final Bundle params = new Bundle();

		params.putString(QQShare.SHARE_TO_QQ_TITLE, mWeiShareTitle);
		params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,
				HttpClientUtil.SHARE_PATH);
		params.putString(QQShare.SHARE_TO_QQ_SUMMARY, mWeiShareMessage);

		params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,
				"http://www.leature.cn/images/icon.png");

		params.putString(QQShare.SHARE_TO_QQ_APP_NAME, mWeiShareTitle);
		params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, mQQShareType);
		params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, mExtarFlag);

		params.putString(QQShare.SHARE_TO_QQ_AUDIO_URL,
				HttpClientUtil.SHARE_PATH);

		doShareToQQ(params);

	}

	private void doShareToQQ(Bundle params) {
		mTencent.shareToQQ(WXEntryActivity.this, params, qqShareListener);
	}

	IUiListener qqShareListener = new IUiListener() {
		@Override
		public void onCancel() {
			if (mQQShareType != QQShare.SHARE_TO_QQ_TYPE_IMAGE) {
				QQShareUtil.toastMessage(WXEntryActivity.this, "取消分享。");
			}
		}

		@Override
		public void onComplete(Object response) {
			// TODO Auto-generated method stub
			QQShareUtil.toastMessage(WXEntryActivity.this, "完成分享。");
		}

		@Override
		public void onError(UiError e) {
			// TODO Auto-generated method stub
			QQShareUtil.toastMessage(WXEntryActivity.this, "分享失败: "
					+ e.errorMessage, "e");
		}
	};
}
