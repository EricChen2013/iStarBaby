package cn.leature.istarbaby.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.leature.istarbaby.R;

public class LzProgressDialog extends ProgressDialog {

	// 文本及显示位置
	private String mText = "";
	private boolean mRightText = false;

	private ImageView Lzdialog_imageview; // 动画的图片
	private TextView Lzdialog_textview; // 请等待的文字
	private AnimationDrawable Lzdialog_animationdrawable; // 实现动画的控件

	public LzProgressDialog(Context context) {
		super(context, R.style.Lzdialogstyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		if (mRightText) {
			this.setContentView(R.layout.lzprogressdialog_right);
		} else {
			this.setContentView(R.layout.lzprogressdialog_bottom);
		}

		init_Layout();
	}

	private void init_Layout() {

		Lzdialog_imageview = (ImageView) this
				.findViewById(R.id.Lz_progressdialog_imageview);

		Lzdialog_textview = (TextView) this
				.findViewById(R.id.Lz_progressdialog_textview);

		Lzdialog_imageview.setBackgroundResource(R.anim.lz_animation_dialog);
		Lzdialog_animationdrawable = (AnimationDrawable) Lzdialog_imageview
				.getBackground();
		Lzdialog_animationdrawable.start();
		Lzdialog_animationdrawable.setOneShot(false); // 动画是否只执行一次

		// 显示文字
		if (!"".equals(mText)) {

			Lzdialog_textview.setText(mText);

			// 显示文字
			Lzdialog_textview.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void setMessage(CharSequence message) {
		// TODO Auto-generated method stub
		super.setMessage(message);

		set_Text(message.toString());
	}

	public void set_Text(String text) {
		// 默认文字显示在下方
		set_Text(text, false);
	}

	public void set_Text(String text, boolean isRight) {
		// 设置文本
		this.mText = text;

		// 设置文本是否显示在右边（默认：显示在下方）
		this.mRightText = isRight;
	}

	public void showSubmitSuccess(String text) {

		Lzdialog_imageview.setBackgroundResource(R.drawable.submit_success);

		// 设置文本
		this.mText = text;

		// 显示文字
		if (!"".equals(mText)) {

			Lzdialog_textview.setText(mText);

			// 显示文字
			Lzdialog_textview.setVisibility(View.VISIBLE);
		}
	}
	
	public void showSubmitFailed(String text) {

		Lzdialog_imageview.setBackgroundResource(R.drawable.submit_error);

		// 设置文本
		this.mText = text;

		// 显示文字
		if (!"".equals(mText)) {

			Lzdialog_textview.setText(mText);

			// 显示文字
			Lzdialog_textview.setVisibility(View.VISIBLE);
		}
	}
}