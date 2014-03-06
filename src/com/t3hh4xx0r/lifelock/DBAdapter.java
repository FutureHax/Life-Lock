package com.t3hh4xx0r.lifelock;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.t3hh4xx0r.lifelock.objects.Peek;

public class DBAdapter {

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, "peeks.db", null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_PEEKS);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS peeks;");
			db.execSQL(CREATE_PEEKS);

		}
	}

	private final Context context;
	private static DatabaseHelper DBHelper;

	public SQLiteDatabase db;

	private static final int DB_VERSION = 1;

	private static final String CREATE_PEEKS = "create table peeks (_id integer primary key autoincrement, "
			+ "key_locked long not null, key_unlocked long not null);";

	public DBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
		db = DBHelper.getReadableDatabase();
	}

	public void addPeek(Peek peek) {
		ContentValues values = new ContentValues();
		values.put("key_locked", peek.getLockedTime());
		values.put("key_unlocked", peek.getUnlockTime());
		db.insert("peeks", null, values);
	}

	public void close() {
		DBHelper.close();
	}

	public ArrayList<Peek> getPeeks() {
		ArrayList<Peek> res = new ArrayList<Peek>();
		Cursor c = getPeeksCursor();
		while (c.moveToNext()) {
			Peek peek = new Peek(c.getLong(c
					.getColumnIndex("key_locked")), c.getLong(c
					.getColumnIndex("key_unlocked")));
			res.add(peek);
		}
		return res;
	}

	public Cursor getPeeksCursor() {
		Cursor mCursor;
		mCursor = db.query("peeks", new String[] { "_id", "key_locked",
				"key_unlocked" }, null,
				null, null, null, null, null);

		return mCursor;
	}

	public DBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}
}
