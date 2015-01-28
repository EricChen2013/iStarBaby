package cn.leature.istarbaby.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class SaveBitmapFile {
	public static final String SDPATH = Environment
			.getExternalStorageDirectory() + "";

	public static final String SnapShotPath = "iSnapshot";

	public static String getSnapshotPath() {

		String pathString = SDPATH + File.separator + SnapShotPath;
		// 是否创建目录
		if (!isFolderExists(pathString)) {
			// 没有SD卡，目录创建失败
			return "";
		}

		return pathString;
	}

	public static int saveSnapshot(Bitmap bitmap) {

		String pathString = SDPATH + File.separator + SnapShotPath;
		// 是否创建目录
		if (!isFolderExists(pathString)) {
			// 没有SD卡，目录创建失败
			return -1;
		}

		if (saveBitmapToFile(pathString, getFileNameWithTime(), bitmap)) {
			// 创建成功
			return 0;
		}

		return 1;
	}

	private static String getFileNameWithTime() {
		Calendar cal = Calendar.getInstance();

		StringBuffer stringbuffer = new StringBuffer();
		stringbuffer.append("IMG_");

		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		// 年月日(yyyyMMdd)
		stringbuffer.append(String.format("%d%02d%02d", year, month, day));
		stringbuffer.append('_');
		// 时分秒(hhmmss)毫秒(xxx)
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		int milisecond = cal.get(Calendar.MILLISECOND);
		stringbuffer.append(String.format("%02d%02d%02d%03d", hour, minute,
				second, milisecond));
		stringbuffer.append(".png");

		return stringbuffer.toString();
	}

	public static boolean saveBitmapToFile(String pathName, String fileName,
			Bitmap bitmap) {

		File file = null;
		FileOutputStream fOut = null;

		try {

			file = new File(pathName, fileName);
			file.createNewFile();

			fOut = new FileOutputStream(file);

			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);

			fOut.flush();

			if (fOut != null) {
				fOut.close();
			}
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}

		return true;
	}

	private static boolean isFolderExists(String strFolder) {
		File file = new File(strFolder);
		if (!file.exists()) {
			if (file.mkdirs()) {
				return true;
			} else {
				return false;
			}
		}

		return true;
	}

	public static Bitmap loadBitmapFromFile(String fullFileName) {
		Bitmap bitmap = null;

		InputStream inputStream = null;
		try {

			inputStream = new FileInputStream(fullFileName);

			bitmap = BitmapFactory.decodeStream(inputStream);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;
	}

	public static Bitmap loadBitmapFromFile(String pathName, String fileName) {
		Bitmap bitmap = null;

		InputStream inputStream = null;
		try {
			String fullName = pathName + File.separator + fileName;

			inputStream = new FileInputStream(fullName);

			bitmap = BitmapFactory.decodeStream(inputStream);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;
	}
}
