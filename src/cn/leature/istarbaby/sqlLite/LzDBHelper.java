/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: LzDBHelper.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.sqlLite
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-8-4 上午9:42:57
 * @version: V1.0  
 */
package cn.leature.istarbaby.sqlLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @ClassName: LzDBHelper
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-8-4 上午9:42:57
 */
public class LzDBHelper {
	static private DatabaseHelper mDbHelper;
	static private SQLiteDatabase mDb;

	private static final String DATABASE_NAME = "iStarBaby.db";

	private static final int DATABASE_VERSION = 1;

	private final Context mCtx;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}

	public LzDBHelper(Context ctx) {
		this.mCtx = ctx;
	}

	public LzDBHelper open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {

		mDb.close();
		mDbHelper.close();
	}

	/**
	 * 插入数据 参数：tableName 表名 initialValues 要插入的列对应值
	 * */
	public long insert(String tableName, ContentValues initialValues) {

		return mDb.insert(tableName, null, initialValues);
	}

	/**
	 * 删除数据 参数：tableName 表名 deleteCondition 删除的条件 deleteArgs
	 * 如果deleteCondition中有“？”号，将用此数组中的值替换
	 * */
	public boolean delete(String tableName, String deleteCondition,
			String[] deleteArgs) {

		return mDb.delete(tableName, deleteCondition, deleteArgs) > 0;
	}

	/**
	 * 更新数据 参数：tableName 表名 initialValues 要更新的列 selection 更新的条件 selectArgs
	 * 如果selection中有“？”号，将用此数组中的值替换
	 * */
	public boolean update(String tableName, ContentValues initialValues,
			String selection, String[] selectArgs) {
		int returnValue = mDb.update(tableName, initialValues, selection,
				selectArgs);

		return returnValue > 0;
	}

	/**
	 * 取得一个列表 参数：tableName 表名 columns 返回的列 selection 查询条件 selectArgs
	 * 如果selection中有“？”号，将用此数组中的值替换
	 * */
	public Cursor findList(String tableName, String[] columns,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy) {

		return mDb.query(tableName, columns, selection, selectionArgs, groupBy,
				having, orderBy);
	}

	/**
	 * 取得单行记录 参数：tableName 表名 columns 返回的列 selection 查询条件 selectArgs
	 * 如果selection中有“？”号，将用此数组中的值替换
	 * */
	public Cursor findInfo(String tableName, String[] columns,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy, String limit, boolean distinct)
			throws SQLException {

		Cursor mCursor = mDb.query(distinct, tableName, columns, selection,
				selectionArgs, groupBy, having, orderBy, limit);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		return mCursor;
	}

	/**
	 * 执行sql 参数：sql 要执行的sql
	 * 
	 * */
	public void execSQL(String sql) {
		mDb.execSQL(sql);
	}

	/**
	 * 判断某记录是否存在
	 * 
	 * @param tabName
	 *            表名
	 * @return
	 */
	public boolean isRecordExist(String tableName, String[] columns,
			String selection, String[] selectionArgs) {
		boolean result = false;

		if (tableName == null) {
			return false;
		}

		try {
			Cursor cursor = findInfo(tableName, columns, selection,
					selectionArgs, null, null, null, null, false);

			if (cursor == null) {
				return false;
			}

			if (cursor.getCount() > 0) {
				result = true;
			}

			cursor.close();
		} catch (Exception e) {
			// TODO: handle exception
		}

		return result;
	}

	/**
	 * 判断某张表是否存在
	 * 
	 * @param tabName
	 *            表名
	 * @return
	 */
	public boolean isTableExist(String tableName) {
		boolean result = false;
		if (tableName == null) {
			return false;
		}

		try {
			Cursor cursor = null;
			String sql = "select count(1) as c from sqlite_master where type ='table' and name ='"
					+ tableName.trim() + "' ";
			cursor = mDb.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}

			cursor.close();
		} catch (Exception e) {
			// TODO: handle exception
		}

		return result;
	}

	/**
	 * 判断某张表中是否存在某字段(注，该方法无法判断表是否存在，因此应与isTableExist一起使用)
	 * 
	 * @param tabName
	 *            表名
	 * @return
	 */
	public boolean isColumnExist(String tableName, String columnName) {
		boolean result = false;
		if (tableName == null) {
			return false;
		}

		try {
			Cursor cursor = null;
			String sql = "select count(1) as c from sqlite_master where type ='table' and name ='"
					+ tableName.trim()
					+ "' and sql like '%"
					+ columnName.trim() + "%'";
			cursor = mDb.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}

			cursor.close();
		} catch (Exception e) {
			// TODO: handle exception
		}

		return result;
	}
}
