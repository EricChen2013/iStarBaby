package cn.leature.istarbaby.friend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzBaseActivity;
import cn.leature.istarbaby.domain.FriendGroupInfo;
import cn.leature.istarbaby.domain.FriendInfo;
import cn.leature.istarbaby.domain.FriendListInfo;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.network.HttpClientUtil;
import cn.leature.istarbaby.network.HttpGetUtil;
import cn.leature.istarbaby.network.HttpGetUtil.RequestGetDoneCallback;
import cn.leature.istarbaby.slidingmenu.SlidingMenu;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.LzViewHolder;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class FriendListActivity extends LzBaseActivity implements
		OnClickListener, OnChildClickListener, OnItemLongClickListener,
		OnGroupClickListener {

	private ExpandableListView mPandListview;
	private SlidingMenu mSlidingMenu;

	private List<List<FriendInfo>> ChildrenData = new ArrayList<List<FriendInfo>>();
	List<FriendGroupInfo> GroupData = new ArrayList<FriendGroupInfo>();

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;

	private String mPreGroupId = "-1";
	private PopupWindow mPopupWindow;
	private Button mFriendlistlayout;
	private FrameLayout mWindow_layout;
	boolean openWindow = false;
	private ExPandListviewAdapter adapter;
	private int mGroupShowPositionY = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sliding_main);

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.noimage)
				.showImageForEmptyUri(R.drawable.noimage)
				.showImageOnFail(R.drawable.noimage).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();

		initUI();

		getChildDetail();
	}

	private void initUI() {
		// 设置主菜单
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mSlidingMenu = (SlidingMenu) findViewById(R.id.slidingMain);

		View leftView = getLayoutInflater()
				.inflate(R.layout.sliding_menu, null);
		View centerView = getLayoutInflater().inflate(
				R.layout.activity_friend_list, null);
		mSlidingMenu.setLeftView(leftView);
		mSlidingMenu.setCenterView(centerView);

		// 设置顶部导航
		TextView titleBarTitle = (TextView) this
				.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("联系人");

		ImageButton showLeftMenu = (ImageButton) centerView
				.findViewById(R.id.titlebar_leftbtn);
		showLeftMenu.setBackgroundResource(R.drawable.selector_hd_menu);
		showLeftMenu.setOnClickListener(this);

		ImageButton titleBarPost = (ImageButton) this
				.findViewById(R.id.titlebar_rightbtn1);
		titleBarPost.setBackgroundResource(R.drawable.selector_hd_add);
		titleBarPost.setOnClickListener(this);

		mPandListview = (ExpandableListView) findViewById(R.id.expandableListView1);
		mPandListview.setOnChildClickListener(FriendListActivity.this);
		mFriendlistlayout = (Button) centerView
				.findViewById(R.id.friendlistlayout);
		mWindow_layout = (FrameLayout) centerView
				.findViewById(R.id.friendpopwindow_layout);

		mPandListview.setOnItemLongClickListener(this);
		mPandListview.setOnGroupClickListener(this);
		mFriendlistlayout.setOnClickListener(this);

		/**
		 * 监听父节点打开的事件
		 */
		mPandListview.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
				// the_group_expand_position = groupPosition;
				// position_child_count = childData.get(groupPosition).size();
				// isExpanding = true;
				if (groupPosition >= ChildrenData.size()) {
					return;
				}
			}

		});

	}

	@Override
	protected void onPause() {
		if (openWindow) {
			mPopupWindow.dismiss();
			openWindow = false;
		}

		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (openWindow) {
			mPopupWindow.dismiss();
			openWindow = false;
		}

		super.onDestroy();
	}

	private void getChildDetail() {

		// 日志List取得的参数设定
		Map<String, String> param = new HashMap<String, String>();
		param.put("UserId", LoginInfo.getLoginUserId(this));

		HttpGetUtil httpGetUtil = new HttpGetUtil(new RequestGetDoneCallback() {

			@Override
			public void requestWithGetDone(String result) {
				try {

					List<FriendInfo> child = null;

					JSONArray jsonArray = new JSONArray(result);
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);

						FriendListInfo friendListInfo = new FriendListInfo(
								jsonObject);
						// 设置
						if (mPreGroupId.equals(friendListInfo.getGroup_id())) {
							FriendInfo friendInfo = new FriendInfo(
									friendListInfo);
							child.add(friendInfo);

						} else {

							FriendGroupInfo friendGroupInfo = new FriendGroupInfo(
									friendListInfo);
							GroupData.add(friendGroupInfo);

							child = new ArrayList<FriendInfo>();
							FriendInfo friendInfo = new FriendInfo(
									friendListInfo);
							child.add(friendInfo);

							ChildrenData.add(child);

						}

						mPreGroupId = friendListInfo.getGroup_id();
					}

					adapter = new ExPandListviewAdapter();
					mPandListview.setAdapter(adapter);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		httpGetUtil.execute(ConstantDef.cUrlContactList, param);
	}

	class ExPandListviewAdapter extends BaseExpandableListAdapter {

		@Override
		public int getGroupCount() {
			return GroupData.size();
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			convertView = getLayoutInflater().inflate(
					R.layout.expandviewgroup_layout, null);

			ImageView mgroupimage = LzViewHolder.get(convertView,
					R.id.groupimage);
			if (isExpanded) {
				mgroupimage.setImageBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.listview_hd_on));
			} else {
				mgroupimage.setImageBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.listview_hd));
			}

			TextView groupText = LzViewHolder.get(convertView, R.id.group_text);
			groupText.setText(GroupData.get(groupPosition).getGroup_name());

			convertView.setTag(-1);
			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			FriendInfo child = ChildrenData.get(groupPosition).get(0);
			if ("".equals(child.getContactId())) {
				return 0;
			}

			return ChildrenData.get(groupPosition).size();
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			FriendInfo friendInfo = ChildrenData.get(groupPosition).get(
					childPosition);

			convertView = getLayoutInflater().inflate(
					R.layout.friends_listview_item, null);

			ImageView item_Usericon = LzViewHolder.get(convertView,
					R.id.item_usericon);
			TextView item_Userid = LzViewHolder.get(convertView,
					R.id.item_userid);
			TextView item_Username = LzViewHolder.get(convertView,
					R.id.item_username);

			String name = friendInfo.getContactMemo();
			if (name.length() == 0) {
				name = friendInfo.getContact_name();
			}
			item_Username.setText(name);
			item_Userid.setText(friendInfo.getContactId());
			if (!"".equals(friendInfo.getContact_icon())) {
				imageLoader.displayImage(HttpClientUtil.SERVER_PATH
						+ friendInfo.getContact_icon(), item_Usericon, options);
			}

			convertView.setTag(childPosition);

			return convertView;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return GroupData.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return ChildrenData.get(groupPosition).get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_leftbtn:
			mSlidingMenu.showLeftView();
			break;
		case R.id.titlebar_rightbtn1:
			// 查找页面
			Intent intent = new Intent();
			intent.setClass(this, FriendSearchActivity.class);
			startActivityForResult(intent,
					ConstantDef.REQUEST_CODE_FRIENDSEARCH);
			// 设定启动动画
			this.overridePendingTransition(R.anim.activity_in_from_right,
					R.anim.activity_nothing_in_out);
			break;
		case R.id.friendlistlayout:
			if (mPopupWindow != null) {
				openWindow = false;
				mPopupWindow.dismiss();
			}
			break;
		}
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		if (openWindow) {
			openWindow = false;
			mPopupWindow.dismiss();
			return true;
		}

		FriendInfo friendInfo = ChildrenData.get(groupPosition).get(
				childPosition);
		FriendGroupInfo friendGroupInfo = GroupData.get(groupPosition);
		// 好友详情页面
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("ContactId", friendInfo.getContactId());
		bundle.putString("oldGroup", friendGroupInfo.getGroup_name());
		intent.putExtras(bundle);
		intent.setClass(FriendListActivity.this, FriendDetailActivity.class);
		startActivityForResult(intent, ConstantDef.REQUEST_CODE_FRIENDDETAIL);

		// 设定启动动画
		this.overridePendingTransition(R.anim.activity_in_from_right,
				R.anim.activity_nothing_in_out);

		return false;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
			return true;
		}

		int[] location = new int[2];
		view.getLocationInWindow(location);
		mGroupShowPositionY = location[1];

		int i = (Integer) view.getTag();

		if (i == -1) {
			if (!openWindow) {
				getpopunwindow(position);
			}
		}

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == ConstantDef.REQUEST_CODE_FRIENDGRPMNG) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				mPreGroupId = "-1";
				GroupData.clear();
				ChildrenData.clear();
				getChildDetail();

				break;

			default:
				break;
			}
		} else if ((requestCode == ConstantDef.REQUEST_CODE_FRIENDDETAIL)
				|| (requestCode == ConstantDef.REQUEST_CODE_FRIENDSEARCH)) {
			mPreGroupId = "-1";
			GroupData.clear();
			ChildrenData.clear();
			getChildDetail();
		}
	}

	private void getpopunwindow(int position) {
		openWindow = !openWindow;
		// 编辑文章
		LayoutInflater LayoutInflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View popunwindwow = LayoutInflater.inflate(R.layout.friendlist_dialog,
				null);

		mPopupWindow = new PopupWindow(popunwindwow, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		mPopupWindow.showAtLocation(mWindow_layout, Gravity.TOP, 0,
				mGroupShowPositionY);

		popunwindwow.findViewById(R.id.list_dialogtext).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						mPopupWindow.dismiss();
						openWindow = false;

						Intent intent = new Intent();
						intent.setClass(FriendListActivity.this,
								FriendGroupManageActivity.class);

						startActivityForResult(intent,
								ConstantDef.REQUEST_CODE_FRIENDGRPMNG);
					}
				});
	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v,
			int groupPosition, long id) {
		if (openWindow) {
			mPopupWindow.dismiss();
			openWindow = false;
			return true;
		}

		return false;
	}
}
