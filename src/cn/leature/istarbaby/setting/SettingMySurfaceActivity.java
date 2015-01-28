package cn.leature.istarbaby.setting;

import java.util.Hashtable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.network.HttpClientUtil;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingMySurfaceActivity extends Activity implements
		OnClickListener {
	private static int QR_WIDTH_HEIGHT = 800;
	private TextView titleBarTitle;
	private ImageButton titleBarBack;
	private ImageButton titleBarEdit;

	private int screenWidth, screenHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_my_surface);

		setCustomTitleBar();

		ImageView imageview = (ImageView) findViewById(R.id.image_myface);

		// 取得窗口属性
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		// 窗口的宽度
		screenWidth = dm.widthPixels;
		// 窗口高度
		screenHeight = dm.heightPixels;

		// 设置view的宽高
		int viewWH = screenWidth * 3 / 4;
		LayoutParams lParams = imageview.getLayoutParams();
		lParams.width = viewWH;
		lParams.height = viewWH;
		imageview.setLayoutParams(lParams);

		Bitmap bm = createBitmap(HttpClientUtil.PACKAGE_SERVER_URL);
		imageview.setImageBitmap(bm);
	}

	private void setCustomTitleBar() {
		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("二维码");

		titleBarBack = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarBack.setOnClickListener((this));
		titleBarBack.setBackgroundResource(R.drawable.selector_hd_bk);

		titleBarEdit = (ImageButton) this.findViewById(R.id.titlebar_rightbtn1);
		titleBarEdit.setVisibility(View.INVISIBLE);
		titleBarEdit.setOnClickListener(this);
	}

	/**
	 * 生成二维码图片
	 * 
	 * @return
	 */
	private Bitmap createBitmap(String text) {
		Bitmap bitmap = null;
		try {
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			BitMatrix bitMatrix = new QRCodeWriter().encode(text,
					BarcodeFormat.QR_CODE, QR_WIDTH_HEIGHT, QR_WIDTH_HEIGHT,
					hints);

			int[] pixels = new int[QR_WIDTH_HEIGHT * QR_WIDTH_HEIGHT];
			for (int y = 0; y < QR_WIDTH_HEIGHT; y++) {
				for (int x = 0; x < QR_WIDTH_HEIGHT; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * QR_WIDTH_HEIGHT + x] = 0xff000000;
					} else {
						pixels[y * QR_WIDTH_HEIGHT + x] = 0xffffffff;
					}

				}
			}
			bitmap = Bitmap.createBitmap(QR_WIDTH_HEIGHT, QR_WIDTH_HEIGHT,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH_HEIGHT, 0, 0, QR_WIDTH_HEIGHT,
					QR_WIDTH_HEIGHT);

		} catch (WriterException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting_my_surface, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_leftbtn:
			this.finish();
			break;

		default:
			break;
		}
	}
}
