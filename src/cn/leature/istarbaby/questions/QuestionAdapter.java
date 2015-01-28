/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: QuestionAdapter.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.questions
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-6-12 下午1:39:06
 * @version: V1.0  
 */
package cn.leature.istarbaby.questions;

/**
 * @ClassName: QuestionAdapter
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-6-12 下午1:39:06
 */
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.leature.istarbaby.R;

public class QuestionAdapter extends ArrayAdapter<Object> {

	private List<String> listItems;
	private int index;

	public QuestionAdapter(Context context, int resource) {
		super(context, resource);

	}

	public void setListItems(List<String> listItems) {
		this.listItems = listItems;
	}

	public void setbirthday(int birthday) {
		this.index = birthday;
	}

	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean isEnabled(int position) {
		if (position == 0) {
			// 固定第一行Title，不可点击
			return false;
		}

		return super.isEnabled(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {

		View view = convertView;
		TextView textView;
		ImageView imageView;

		view = LayoutInflater.from(getContext()).inflate(
				R.layout.question_list_item, null);

		textView = (TextView) view.findViewById(R.id.question_list_text);

		if (position == 0) {
			view.setBackgroundResource(R.drawable.bg_repeat);
			imageView = (ImageView) view.findViewById(R.id.question_list_img);
			imageView.setVisibility(View.INVISIBLE);

			textView.setText(Html.fromHtml("<b>" + listItems.get(position)
					+ "</b>"));
		} else {
			if (index == position) {
				view.setBackgroundResource(R.color.istar_c);

			}
			textView.setText(listItems.get(position));

		}

		return view;
	}

}
