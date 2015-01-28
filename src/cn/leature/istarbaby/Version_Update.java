package cn.leature.istarbaby;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.leature.istarbaby.network.HttpClientUtil;

public class Version_Update implements android.view.View.OnClickListener {
	// 文件分隔符
	private static final String FILE_SEPARATOR = "/";
	// 外存sdcard存放路径
	private static final String FILE_PATH = Environment
			.getExternalStorageDirectory()
			+ FILE_SEPARATOR
			+ "version_update"
			+ FILE_SEPARATOR;
	// 应该程序名
	private static final String PACKAGE_NAME = HttpClientUtil.PACKAGE_NAME;
	// 下载应用存放的本地路径
	private static final String FILE_NAME = FILE_PATH + PACKAGE_NAME;

	// APK的服务器地址
	private String PACKAGE_SERVER_URL = HttpClientUtil.SERVER_PATH + "app/"
			+ PACKAGE_NAME;

	// 更新应用版本标记
	private static final int UPDARE_TOKEN = 0x29;
	// 准备安装新版本应用标记
	private static final int INSTALL_TOKEN = 0x31;

	private Context context;
	// 下载应用的对话框
	private Dialog dialog;
	// 下载应用的进度条
	private ProgressBar progressBar;
	// 进度条的当前刻度值
	private int curProgress;
	// 用户是否取消下载
	private boolean isCancel;
	private boolean isFinish = false;
	private TextView curProgresstext;
	private Button progress_cancel_btn;
	private Button version_update_later;
	private Button version_update_now;

	public Version_Update(Context context) {
		this.context = context;
	}

	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDARE_TOKEN:
				progressBar.setProgress(curProgress);
				curProgresstext.setText("下载进度：" + curProgress + "%");
				break;

			case INSTALL_TOKEN:
				installApp();
				isFinish = true;
				break;
			}
		}
	};

	public boolean DownLoadfinish() {
		return isFinish;
	}

	/**
	 * 检测应用更新信息
	 */
	public void checkUpdateInfo() {
		showNoticeDialog();
	}

	/**
	 * 显示提示更新对话框
	 */
	private void showNoticeDialog() {

		View view = LayoutInflater.from(context).inflate(
				R.layout.version_update_dialog, null);
		version_update_later = (Button) view
				.findViewById(R.id.version_update_later);// 以后再说
		version_update_now = (Button) view
				.findViewById(R.id.version_update_now);// 下载更新
		version_update_later.setOnClickListener(this);
		version_update_now.setOnClickListener(this);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(false);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
	}

	/**
	 * 显示下载进度对话框
	 */
	private void showDownloadDialog() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.version_update_progressbar, null);
		progressBar = (ProgressBar) view.findViewById(R.id.version_progressBar);
		curProgresstext = (TextView) view.findViewById(R.id.version_textview);
		progress_cancel_btn = (Button) view
				.findViewById(R.id.version_download_cancel_btn);
		progress_cancel_btn.setOnClickListener(this);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(false);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		downloadApp();
	}

	/**
	 * 下载新版本应用
	 */
	private void downloadApp() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				URL url = null;
				InputStream in = null;
				FileOutputStream out = null;
				HttpURLConnection conn = null;
				try {
					url = new URL(PACKAGE_SERVER_URL);
					conn = (HttpURLConnection) url.openConnection();
					// conn.connect();
					long fileLength = conn.getContentLength();
					in = conn.getInputStream();
					File filePath = new File(FILE_PATH);
					if (!filePath.exists()) {
						filePath.mkdir();
					}
					out = new FileOutputStream(new File(FILE_NAME));
					byte[] buffer = new byte[1024];
					int len = 0;
					long readedLength = 0l;
					while ((len = in.read(buffer)) != -1) {
						// 用户点击“取消”按钮，下载中断
						if (isCancel) {
							break;
						}
						out.write(buffer, 0, len);
						readedLength += len;
						curProgress = (int) (((float) readedLength / fileLength) * 100);
						handler.sendEmptyMessage(UPDARE_TOKEN);
						if (readedLength >= fileLength) {
							dialog.dismiss();
							// 下载完毕，通知安装
							handler.sendEmptyMessage(INSTALL_TOKEN);
							break;
						}
					}
					out.flush();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (conn != null) {
						conn.disconnect();
					}
				}
			}
		}).start();
	}

	/**
	 * 安装新版本应用
	 */
	private void installApp() {
		File appFile = new File(FILE_NAME);
		if (!appFile.exists()) {
			return;
		}
		// 跳转到新版本应用安装页面
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://" + appFile.toString()),
				"application/vnd.android.package-archive");
		context.startActivity(intent);

		// 取消安装时候，退出应用程序
		System.exit(0);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == progress_cancel_btn) {
			dialog.dismiss();
			isCancel = true;
			System.exit(0);
		}
		if (v == version_update_later) {
			dialog.dismiss();
			// System.exit(0);
		}
		if (v == version_update_now) {
			dialog.dismiss();
			showDownloadDialog();
		}
	}

}