package com.setting.app.friend;

import java.util.Comparator;

public class PinyinComparator implements Comparator
{

	// @Override
	// public int compare(Object o1, Object o2) {
	// String str1 = PinyinUtils.getPingYin((String) o1);
	// String str2 = PinyinUtils.getPingYin((String) o2);
	// return str1.compareTo(str2);
	// }
	//
	@Override
	public int compare(Object o1, Object o2)
	{
		String str1 = PinyinUtils.getPingYin(((UserInfo) o1).getUserName());
		String str2 = PinyinUtils.getPingYin(((UserInfo) o2).getUserName());
		return str1.compareTo(str2);
	}

}
