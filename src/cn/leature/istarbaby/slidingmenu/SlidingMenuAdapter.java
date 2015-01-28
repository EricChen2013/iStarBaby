/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: SlidingMenuAdapter.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.slidingmenu
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-6-11 下午12:11:10
 * @version: V1.0  
 */
package cn.leature.istarbaby.slidingmenu;

/**
 * @ClassName: SlidingMenuAdapter
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-6-11 下午12:11:10
 */
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.child.ChildrenActivity;
import cn.leature.istarbaby.domain.ChildrenInfo;
import cn.leature.istarbaby.network.HttpClientUtil;
import cn.leature.istarbaby.utils.ConstantDef;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SlidingMenuAdapter extends ArrayAdapter<Object>
{

	private List<SlidingMenuItem> listItems = null;

	private List<ChildrenInfo> childrenInfos = null;

	private Context mContext;
	private DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	public SlidingMenuAdapter(Context context, int resource)
	{
		super(context, resource);

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.noimage_child)
				.showImageForEmptyUri(R.drawable.noimage_child)
				.showImageOnFail(R.drawable.noimage_child).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();

		this.mContext = context;
	}

	public void setListItems(List<SlidingMenuItem> listItems)
	{
		this.listItems = listItems;
	}

	@Override
	public int getCount()
	{
		return listItems.size();
	}

	@Override
	public Object getItem(int position)
	{
		return position;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public boolean isEnabled(int position)
	{

		SlidingMenuItem item = listItems.get(position);

		if (item.isHeader() || item.isFooter())
		{
			return false;
		}

		if (item.isGroupTag())
		{
			return false;
		}

		return super.isEnabled(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{

		// 菜单信息设定
		SlidingMenuItem item = listItems.get(position);

		View view = convertView;
		ImageView imageView = null;
		TextView textView = null;

		if (item.isHeader() || item.isFooter())
		{
			// 菜单的头部和尾部
			view = LayoutInflater.from(mContext).inflate(
					R.layout.slidingmenu_item_hf, null);
		}
		else if (item.isGroupTag())
		{
			// 菜单组的标题
			view = LayoutInflater.from(mContext).inflate(
					R.layout.slidingmenu_tag, null);

			textView = (TextView) view.findViewById(R.id.slidingmenu_list_tag);
			textView.setText(item.getItemText());
		}
		else if (item.isImageItem())
		{
			// 菜单第二组：宝宝管理的图片
			view = LayoutInflater.from(mContext).inflate(
					R.layout.slidingmenu_hscrollview, null);
			LinearLayout layout = (LinearLayout) view
					.findViewById(R.id.slidingmenu_hscroll);

			childrenInfos = item.getChildrenInfos();
			if (childrenInfos.size() == 0)
			{
				View iconView = LayoutInflater.from(mContext).inflate(
						R.layout.slidingmenu_icon, null);
				TextView text = (TextView) iconView
						.findViewById(R.id.slidingmenu_icon_text);
				text.setText("未登录");
				layout.addView(iconView);
			}
			else
			{
				for (int i = 0; i < childrenInfos.size(); i++)
				{
					// 显示宝宝小名，如果没设，显示姓名
					String childName = childrenInfos.get(i).getNickname();
					if (childName.equals(""))
					{
						childName = childrenInfos.get(i).getChildName();
					}
					final String childNo = childrenInfos.get(i).getChild_no();
					final String childId = childrenInfos.get(i).getChild_id();
					View iconView = LayoutInflater.from(mContext).inflate(
							R.layout.slidingmenu_icon, null);

					final ImageButton imgButton = (ImageButton) iconView
							.findViewById(R.id.slidingmenu_icon_img);
					// 图片加载
					imageLoader.displayImage(HttpClientUtil.SERVER_PATH
							+ childrenInfos.get(i).getIcon(), imgButton,
							options);

					imgButton.setOnClickListener(new View.OnClickListener()
					{

						@Override
						public void onClick(View arg0)
						{

							Intent intent = new Intent();
							intent.setClass(mContext, ChildrenActivity.class);
							// 传递参数：ChildNo
							Bundle bundle = new Bundle();
							bundle.putString(ConstantDef.ccChildNo, childNo);
							bundle.putString(ConstantDef.ccChildId, childId);
							intent.putExtras(bundle);

							getContext().startActivity(intent);

							// 设定启动动画
							((Activity) mContext).overridePendingTransition(
									R.anim.activity_in_from_right,
									R.anim.activity_nothing_in_out);

							// 结束当前activity
							((Activity) mContext).finish();
						}
					});

					TextView text = (TextView) iconView
							.findViewById(R.id.slidingmenu_icon_text);
					text.setText(childName);

					layout.addView(iconView);
				}
			}
		}
		else
		{
			view = LayoutInflater.from(getContext()).inflate(
					R.layout.slidingmenu_item, null);

			imageView = (ImageView) view
					.findViewById(R.id.slidingmenu_list_img);
			if (item.getResImage() == 0)
			{
				// 未定义图片时，显示默认图片
				imageView.setImageResource(R.drawable.ic_launcher);
			}
			else
			{
				imageView.setImageResource(item.getResImage());
			}

			textView = (TextView) view.findViewById(R.id.slidingmenu_list_text);
			textView.setText(item.getItemText());
		}
		return view;
	}

	public void reloadData()
	{
		notifyDataSetChanged();
	}
}
