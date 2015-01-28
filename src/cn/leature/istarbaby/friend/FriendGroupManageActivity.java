package cn.leature.istarbaby.friend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.domain.FriendGroupListInfo;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.network.HttpGetUtil;
import cn.leature.istarbaby.network.HttpGetUtil.RequestGetDoneCallback;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.LzViewHolder;
import cn.leature.utils.dslv.DragSortController;
import cn.leature.utils.dslv.DragSortListView;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FriendGroupManageActivity extends Activity implements
		OnClickListener, OnItemClickListener {

	private TextView titleBarTitle;
	private ImageButton titleBarBack, titleBarEdit;
	private AlertDialog mDeletegroupDialog;

	private ArrayList<String> mGroupNameData = null;
	private ArrayList<String> mGroupIdData = null;
	private AlertDialog mAddgroupDialog;

	private EditText mAddGroupName;

	private DragSortListView lv;
	public int dragStartMode = DragSortController.ON_DOWN;
	public int removeMode = DragSortController.CLICK_REMOVE;
	public boolean sortEnabled = true;
	public boolean dragEnabled = true;
	private GroupListManageAdapter mAdapter;
	private List<body> mListItem;

	private LzProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_group_manage);

		mProgressDialog = new LzProgressDialog(this);
		mProgressDialog.setCancelable(true);

		initUI();
		// 头部导航
		setCustomTitleBar();
	}

	/*
	 * "UserId（必须） GroupId GroupName EditType（ 1：添加，参数需要UserId，GroupName；
	 * 2：修改，参数需要UserId，GroupId，GroupName； 3：删除，参数需要UserId，GroupId）"
	 */
	private void initUI() {
		TextView addgroup = (TextView) findViewById(R.id.add_grouptext);
		addgroup.setOnClickListener(this);

		lv = (DragSortListView) findViewById(R.id.group_dslistview);
		lv.setDragScrollProfile(ssProfile);
		lv.setOnItemClickListener(this);

		DragSortController mController = buildController(lv);
		lv.setFloatViewManager(mController);
		lv.setOnTouchListener(mController);

		lv.setDragEnabled(dragEnabled);
		lv.setDropListener(onDrop);

		// 取得组列表
		Map<String, String> param = new HashMap<String, String>();
		param.put("UserId", LoginInfo.getLoginUserId(this));
		geDetail(param, ConstantDef.cUrlContactGroupEdit, "取得分组列表失败");
	}

	private void geDetail(Map<String, String> param, String url,
			final String msg) {
		mProgressDialog.show();

		HttpGetUtil httpGetUtil = new HttpGetUtil(new RequestGetDoneCallback() {

			@Override
			public void requestWithGetDone(String result) {
				mProgressDialog.dismiss();

				if ((result == null) || ("0".equals(result))) {
					Toast.makeText(FriendGroupManageActivity.this, msg,
							Toast.LENGTH_LONG).show();
					return;
				}

				mListItem = new ArrayList<body>();
				mGroupNameData = new ArrayList<String>();
				mGroupIdData = new ArrayList<String>();
				JSONArray jsonArray;
				try {
					jsonArray = new JSONArray(result);
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);

						FriendGroupListInfo Groupid = new FriendGroupListInfo(jsonObject);

						mGroupIdData.add(Groupid.getGroup_id());
						mGroupNameData.add(Groupid.getGroup_name());

						body b = new body();
						b.groupid = Groupid.getGroup_id();
						b.name = Groupid.getGroup_name();
						b.position = Groupid.getPosition();
						mListItem.add(b);
					}

					mAdapter = new GroupListManageAdapter(
							FriendGroupManageActivity.this, mListItem);
					lv.setAdapter(mAdapter);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		httpGetUtil.execute(url, param);
	}

	private void setCustomTitleBar() {

		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("分组管理");

		titleBarBack = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarBack.setOnClickListener((this));
		titleBarBack.setVisibility(View.INVISIBLE);

		titleBarEdit = (ImageButton) this.findViewById(R.id.titlebar_rightbtn1);
		titleBarEdit.setOnClickListener(this);
		titleBarEdit.setBackgroundResource(R.drawable.selector_hd_cmp);
	}

	private void deleteGroupDialog(final int position) {
		// 删除组
		View layout = getLayoutInflater().inflate(R.layout.delete_dialog, null);
		Button btnDelete = (Button) layout.findViewById(R.id.delete_queding);
		// 提示信息
		TextView title = (TextView) layout.findViewById(R.id.alert_text);
		title.setText("确定要删除吗？删除该分组后，组内联系人将移至默认分组。");

		btnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDeletegroupDialog.dismiss();

				Map<String, String> param = new HashMap<String, String>();
				param.put("UserId", LoginInfo
						.getLoginUserId(FriendGroupManageActivity.this));
				param.put("GroupId", mGroupIdData.get(position));
				param.put("EditType", "3");

				geDetail(param, ConstantDef.cUrlContactGroupEdit, "删除失败");
			}
		});

		Button btnCancel = (Button) layout.findViewById(R.id.delete_quxiao);
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDeletegroupDialog.dismiss();
			}
		});

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		mDeletegroupDialog = builder.create();
		mDeletegroupDialog.show();
		mDeletegroupDialog.setContentView(layout);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_rightbtn1:
			setResult(Activity.RESULT_OK);
			this.finish();
			break;
		case R.id.add_grouptext:
			addGroupDialog("add", -1);
			break;
		}
	}

	private void addNewGroup(String mode, int position) {
		mAddgroupDialog.dismiss();

		String GroupName = mAddGroupName.getText().toString().trim();

		// 添加分组
		if ("add".equals(mode)) {
			Map<String, String> param = new HashMap<String, String>();
			param.put("UserId",
					LoginInfo.getLoginUserId(FriendGroupManageActivity.this));
			param.put("GroupName", GroupName);
			param.put("EditType", "1");
			geDetail(param, ConstantDef.cUrlContactGroupEdit, "添加失败");

		} else if ("upadete".equals(mode)) {

			Map<String, String> param = new HashMap<String, String>();
			param.put("UserId",
					LoginInfo.getLoginUserId(FriendGroupManageActivity.this));
			param.put("GroupId", mGroupIdData.get(position));
			param.put("GroupName", GroupName);
			param.put("EditType", "2");
			geDetail(param, ConstantDef.cUrlContactGroupEdit, "修改失败");
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backPrePage();
		}

		return super.onKeyDown(keyCode, event);
	}

	private void backPrePage() {
		this.setResult(Activity.RESULT_CANCELED);
		this.finish();
	}

	private void addGroupDialog(final String mode, final int position) {
		LayoutInflater inflater = getLayoutInflater();
		View inflate = inflater.inflate(R.layout.addgroup_dailog_layout,
				(ViewGroup) findViewById(R.id.dialog));

		Button btn_ok = (Button) inflate
				.findViewById(R.id.addgroupdialog_btn_ok);
		Button btn_close = (Button) inflate
				.findViewById(R.id.addgroupdialog_btn_close);

		mAddGroupName = (EditText) inflate
				.findViewById(R.id.addgroupdialog_pwd);
		final TextView titletext = (TextView) inflate
				.findViewById(R.id.groupdialogtitle);
		titletext.setText("请输入分组名称");

		btn_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				addNewGroup(mode, position);
			}
		});

		btn_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mAddgroupDialog.dismiss();
			}
		});

		mAddgroupDialog = new AlertDialog.Builder(this).setView(inflate).show();
	}

	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			if (from == to) {
				// 没有拖动
				return;
			}

			body item = (body) mListItem.get(from);
			Map<String, String> param = new HashMap<String, String>();
			param.put("UserId",
					LoginInfo.getLoginUserId(FriendGroupManageActivity.this));
			param.put("GroupId", item.getGroupid());
			param.put("PositionFrom", "" + item.getPosition());

			body item2 = (body) mListItem.get(to);
			param.put("PositionTo", "" + item2.getPosition());

			geDetail(param, ConstantDef.cUrlContactGroupPositionChange, "移动失败");
		}
	};

	private DragSortListView.DragScrollProfile ssProfile = new DragSortListView.DragScrollProfile() {
		@Override
		public float getSpeed(float w, long t) {
			if (w > 0.8f) {
				return ((float) mAdapter.getCount()) / 0.001f;
			} else {
				return 10.0f * w;
			}
		}
	};

	/**
	 * Called in onCreateView. Override this to provide a custom
	 * DragSortController.
	 */
	public DragSortController buildController(DragSortListView dslv) {
		DragSortController controller = new DragSortController(dslv);
		controller.setSortEnabled(sortEnabled);

		controller.setDragHandleId(R.id.drag_handle);
		controller.setDragInitMode(dragStartMode);

		controller.setBackgroundColor(getResources().getColor(
				R.color.translucence));

		return controller;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		addGroupDialog("upadete", position);
	}

	class GroupListManageAdapter extends BaseAdapter {

		private Context context;
		List<body> items;// 适配器的数据源

		public GroupListManageAdapter(Context context, List<body> list) {
			this.context = context;
			this.items = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return items.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return items.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		public void remove(int arg0) {// 删除指定位置的item
			items.remove(arg0);
			this.notifyDataSetChanged();// 不要忘记更改适配器对象的数据源
		}

		public void insert(body item, int arg0) {
			items.add(arg0, item);
			this.notifyDataSetChanged();
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			convertView = getLayoutInflater().inflate(
					R.layout.list_item_click_remove, null);

			body item = (body) getItem(position);
			TextView txtTitle = LzViewHolder.get(convertView, R.id.text);
			txtTitle.setText(item.getName());

			ImageView imgRemove = LzViewHolder.get(convertView,
					R.id.click_remove);
			imgRemove.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// 删除分组
					if (getCount() <= 1) {
						Toast.makeText(FriendGroupManageActivity.this,
								"最后一个分组不能删除", Toast.LENGTH_LONG).show();
						return;
					}

					deleteGroupDialog(position);
				}
			});

			return convertView;
		}
	}

	class body { // 放置adapter数据的类
		String groupid;
		String name;
		String position;

		public String getGroupid() {
			return groupid;
		}

		public void setGroupid(String id) {
			this.groupid = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPosition() {
			return position;
		}

		public void setPosition(String pos) {
			this.position = pos;
		}
	}

}
