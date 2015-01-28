package cn.leature.istarbaby.child;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class ChildEditSelectImageUtils
{

	public static final int GET_IMAGE_FROM_PHONE = 1;
	public static final int GET_IMAGE_BY_CAMERA = 5001;
	public static Uri imageUriFromCamera;

	public static void openLocalImage(Activity activity)
	{
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		activity.startActivityForResult(intent,
				ChildEditSelectImageUtils.GET_IMAGE_FROM_PHONE);
	}

	public static Uri createImagePathUri(Context context)
	{
		Uri imageFilePath = null;
		String status = Environment.getExternalStorageState();
		SimpleDateFormat timeFormatter = new SimpleDateFormat(
				"yyyyMMdd_HHmmss", Locale.CHINA);
		long time = System.currentTimeMillis();
		String imageName = timeFormatter.format(new Date(time));
		// ContentValues是我们希望这条记录被创建时包含的数据信息
		ContentValues values = new ContentValues(3);
		values.put(MediaStore.Images.Media.DISPLAY_NAME, imageName);
		values.put(MediaStore.Images.Media.DATE_TAKEN, time);
		values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

		// 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
		if (status.equals(Environment.MEDIA_MOUNTED))
		{
			imageFilePath = context.getContentResolver().insert(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		}
		else
		{
			imageFilePath = context.getContentResolver().insert(
					MediaStore.Images.Media.INTERNAL_CONTENT_URI, values);
		}
		Log.i("", "生成的照片输出路径：" + imageFilePath.toString());
		return imageFilePath;
	}

	public static void openCameraImage(final Activity activity,
			final Uri imageFilePath)
	{
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (imageFilePath != null)
		{
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFilePath);
		}
		activity.startActivityForResult(intent,
				ChildEditSelectImageUtils.GET_IMAGE_BY_CAMERA);
	}

	public static Bitmap decodeBitmapByUri(Context context, Uri uri)
	{
		ContentResolver contentResolver = context.getContentResolver();
		Bitmap bitmap = null;
		try
		{
			InputStream is = contentResolver.openInputStream(uri);
			bitmap = BitmapFactory.decodeStream(is);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return bitmap;
	}

	public static void getCropImage(Activity activity)
	{

		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 2);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 600);
		intent.putExtra("outputY", 300);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", true);
		// intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
		intent.putExtra("noFaceDetection", false); // no face detection
		// startActivityForResult(intent, CHOOSE_BIG_PICTURE);

		activity.startActivityForResult(intent, 2);
	}
	// public final static int SELECT_CROP_IMAGE = 9;
	//
	// public static void getCropImage(Activity activity, int aspectX,
	// int aspectY, int outputX, int outputY) {
	//
	// Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
	// intent.setType("image/*");
	// intent.putExtra("crop", "true");
	// intent.putExtra("aspectX", aspectX);
	// intent.putExtra("aspectY", aspectY);
	// intent.putExtra("outputX", outputX);
	// intent.putExtra("outputY", outputY);
	// intent.putExtra("scale", true);
	// intent.putExtra("return-data", true);
	// intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
	// intent.putExtra("noFaceDetection", false); // no face detection
	// activity.startActivityForResult(intent, SELECT_CROP_IMAGE);
	// }
	//
	// public static void getCropRectImage(Activity activity, int outputWH) {
	//
	// // 截取正方形图片
	// SelectCropImageUtil.getCropImage(activity, 1, 1, outputWH, outputWH);
	// }
}
