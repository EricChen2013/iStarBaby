package cn.leature.istarbaby.gallery;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzBaseActivity;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.domain.DailyDetailInfo;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.domain.UserInfo;
import cn.leature.istarbaby.network.HttpClientUtil;
import cn.leature.istarbaby.network.HttpPostUtil;
import cn.leature.istarbaby.network.HttpPostUtil.OnPostProcessListener;
import cn.leature.istarbaby.slidingmenu.SlidingMenu;
import cn.leature.istarbaby.utils.DensityUtil;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class GalleryActivity extends LzBaseActivity implements
		OnPostProcessListener, OnClickListener {

	// 发布新消息的URL
	private final static String cGalleryListUrl = "GalleryList.aspx";

	// 图片列表的列数
	private final static int cPhotoListColNum = 4;
	// 图片列表的图片间隔(dip)
	private final static int cPhotoListCellSpace = 4;
	// 图片列表和边框的间距(dip)
	private final static int cPhotoListMargin = 4;

	private LzProgressDialog mProgressDialog;
	private GridView mGridView;
	private SlidingMenu mSlidingMenu;

	private HttpPostUtil mHttpUtil;

	// 用户登录信息
	private UserInfo mLoginUserInfo;

	// 图片的宽高
	private int mPhotoWidthHeight;

	// 表示数据源
	private ArrayList<String> listData;

	// 图片列表取得成功（Message用）
	protected static final int GALLERY_LIST_DONE = 1;
	// 图片列表取得出错（Message用）
	protected static final int GALLERY_LIST_ERROR = -1;

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sliding_main);

		// 检查用户登录状况
		checkUserLogin();

		initView();

		getGalleryList();
		// 初始化
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.nophoto)
				.showImageForEmptyUri(R.drawable.nophoto)
				.showImageOnFail(R.drawable.nophoto).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	private void checkUserLogin() {
		// 取得用户登录信息
		mLoginUserInfo = LoginInfo.loadLoginUserInfo(this);

		// 用户名或密码为空，需要重新登录
		if (mLoginUserInfo.getUserSId().equals("")
				|| mLoginUserInfo.getPassword().equals("")) {

		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GALLERY_LIST_DONE:
				toGalleryListDone((String) msg.obj);
				break;
			default:
				// 错误处理
				toErrorMessage(msg.obj.toString());
				break;
			}

			super.handleMessage(msg);
		}
	};

	private void initView() {
		// 设置主菜单
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		mSlidingMenu = (SlidingMenu) findViewById(R.id.slidingMain);

		View leftView = getLayoutInflater()
				.inflate(R.layout.sliding_menu, null);
		View centerView = getLayoutInflater().inflate(
				R.layout.activity_gallery, null);

		mSlidingMenu.setLeftView(leftView);
		mSlidingMenu.setCenterView(centerView);

		// 设置顶部导航
		TextView titleBarTitle = (TextView) this
				.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("精彩写真");

		ImageButton showLeftMenu = (ImageButton) centerView
				.findViewById(R.id.titlebar_leftbtn);
		showLeftMenu.setBackgroundResource(R.drawable.selector_hd_menu);
		showLeftMenu.setOnClickListener(this);

		ImageButton titleBarPost = (ImageButton) this
				.findViewById(R.id.titlebar_rightbtn1);
		titleBarPost.setVisibility(View.INVISIBLE);

		// 计算列表中图片的大小
		mPhotoWidthHeight = dm.widthPixels
				- DensityUtil.dip2px(this, cPhotoListMargin) * 2
				- DensityUtil.dip2px(this, cPhotoListCellSpace)
				* (cPhotoListColNum - 1);
		mPhotoWidthHeight /= cPhotoListColNum;

		mGridView = (GridView) this.findViewById(R.id.gallery_gridview);

		mHttpUtil = HttpPostUtil.getInstance();
		mHttpUtil.setOnPostProcessListener(this);

	}

	protected void toErrorMessage(String msgString) {
		// 取得出错，显示提示信息
		mProgressDialog.dismiss();
		Toast.makeText(this, msgString, Toast.LENGTH_LONG).show();
	}

	private void getGalleryList() {
		mProgressDialog = new LzProgressDialog(this);
		mProgressDialog.set_Text("正在取得列表...", true);
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();

		// 日志List取得的参数设定
		Map<String, String> param = new HashMap<String, String>();
		param.put("UserId", mLoginUserInfo.getUserSId());
		mHttpUtil.sendPostMessage(cGalleryListUrl, param);
	}

	@Override
	public void onPostDone(int responseCode, String responseMessage) {

		Message msg = Message.obtain();
		msg.arg1 = responseCode;
		msg.obj = responseMessage;

		if (responseCode == HttpPostUtil.POST_SUCCESS_CODE) {
			msg.what = GALLERY_LIST_DONE;
		} else {
			msg.what = GALLERY_LIST_ERROR;
		}
		handler.sendMessage(msg);
	}

	protected void toGalleryListDone(String listResult) {
		listData = new ArrayList<String>();

		try {
			JSONArray jsonArray = new JSONArray(listResult);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				DailyDetailInfo detailInfo = new DailyDetailInfo(jsonObject);

				// 图片列表
				String[] imgFiles = detailInfo.getImage();
				if (imgFiles != null) {
					for (int j = 0; j < imgFiles.length; j++) {
						listData.add(imgFiles[j]);
					}

				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 关闭进度条
		mProgressDialog.dismiss();
		// 显示内容
		if ((listData != null) && (listData.size() > 0)) {
			mGridView.setAdapter(new ImagesAdapter(this));
		}
	}

	public class ImagesAdapter extends BaseAdapter {
		private Context mContext;
		HashMap<String, SoftReference<Bitmap>> mImageCache;

		public ImagesAdapter(Context context) {
			mContext = context;
			mImageCache = new HashMap<String, SoftReference<Bitmap>>();
		}

		public int getCount() {
			return listData.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;

			if (convertView == null) {
				imageView = new ImageView(mContext);
				// 设置显示图片的大小
				imageView.setLayoutParams(new GridView.LayoutParams(
						mPhotoWidthHeight, mPhotoWidthHeight));

				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(0, 0, 0, 0);
				imageView.setImageResource(R.drawable.nophoto);
			} else {
				imageView = (ImageView) convertView;
			}

			String imageUrlPath = listData.get(position);

			if (imageUrlPath.length() > 0) {
				imageUrlPath = HttpClientUtil.SERVER_PATH + imageUrlPath;
				imageView.setTag("" + position);

				// 加载图片
				imageLoader
						.displayImage(imageUrlPath, imageView, options, null);
				// 加载图片

			}

			// 图片的点击事件
			imageView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (v.getTag() == null) {
						return;
					}

					Intent intent = new Intent();
					intent.setClass(GalleryActivity.this,
							GalleyShowActivity.class);

					// 传递参数：Image URL
					Bundle bundle = new Bundle();
					bundle.putString("SD", "gallery");
					bundle.putInt("StartIndex",
							Integer.parseInt(v.getTag().toString()));
					bundle.putStringArrayList("ImageList", listData);
					intent.putExtras(bundle);

					startActivity(intent);
				}
			});

			return imageView;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_leftbtn:
			mSlidingMenu.showLeftView();
			break;
		default:
			break;
		}
	}

}
