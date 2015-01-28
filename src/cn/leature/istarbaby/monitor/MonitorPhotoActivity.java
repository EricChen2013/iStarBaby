package cn.leature.istarbaby.monitor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.gallery.GalleyShowActivity;
import cn.leature.istarbaby.utils.DensityUtil;
import cn.leature.istarbaby.utils.LzViewHolder;
import cn.leature.istarbaby.utils.SaveBitmapFile;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MonitorPhotoActivity extends Fragment implements OnClickListener {

	private ImageButton mTitleBarDone;
	private TextView titleBarTitle;
	ArrayList<String> mData = null;
	// 图片的宽高
	private int mPhotoWidthHeight;
	// 图片列表的列数
	private final static int cPhotoListColNum = 4;
	// 图片列表的图片间隔(dip)
	private final static int cPhotoListCellSpace = 4;
	// 图片列表和边框的间距(dip)
	private final static int cPhotoListMargin = 4;

	private static GridView mPhotoView;
	private int fragmentlayout;
	private ImageView mBtn_Add, mBtn_List, mBtn_photo, mBtn_Event;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private RelativeLayout mLayout;
	private Button mQuding;
	private Button mQuxiao;
	private boolean isDeleteModle;
	private List<String> mDeleteed = new ArrayList<String>();
	private PhotoImageApdate mAdapter;
	private TextView mDeleteTitle;

	public static MonitorPhotoActivity newInstance(int frament,
			ImageView mBtn_List, ImageView mBtn_Event, ImageView mBtn_Add,
			ImageView mBtn_photo, TextView mTitleBarTitle,
			ImageButton mTitleBarPost) {
		MonitorPhotoActivity newFragment = new MonitorPhotoActivity();
		newFragment.fragmentlayout = frament;
		newFragment.mBtn_List = mBtn_List;
		newFragment.mBtn_Event = mBtn_Event;
		newFragment.mBtn_Add = mBtn_Add;
		newFragment.mBtn_photo = mBtn_photo;
		newFragment.titleBarTitle = mTitleBarTitle;
		newFragment.mTitleBarDone = mTitleBarPost;

		return newFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View inflate = inflater.inflate(R.layout.activity_monitor_photo, null);

		initView(inflate);

		// 设置bottombar
		setBottomBarImage();

		// 计算列表中图片的大小
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		mPhotoWidthHeight = dm.widthPixels
				- DensityUtil.dip2px(getActivity(), cPhotoListMargin) * 2
				- DensityUtil.dip2px(getActivity(), cPhotoListCellSpace)
				* (cPhotoListColNum - 1);
		mPhotoWidthHeight /= cPhotoListColNum;

		// 初始化
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.nophoto)
				.showImageForEmptyUri(R.drawable.nophoto)
				.showImageOnFail(R.drawable.nophoto).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		return inflate;
	}

	private void setBottomBarImage() {

		mBtn_List.setImageResource(R.drawable.monitor_tab_list);

		mBtn_Event.setImageResource(R.drawable.monitor_tab_event);

		mBtn_photo.setImageResource(R.drawable.monitor_tab_gallery_on);

		mBtn_Add.setImageResource(R.drawable.monitor_tab_add);

	}

	private void initView(View inflate) {

		mPhotoView = (GridView) inflate.findViewById(R.id.monitor_gridview);

		mLayout = (RelativeLayout) inflate
				.findViewById(R.id.monitor_photo_layout);
		mQuding = (Button) inflate.findViewById(R.id.quxiao);
		mQuxiao = (Button) inflate.findViewById(R.id.queding);
		mQuding.setOnClickListener(this);
		mQuxiao.setOnClickListener(this);
		mDeleteTitle = (TextView) inflate.findViewById(R.id.txt_delete_title);

		mTitleBarDone.setVisibility(View.INVISIBLE);
		titleBarTitle.setText("影像快照");

		mData = new ArrayList<String>();
		// 得到文件名下面的所有图片名
		String pathName = SaveBitmapFile.getSnapshotPath();
		File file = new File(pathName);

		File[] fs = file.listFiles();

		if (fs.length > 0) {
			// 按时间排序
			Arrays.sort(fs, new CompratorByLastModified());
			for (int i = 0; i < fs.length; i++) {

				String str = fs[i].getPath().substring(
						fs[i].getPath().length() - 4, fs[i].getPath().length());

				if (".png".equals(str))

				{
					mData.add(fs[i].getPath());
				}

			}
			mAdapter = new PhotoImageApdate();
			mPhotoView.setAdapter(mAdapter);
		}
	}

	static class CompratorByLastModified implements Comparator<File> {
		public int compare(File f1, File f2) {
			long diff = f1.lastModified() - f2.lastModified();
			if (diff > 0)
				return 1;
			else if (diff == 0)
				return 0;
			else
				return -1;
		}

		public boolean equals(Object obj) {
			return true;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.quxiao:
			isDeleteModle = false;
			mDeleteed.clear();
			mLayout.setVisibility(View.GONE);
			mDeleteTitle.setText("选择项目");
			break;
		case R.id.queding:
			saveselector();
			break;
		default:
			break;
		}
	}

	class imgListener implements OnClickListener {
		// 定义一个 Button 类型的变量
		private ImageView seleBtn;
		private boolean ischeak;
		private int position;

		/*
		 * 一个构造方法, 将Button对象做为参数
		 */
		private imgListener(ImageView seleBtn, boolean ischeak, int position) {
			this.seleBtn = seleBtn;
			this.position = position;
			this.ischeak = ischeak;
		}

		public void onClick(View v) {
			ischeak = !ischeak;

			if (ischeak) {
				seleBtn.setVisibility(View.VISIBLE);
				mDeleteed.add("" + position);// 选中的图片URL

			} else {

				seleBtn.setVisibility(View.INVISIBLE);

				mDeleteed.remove("" + position);// 取消选中的图片URL
			}
			if (mDeleteed.size() == 0) {
				mDeleteTitle.setText("选择项目");
			} else {
				mDeleteTitle.setText("已选择 " + mDeleteed.size() + " 张图片");
			}
		}
	}

	class PhotoImageApdate extends BaseAdapter {

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			convertView = getActivity().getLayoutInflater().inflate(
					R.layout.monitorphoto_itemlayout, null);
			ImageView mImageView = LzViewHolder.get(convertView,
					R.id.monitorphoto_itemphoto);
			// 设置显示图片的大小
			mImageView.setLayoutParams(new RelativeLayout.LayoutParams(
					mPhotoWidthHeight, mPhotoWidthHeight));
			// 重新设置图片大小
			mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			mImageView.setPadding(0, 0, 0, 0);
			mImageView.setImageResource(R.drawable.mn_icon_howto);

			ImageView mSelectorImageView = LzViewHolder.get(convertView,
					R.id.monitorphoto_itemselector);

			String imageUrlPath = mData.get(position);

			if (imageUrlPath.length() > 0) {

				imageLoader.displayImage("file://" + imageUrlPath, mImageView,
						options, null);

			}

			mImageView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					if ("".equals(mData.size())) {
						return false;
					}
					isDeleteModle = true;
					mLayout.setVisibility(View.VISIBLE);

					return false;
				}
			});
			if (isDeleteModle) {
				boolean isChecked = isVaccineChecked(position);
				if (isChecked) {
					mSelectorImageView.setVisibility(View.VISIBLE);
				} else {
					mSelectorImageView.setVisibility(View.INVISIBLE);
				}

				mImageView.setOnClickListener(new imgListener(
						mSelectorImageView, isChecked, position));

			} else {
				mImageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(getActivity(), GalleyShowActivity.class);

						// 传递参数：Image URL
						Bundle bundle = new Bundle();
						bundle.putInt("StartIndex",
								Integer.parseInt("" + position));
						bundle.putStringArrayList("ImageList", mData);
						bundle.putString("SD", "montior");
						intent.putExtras(bundle);

						startActivity(intent);
					}
				});
			}

			return convertView;
		}

	}

	private boolean isVaccineChecked(int position) {

		for (int j = 0; j < mDeleteed.size(); j++) {
			String item = mDeleteed.get(j);
			if (("" + position).equals(item)) {
				return true;
			}
		}

		return false;
	}

	private void saveselector() {
		for (int i = mData.size() - 1; i >= 0; i--) {
			for (int j = 0; j < mDeleteed.size(); j++) {
				if (("" + i).equals(mDeleteed.get(j))) {

					File file2 = new File(mData.get(i));
					file2.delete();

					mData.remove(i);
					break;
				}
			}
		}

		mLayout.setVisibility(View.GONE);
		mDeleteTitle.setText("选择项目");
		mAdapter.notifyDataSetChanged();
		isDeleteModle = false;
		mDeleteed.clear();
	}
}
