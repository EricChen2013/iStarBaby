package cn.leature.istarbaby.daily;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.daily.SelectPhotosAdapter.OnItemClickPhotosClass;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.FileTraversal;
import cn.leature.istarbaby.utils.SelectPhotosCallBack;
import cn.leature.istarbaby.utils.SelelctPhotosUtils;

public class SelectPhotosActivity extends Activity implements OnClickListener {

	private FileTraversal mFileTraversal;

	private SelelctPhotosUtils mPhotosUtils;

	private ArrayList<String> mImageSelectedList;

	// TitleBar的控件
	private TextView titleBarTitle;
	private ImageButton titleBarBack, titleBarDone;

	private Intent mIntent;

	// 图片的宽高
	private int mPhotoWidthHeight;
	// 图片列表的列数
	private final int cPhotoListColNum = 4;
	// 图片列表的图片间隔
	private final int cPhotoListCellSpace = 4;
	// 图片列表和边框的间距
	private final int cPhotoListMargin = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_select_photos);

		mIntent = this.getIntent();

		// 屏幕宽高
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		// 计算列表中图片的大小
		mPhotoWidthHeight = dm.widthPixels - cPhotoListMargin * 2
				- cPhotoListCellSpace * (cPhotoListColNum - 1);
		mPhotoWidthHeight /= cPhotoListColNum;

		// 自定义title bar
		setCustomTitleBar();

		// 参数取得（选择的图片目录）
		Bundle bundle = getIntent().getExtras();
		mFileTraversal = bundle
				.getParcelable(ConstantDef.cdImageFolderSelected);

		SelectPhotosAdapter imgsAdapter = new SelectPhotosAdapter(this,
				mPhotoWidthHeight, mFileTraversal.getFileContentList(),
				onItemClickPhotosClass);

		GridView imgGridView = (GridView) this
				.findViewById(R.id.sel_photes_gridView1);
		imgGridView.setAdapter(imgsAdapter);

		mPhotosUtils = new SelelctPhotosUtils(this);
		mImageSelectedList = new ArrayList<String>();
	}

	private void setCustomTitleBar() {
		// 设置顶部导航
		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("选择照片");

		titleBarBack = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarBack.setBackgroundResource(R.drawable.selector_hd_bk);
		titleBarBack.setOnClickListener(this);

		titleBarDone = (ImageButton) this.findViewById(R.id.titlebar_rightbtn1);
		titleBarDone.setBackgroundResource(R.drawable.selector_hd_cmp);
		titleBarDone.setEnabled(false);
		titleBarDone.setOnClickListener(this);
	}

	OnItemClickPhotosClass onItemClickPhotosClass = new OnItemClickPhotosClass() {

		@Override
		public void OnItemClick(View v, int position, CheckBox checkBox) {
			String filePath = mFileTraversal.getFileContentList().get(position);
			if (checkBox.isChecked()) {
				checkBox.setChecked(false);

				// 列表中删除取消选择的对象
				for (int i = 0; i < mImageSelectedList.size(); i++) {
					if (filePath.equals(mImageSelectedList.get(i))) {
						mImageSelectedList.remove(i);
					}
				}

				if (mImageSelectedList.size() > 0) {
					titleBarTitle.setText("选择照片(" + mImageSelectedList.size()
							+ "张)");
				} else {
					titleBarTitle.setText("选择照片");
					titleBarDone.setEnabled(false);
				}
			} else {
				try {
					if (mImageSelectedList.size() >= 9) {
						Toast.makeText(SelectPhotosActivity.this,
								"最多只能选择9张照片。", Toast.LENGTH_LONG).show();
					} else {
						checkBox.setChecked(true);
						titleBarDone.setEnabled(true);

						ImageView imageView = iconImage(filePath, position,
								checkBox);
						if (imageView != null) {
							mImageSelectedList.add(filePath);

							titleBarTitle.setText("选择照片("
									+ mImageSelectedList.size() + "张)");
						}
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void checkItemSelected(int position, CheckBox checkBox) {

			String filePath = mFileTraversal.getFileContentList().get(position);
			for (int i = 0; i < mImageSelectedList.size(); i++) {
				if (mImageSelectedList.get(i).equals(filePath)) {
					checkBox.setChecked(true);
					return;
				}
			}
		}
	};

	SelectPhotosCallBack imgSelectedCallBack = new SelectPhotosCallBack() {

		@Override
		public void resultImgCall(ImageView imageView, Bitmap bitmap) {
			imageView.setImageBitmap(bitmap);
		}
	};

	@SuppressLint("NewApi")
	public ImageView iconImage(String filePath, int index, CheckBox checkBox)
			throws FileNotFoundException {
		ImageView imageView = new ImageView(this);
		imageView.setBackgroundResource(R.drawable.ic_launcher);

		// 设置透明
		float alpha = 100.0f;
		imageView.setAlpha(alpha);

		mPhotosUtils.imgExcute(imageView, imgSelectedCallBack, filePath);

		return imageView;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.select_phones, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_leftbtn:
			cancelSelectPhoto();
			break;
		case R.id.titlebar_rightbtn1:
			doneSelectPhotos();
			break;
		default:
			break;
		}
	}

	private void doneSelectPhotos() {

		// 将选择图片文件设定为参数
		Bundle bundle = new Bundle();
		bundle.putStringArrayList(ConstantDef.cdImagesSelected,
				mImageSelectedList);

		mIntent.putExtras(bundle);
		this.setResult(Activity.RESULT_OK, mIntent);
		this.finish();
	}

	private void cancelSelectPhoto() {
		// 直接返回
		this.setResult(Activity.RESULT_CANCELED, mIntent);
		this.finish();
	}
}
