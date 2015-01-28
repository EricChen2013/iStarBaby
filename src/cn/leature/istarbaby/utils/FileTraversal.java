/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: FileTraversal.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.utils
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-6-11 下午2:49:28
 * @version: V1.0  
 */
package cn.leature.istarbaby.utils;

/**
 * @ClassName: FileTraversal
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-6-11 下午2:49:28
 */
import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class FileTraversal implements Parcelable {

	// 图片的目录名称
	private String filePathName;
	// 目录下图片的文件列表
	private List<String> fileContentList;

	public FileTraversal() {
		super();

		this.fileContentList = new ArrayList<String>();
	}

	public String getFilePathName() {
		return filePathName;
	}

	public void setFilePathName(String filePathName) {
		this.filePathName = filePathName;
	}

	public List<String> getFileContentList() {
		return fileContentList;
	}

	public void setFileContentList(List<String> fileContentList) {
		this.fileContentList = fileContentList;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(filePathName);
		dest.writeList(fileContentList);
	}

	public static final Parcelable.Creator<FileTraversal> CREATOR = new Creator<FileTraversal>() {

		@Override
		public FileTraversal[] newArray(int size) {
			return null;
		}

		@Override
		public FileTraversal createFromParcel(Parcel source) {
			FileTraversal ft = new FileTraversal();
			ft.filePathName = source.readString();
			ft.fileContentList = source.readArrayList(FileTraversal.class
					.getClassLoader());

			return ft;
		}
	};
}
