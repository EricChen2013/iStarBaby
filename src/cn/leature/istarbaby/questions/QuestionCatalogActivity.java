package cn.leature.istarbaby.questions;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.domain.QuestionDetailInfo;
import cn.leature.istarbaby.domain.QuestionDetailInfo.QuestionDetailModel;
import cn.leature.istarbaby.domain.QuestionDetailInfo.QuestionListModel;
import cn.leature.istarbaby.utils.ConstantDef;

public class QuestionCatalogActivity extends Activity implements
		OnClickListener, OnItemClickListener {
	private TextView titleBarTitle;
	private ImageButton titleBarBack;
	private ImageButton titleBarEdit;
	private QuestionAdapter adapter;
	private ListView listView;
	private Bundle mBundle;
	private QuestionDetailInfo mQuestionDetailInfo;
	private ArrayList<String> listItems = new ArrayList<String>();
	private int list_position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question_list);
		initUI();
		setCustomTitleBar();

		mBundle = getIntent().getExtras();
		mQuestionDetailInfo = (QuestionDetailInfo) getIntent()
				.getSerializableExtra("Info");

		list_position = mBundle.getInt("list_position");

		// 去掉第一行是Title
		QuestionListModel questionInfo = mQuestionDetailInfo.getList().get(
				list_position - 1);
		// 设置标题
		listItems.add(questionInfo.getCatalog1_text());

		List<QuestionDetailModel> detailList = questionInfo.getQuestion_list();
		// 设置内容
		for (QuestionDetailModel listModel : detailList) {
			listItems.add(listModel.getCatalog2_text());
		}
		adapter = new QuestionAdapter(QuestionCatalogActivity.this, 0);
		adapter.setListItems(listItems);

		listView.setAdapter(adapter);
	}

	private void initUI() {
		listView = (ListView) findViewById(R.id.question_listview);
		listView.setOnItemClickListener(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			backPrePage();
		}

		return true;

	}

	private void backPrePage() {
		this.setResult(Activity.RESULT_CANCELED);
		this.finish();

		// 设定退出动画
		this.overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_right);
	}

	/**
	 * 
	 */
	private void setCustomTitleBar() {

		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("育儿百科");

		titleBarBack = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarBack.setOnClickListener((this));
		titleBarBack.setBackgroundResource(R.drawable.selector_hd_bk);

		titleBarEdit = (ImageButton) this.findViewById(R.id.titlebar_rightbtn1);
		titleBarEdit.setVisibility(View.INVISIBLE);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_leftbtn:
			backPrePage();
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent();

		intent.putExtra("CatalogInfo", mQuestionDetailInfo);
		mBundle.putInt("position", position - 1);
		intent.putExtras(mBundle);

		intent.setClass(QuestionCatalogActivity.this,
				QuestionDetailActivity.class);
		this.startActivityForResult(intent, ConstantDef.REQUEST_CODE_QA_DETAIL);

		// 设定启动动画
		this.overridePendingTransition(R.anim.activity_in_from_right,
				R.anim.activity_nothing_in_out);
	}

}
