/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: SlidingMenuItem.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.slidingmenu
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-6-11 下午12:11:46
 * @version: V1.0  
 */
package cn.leature.istarbaby.slidingmenu;

/**
 * @ClassName: SlidingMenuItem
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-6-11 下午12:11:46
 * @author lintoto 
 * 
 */

import java.util.List;

import cn.leature.istarbaby.domain.ChildrenInfo;

public class SlidingMenuItem {

	private boolean isHeader = false;

	private boolean isFooter = false;

	private boolean isGroupTag;

	private int resImage;

	private String itemText;

	private int itemValue;

	private boolean isImageItem;

	private List<ChildrenInfo> childrenInfos;

	public SlidingMenuItem(boolean isHeader, boolean isFooter) {
		super();
		this.isHeader = isHeader;
		this.isFooter = isFooter;
	}

	public SlidingMenuItem(boolean isGroupTag, int resImage, String itemText,
			int value) {
		super();
		this.isGroupTag = isGroupTag;
		this.resImage = resImage;
		this.itemText = itemText;
		this.itemValue = value;
	}

	public SlidingMenuItem(boolean isGroupTag, boolean isImageItem,
			List<ChildrenInfo> list) {
		super();
		this.isGroupTag = isGroupTag;
		this.isImageItem = isImageItem;
		this.childrenInfos = list;
	}

	public boolean isHeader() {
		return isHeader;
	}

	public boolean isFooter() {
		return isFooter;
	}

	public boolean isGroupTag() {
		return isGroupTag;
	}

	public int getResImage() {
		return resImage;
	}

	public String getItemText() {
		return itemText;
	}

	public int getItemValue() {
		return itemValue;
	}

	public boolean isImageItem() {
		return isImageItem;
	}

	public List<ChildrenInfo> getChildrenInfos() {
		return childrenInfos;
	}
}
