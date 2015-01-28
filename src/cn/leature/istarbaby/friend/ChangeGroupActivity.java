package cn.leature.istarbaby.friend;

import java.util.ArrayList;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.R.layout;
import cn.leature.istarbaby.R.menu;
import cn.leature.istarbaby.utils.LzViewHolder;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ChangeGroupActivity extends Activity
{

	private Bundle mBundle;
	private Intent mIntent;
	private ArrayList<String> groupNameList;
	private ChangeAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_group);
	
		mBundle = mIntent.getExtras();
		groupNameList = mBundle.getStringArrayList("GroupName");
		
		
		initUI();
	}

	private void initUI()
	{
	
		ListView mlistview = (ListView) findViewById(R.id.changegroup_listview);
		mAdapter = new ChangeAdapter();
		mlistview.setAdapter(mAdapter);
	
	}
	private class ChangeAdapter extends BaseAdapter
	{

		@Override
		public int getCount()
		{
			return groupNameList.size();
		}

		@Override
		public Object getItem(int position)
		{
			return null;
		}

		@Override
		public long getItemId(int position)
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent)
		{
			String groupName = groupNameList.get(position);
			convertView = getLayoutInflater().inflate(
					R.layout.wifi_listview_item, null);
			LinearLayout layoutitem = LzViewHolder.get(convertView,
					R.id.linearlayout_item);
			layoutitem.setTag(position + "");
			TextView mTextName = LzViewHolder.get(convertView,
					R.id.wifi_textname);
			final TextView mTextSignal = LzViewHolder.get(convertView,
					R.id.wifi_signal);

			final ImageView mWifi_resize = LzViewHolder.get(convertView,
					R.id.wifi_resize);
			mTextName.setText(groupName);
			mTextSignal.setVisibility(View.INVISIBLE);

//			if (position == mSelectedGroup)
//			{
//				mWifi_resize.setVisibility(View.VISIBLE);
//
//			}
//			else
//			{
//				mWifi_resize.setVisibility(View.INVISIBLE);
//			}
//
//			layoutitem.setOnClickListener(new OnClickListener()
//			{
//
//				@Override
//				public void onClick(View v)
//				{
//
//					mSelectedGroup = position;
//					mBundle.putInt("Position", mSelectedGroup);
//					adapter.notifyDataSetChanged();
//					mIntent.putExtras(mBundle);
//					setResult(Activity.RESULT_OK, mIntent);
//					MoveGroupActivity.this.finish();
//				}
//			});
			return convertView;
		}

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.change_group, menu);
		return true;
	}

}
