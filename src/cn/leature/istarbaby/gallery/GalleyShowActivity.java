package cn.leature.istarbaby.gallery;

import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.network.HttpClientUtil;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class GalleyShowActivity extends Activity implements
		OnPageChangeListener
{

	private static final String STATE_POSITION = "STATE_POSITION";

	private DisplayImageOptions options;

	private ViewPager pager;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private TextView mTextView;
	private List<String> listData;

	private String[] imageUrls;

	private String mSdcard;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_galley_show);

		Bundle bundle = getIntent().getExtras();

		int pagerPosition = bundle.getInt("StartIndex");
		if (bundle.getStringArrayList("ImageList") != null)
		{
			listData = bundle.getStringArrayList("ImageList");

			imageUrls = new String[listData.size()];
			imageUrls = listData.toArray(imageUrls);
		}
		if (bundle.getStringArray("Dailyimage") != null)
		{
			String[] Dailyimage = bundle.getStringArray("Dailyimage");
			imageUrls = Dailyimage;
		}

		if (savedInstanceState != null)
		{
			pagerPosition = savedInstanceState.getInt(STATE_POSITION);
		}
		// 一个是sd卡存储一个是网页下载
		mSdcard = bundle.getString("SD");

		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.nophoto)
				.showImageOnFail(R.drawable.nophoto)
				.resetViewBeforeLoading(true).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(400)).build();

		pager = (MyViewPager) findViewById(R.id.my_pager);
		pager.setAdapter(new ImagePagerAdapter(imageUrls));
		pager.setCurrentItem(pagerPosition);
		pager.setOnPageChangeListener(this);

		// 显示图片张数
		mTextView = (TextView) findViewById(R.id.gallery_text_count);
		mTextView.setText((pagerPosition + 1) + " / " + imageUrls.length);
	}

	private class ImagePagerAdapter extends PagerAdapter
	{

		private String[] images;
		private LayoutInflater inflater;

		ImagePagerAdapter(String[] images)
		{
			this.images = images;
			inflater = getLayoutInflater();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object)
		{
			container.removeView((View) object);
		}

		@Override
		public int getCount()
		{
			return images.length;
		}

		@Override
		public Object instantiateItem(ViewGroup view, final int position)
		{
			View imageLayout = inflater.inflate(R.layout.item_pager_image,
					view, false);
			assert imageLayout != null;
			PhotoView imageView = (PhotoView) imageLayout
					.findViewById(R.id.image);

			String imagePath;
			if (mSdcard.equals("montior"))
			{

				imagePath = "file://" + images[position];
			}
			else
			{
				imagePath = HttpClientUtil.SERVER_PATH + images[position];

			}

			imageView.setOnPhotoTapListener(new OnPhotoTapListener()
			{

				@Override
				public void onPhotoTap(View view, float x, float y)
				{
					finish();
				}
			});
			final ProgressBar spinner = (ProgressBar) imageLayout
					.findViewById(R.id.loading);

			imageLoader.displayImage(imagePath, imageView, options,

			new SimpleImageLoadingListener()
			{
				@Override
				public void onLoadingStarted(String imageUri, View view)
				{
					spinner.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view,
						FailReason failReason)
				{
					String message = null;
					switch (failReason.getType())
					{
					case IO_ERROR:
						message = "Input/Output error";
						break;
					case DECODING_ERROR:
						message = "Image can't be decoded";
						break;
					case NETWORK_DENIED:
						message = "Downloads are denied";
						break;
					case OUT_OF_MEMORY:
						message = "Out Of Memory error";
						break;
					case UNKNOWN:
						message = "Unknown error";
						break;
					}
					Toast.makeText(GalleyShowActivity.this, message,
							Toast.LENGTH_SHORT).show();

					spinner.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view,
						Bitmap loadedImage)
				{
					// int height = loadedImage.getHeight();
					// int width = loadedImage.getWidth();
					//
					// DisplayMetrics dm = new DisplayMetrics();
					// getWindowManager().getDefaultDisplay().getMetrics(dm);
					// int widthPixels = dm.widthPixels;
					// int heightPixels = dm.heightPixels;
					// if (height > heightPixels || width > widthPixels)
					// {
					// imageLoader.displayImage(HttpClientUtil.SERVER_PATH
					// + images[position], new ImageView(
					// GalleyShowActivity.this), options);
					//
					// }
					spinner.setVisibility(View.GONE);
				}
			});

			view.addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object)
		{
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader)
		{
		}

		@Override
		public Parcelable saveState()
		{
			return null;
		}
	}

	/**
	 * @Title: onPageScrollStateChanged
	 * @Description: TODO
	 * @param arg0
	 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrollStateChanged(int)
	 */
	@Override
	public void onPageScrollStateChanged(int arg0)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @Title: onPageScrolled
	 * @Description: TODO
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrolled(int,
	 *      float, int)
	 */
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @Title: onPageSelected
	 * @Description: TODO
	 * @param arg0
	 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageSelected(int)
	 */
	@Override
	public void onPageSelected(int position)
	{
		mTextView.setText((position + 1) + " / " + imageUrls.length); // 显示当前的页数和图片的
	}

}