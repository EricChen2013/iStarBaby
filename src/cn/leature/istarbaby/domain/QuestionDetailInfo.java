/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: QuestionDetailInfo.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.domain
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-6-12 下午1:36:28
 * @version: V1.0  
 */
package cn.leature.istarbaby.domain;

/**
 * @ClassName: QuestionDetailInfo
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-6-12 下午1:36:28
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class QuestionDetailInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8305046583861870582L;
	/**
	 * 
	 */
	private String title;
	private List<QuestionListModel> list;

	private String JSON_Title = "title";
	private String JSON_List = "list";

	private String JSON_C1Id = "catalog1_id";
	private String JSON_C1Text = "catalog1_text";
	private String JSON_QuestionList = "question_list";

	private String JSON_C2Id = "catalog2_id";
	private String JSON_C2Text = "catalog2_text";
	private String JSON_Detail = "detail";

	public QuestionDetailInfo() {
		// TODO Auto-generated constructor stub
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<QuestionListModel> getList() {
		return list;
	}

	public void setList(List<QuestionListModel> list) {
		this.list = list;
	}

	public QuestionDetailInfo(JSONObject jsonObject) {
		super();

		try {
			this.title = jsonObject.getString(JSON_Title);

			List<QuestionListModel> listData = new ArrayList<QuestionListModel>();

			// 编辑详细
			JSONArray jsonArray = jsonObject.getJSONArray(JSON_List);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonListModel = jsonArray.getJSONObject(i);
				QuestionListModel listModelInfo = new QuestionListModel(
						jsonListModel);
				listData.add(listModelInfo);
			}

			this.list = listData;
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public class QuestionListModel implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6704113402357342132L;
		private String catalog1_id;
		private String catalog1_text;
		private List<QuestionDetailModel> question_list;

		public String getCatalog1_id() {
			return catalog1_id;
		}

		public void setCatalog1_id(String catalog1_id) {
			this.catalog1_id = catalog1_id;
		}

		public String getCatalog1_text() {
			return catalog1_text;
		}

		public void setCatalog1_text(String catalog1_text) {
			this.catalog1_text = catalog1_text;
		}

		public List<QuestionDetailModel> getQuestion_list() {
			return question_list;
		}

		public void setQuestion_list(List<QuestionDetailModel> question_list) {
			this.question_list = question_list;
		}

		public QuestionListModel(JSONObject jsonObject) {
			try {
				this.catalog1_id = jsonObject.getString(JSON_C1Id);
				this.catalog1_text = jsonObject.getString(JSON_C1Text);

				JSONArray jsonArray = jsonObject
						.getJSONArray(JSON_QuestionList);

				this.question_list = new ArrayList<QuestionDetailModel>();
				for (int i = 0; i < jsonArray.length(); i++) {
					QuestionDetailModel model = new QuestionDetailModel(
							jsonArray.getJSONObject(i));

					this.question_list.add(model);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public class QuestionDetailModel implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -298231252766901720L;
		private String catalog2_id;
		private String catalog2_text;
		private String detail;

		public String getCatalog2_id() {
			return catalog2_id;
		}

		public void setCatalog2_id(String catalog2_id) {
			this.catalog2_id = catalog2_id;
		}

		public String getCatalog2_text() {
			return catalog2_text;
		}

		public void setCatalog2_text(String catalog2_text) {
			this.catalog2_text = catalog2_text;
		}

		public String getDetail() {
			return detail;
		}

		public void setDetail(String detail) {
			this.detail = detail;
		}

		public QuestionDetailModel(JSONObject jsonObject) {
			try {
				this.catalog2_id = jsonObject.getString(JSON_C2Id);
				this.catalog2_text = jsonObject.getString(JSON_C2Text);
				this.detail = jsonObject.getString(JSON_Detail);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
