package cn.leature.istarbaby.daily;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.FileTraversal;
import cn.leature.istarbaby.utils.SelelctPhotosUtils;

public class SelectPhotoListActivity extends Activity implements
		OnItemClickListener, OnClickListener {

	// 选择照片（Activity跳转用）
	protected static final int SELECT_PHOTOS_REQUEST_CODE = 1;

	private List<FileTraversal> mLocalList;

	// TitleBar的控件
	private TextView titleBarTitle;
	private ImageButton titleBarCancel;

	private Intent mIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_select_photo_list);

		mIntent = this.getIntent();

		// 自定义title bar
		setCustomTitleBar();

		ListView listView = (ListView) this
				.findViewById(R.id.sel_photolist_view);

		// 全部图片文件列表
		SelelctPhotosUtils selPhotosUtil = new SelelctPhotosUtils(this);
		mLocalList = selPhotosUtil.localImgFileList();

		List<HashMap<String, String>> listdata = new ArrayList<HashMap<String, String>>();
		if (mLocalList != null) {
			for (int i = 0; i < mLocalList.size(); i++) {
				HashMap<String, String> map = new HashMap<String, String>();

				FileTraversal fileTraversal = mLocalList.get(i);
				// 图片所在的目录名
				map.put(SelectPhotoListAdapter.cFilePathName,
						fileTraversal.getFilePathName());

				List<String> fileContentList = fileTraversal
						.getFileContentList();
				// 该目录下图片张数
				map.put(SelectPhotoListAdapter.cFileCount,
						fileContentList.size() + "张");
				// 该目录下第一张图片
				map.put(SelectPhotoListAdapter.cImgFileName, fileContentList
						.get(0) == null ? null : (fileContentList.get(0)));

				listdata.add(map);
			}
		}

		SelectPhotoListAdapter listAdapter = new SelectPhotoListAdapter(this,
				listdata);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(this);
	}

	private void setCustomTitleBar() {
		// 设置顶部导航
		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("选择相册");

		titleBarCancel = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarCancel.setBackgroundResource(R.drawable.selector_hd_ccl);
		titleBarCancel.setOnClickListener(this);

		ImageButton titleBarRight = (ImageButton) this
				.findViewById(R.id.titlebar_rightbtn1);
		titleBarRight.setVisibility(View.INVISIBLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.select_phone_list, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Intent intent = new Intent(this, SelectPhotosActivity.class);

		// 传递参数（文件列表）
		Bundle bundle = new Bundle();
		bundle.putParcelable(ConstantDef.cdImageFolderSelected,
				mLocalList.get(position));
		intent.putExtras(bundle);

		startActivityForResult(intent, SELECT_PHOTOS_REQUEST_CODE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_leftbtn:
			cancelSelectPhoto();
			break;
		default:
			break;
		}
	}

	private void cancelSelectPhoto() {
		// 取消时候，直接返回
		this.setResult(Activity.RESULT_CANCELED, mIntent);
		this.finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == SELECT_PHOTOS_REQUEST_CODE) {
				// 将选择图片文件设定为参数
				Bundle bundle = data.getExtras();

				mIntent.putExtras(bundle);
				this.setResult(Activity.RESULT_OK, mIntent);
				this.finish();
			}
		}
	}
}
