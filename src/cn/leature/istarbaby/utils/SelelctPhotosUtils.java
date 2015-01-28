/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: SelelctPhotosUtils.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.utils
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-6-11 下午2:48:57
 * @version: V1.0  
 */
package cn.leature.istarbaby.utils;

/**
 * @ClassName: SelelctPhotosUtils
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-6-11 下午2:48:57
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.ImageView;

public class SelelctPhotosUtils
{

	private Context mContext;

	public SelelctPhotosUtils(Context context)
	{
		this.mContext = context;
	}

	/**
	 * 获取全部图片地址
	 * 
	 * @return
	 */
	public ArrayList<String> listAlldir()
	{
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		Uri uri = intent.getData();
		ArrayList<String> list = new ArrayList<String>();

		String[] proj =
		{ MediaStore.Images.Media.DATA };
		Cursor cursor = mContext.getContentResolver().query(uri, proj, null,
				null, null);
		if (cursor != null)
		{
			while (cursor.moveToNext())
			{
				String path = cursor.getString(0);
				list.add(new File(path).getAbsolutePath());
			}
			cursor.close();
		}

		return list;
	}

	public List<FileTraversal> localImgFileList()
	{

		List<FileTraversal> fileTraList = new ArrayList<FileTraversal>();

		// 取出所有图片文件列表
		List<String> allImgFileList = listAlldir();

		if (allImgFileList != null)
		{
			// 图片文件的目录集
			List<String> imgPathList = new ArrayList<String>();
			for (int i = 0; i < allImgFileList.size(); i++)
			{
				imgPathList.add(getFileInfo(allImgFileList.get(i)));
			}

			Set<String> treeSet = new TreeSet<String>();
			for (int i = 0; i < imgPathList.size(); i++)
			{
				treeSet.add(imgPathList.get(i));
			}

			Iterator<String> iterator = treeSet.iterator();
			while (iterator.hasNext())
			{
				FileTraversal ftl = new FileTraversal();
				ftl.setFilePathName(iterator.next().toString());

				fileTraList.add(ftl);
			}

			for (int i = 0; i < fileTraList.size(); i++)
			{
				for (int j = 0; j < allImgFileList.size(); j++)
				{
					if (fileTraList.get(i).getFilePathName()
							.equals(getFileInfo(allImgFileList.get(j))))
					{
						fileTraList.get(i).getFileContentList()
								.add(allImgFileList.get(j));
					}
				}
			}
		}

		return fileTraList;
	}

	public static List<FileTraversal> selectImgFileList(String path)
	{

		List<FileTraversal> fileTraList = new ArrayList<FileTraversal>();

		// // 取出所有图片文件列表
		// List<String> allImgFileList = listAlldir();

		if (path != null)
		{
			// 图片文件的目录集
			// List<String> imgPathList = new ArrayList<String>();
			// for (int i = 0; i < allImgFileList.size(); i++)
			// {
			// imgPathList.add(getFileInfo(allImgFileList.get(i)));
			// }

			Set<String> treeSet = new TreeSet<String>();
			treeSet.add(path);

			// for (int i = 0; i < imgPathList.size(); i++)
			// {
			// treeSet.add(imgPathList.get(i));
			// }

			Iterator<String> iterator = treeSet.iterator();
			while (iterator.hasNext())
			{
				FileTraversal ftl = new FileTraversal();
				ftl.setFilePathName(iterator.next().toString());

				fileTraList.add(ftl);
			}

			// for (int i = 0; i < fileTraList.size(); i++)
			// {
			// for (int j = 0; j < allImgFileList.size(); j++)
			// {
			// if (fileTraList.get(i).getFilePathName()
			// .equals(getFileInfo(allImgFileList.get(j))))
			// {
			// fileTraList.get(i).getFileContentList()
			// .add(allImgFileList.get(j));
			// }
			// }
			// }
		}

		return fileTraList;
	}

	// 显示原生图片尺寸大小
	public Bitmap getPathBitmap(Uri imageFilePath, int dw, int dh)
			throws FileNotFoundException
	{
		// 获取屏幕的宽和高
		/**
		 * 为了计算缩放的比例，我们需要获取整个图片的尺寸，而不是图片
		 * BitmapFactory.Options类中有一个布尔型变量inJustDecodeBounds，将其设置为true
		 * 这样，我们获取到的就是图片的尺寸，而不用加载图片了。
		 * 当我们设置这个值的时候，我们接着就可以从BitmapFactory.Options的outWidth和outHeight中获取到值
		 */
		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inJustDecodeBounds = true;
		// 由于使用了MediaStore存储，这里根据URI获取输入流的形式
		Bitmap pic = BitmapFactory.decodeStream(mContext.getContentResolver()
				.openInputStream(imageFilePath), null, op);

		int wRatio = (int) Math.ceil(op.outWidth / (float) dw); // 计算宽度比例
		int hRatio = (int) Math.ceil(op.outHeight / (float) dh); // 计算高度比例

		/**
		 * 接下来，我们就需要判断是否需要缩放以及到底对宽还是高进行缩放。 如果高和宽不是全都超出了屏幕，那么无需缩放。
		 * 如果高和宽都超出了屏幕大小，则如何选择缩放呢》 这需要判断wRatio和hRatio的大小
		 * 大的一个将被缩放，因为缩放大的时，小的应该自动进行同比率缩放。 缩放使用的还是inSampleSize变量
		 */
		if (wRatio > 1 && hRatio > 1)
		{
			if (wRatio > hRatio)
			{
				op.inSampleSize = wRatio;
			}
			else
			{
				op.inSampleSize = hRatio;
			}
		}
		op.inJustDecodeBounds = false; // 注意这里，一定要设置为false，因为上面我们将其设置为true来获取图片尺寸了
		pic = BitmapFactory.decodeStream(mContext.getContentResolver()
				.openInputStream(imageFilePath), null, op);

		return pic;
	}

	public String getFileInfo(String data)
	{
		String filename[] = data.split("/");

		// 文件所在的目录名
		if (filename != null)
		{
			return filename[filename.length - 2];
		}

		return null;
	}

	public void imgExcute(ImageView imageView, SelectPhotosCallBack icb,
			String... params)
	{
		LoadBitAsynk loadBitAsynk = new LoadBitAsynk(imageView, icb);
		loadBitAsynk.execute(params);
	}

	public class LoadBitAsynk extends AsyncTask<String, Integer, Bitmap>
	{

		ImageView imageView;
		SelectPhotosCallBack icb;

		LoadBitAsynk(ImageView imageView, SelectPhotosCallBack icb)
		{
			this.imageView = imageView;
			this.icb = icb;
		}

		@Override
		protected Bitmap doInBackground(String... params)
		{
			Bitmap bitmap = null;
			try
			{
				if (params != null)
				{
					for (int i = 0; i < params.length; i++)
					{
						bitmap = getPathBitmap(
								Uri.fromFile(new File(params[i])),
								ConstantDef.cgImageResizeWidth,
								ConstantDef.cgImageResizeHeight);
					}
				}
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}

			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result)
		{
			super.onPostExecute(result);
			if (result != null)
			{
				icb.resultImgCall(imageView, result);
			}
		}
	}
}
