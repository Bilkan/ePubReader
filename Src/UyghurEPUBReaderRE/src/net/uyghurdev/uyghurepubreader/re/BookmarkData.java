package net.uyghurdev.uyghurepubreader.re;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BookmarkData extends SQLiteOpenHelper{

	private final static String DB_PATH = "/data/data/net.uyghurdev.app.avaroid.epubreader/databases/";
	private static final String DATABASE_NAME = "avarreader";
	private final static int DATABASE_VERSION = 3;
	
	SQLiteDatabase db;
	Cursor cursor;
	public BookmarkData(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE bookmark(_id INTEGER PRIMARY KEY AUTOINCREMENT,filePath TEXT, MD5Code TEXT, chapter INTEGER, pageNum INTEGER, totalPage INTEGER);");
		db.execSQL("CREATE TABLE recentbooks(_id INTEGER PRIMARY KEY AUTOINCREMENT,filePath TEXT, fileName TEXT, time DATETIME);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	public void addBookmark(String fileName, String md5Code, int chapterNum, int pageNum, int totalPage){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("filePath", fileName);
		cv.put("MD5Code", md5Code);
		cv.put("chapter", chapterNum);
		cv.put("pageNum", pageNum);
		cv.put("totalPage", totalPage);
		db.insert("bookmark", "filePath", cv);
	}
	
	public void deleteBookmark(String filePath){
		SQLiteDatabase db = this.getWritableDatabase(); 
	    String[] args={filePath};
	    db.delete("bookmark", "filePath=?", args);
	}
	
	public void setBookmark(int id, int chapterNum, int pageNum, int totalPage){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("chapter", chapterNum);
		cv.put("pageNum", pageNum);
		cv.put("totalPage", totalPage);
		db.update("bookmark", cv, "_id="+id,null);
	}
	
	
	public Cursor getBookmark(int id){
		SQLiteDatabase db = this.getReadableDatabase();
		String[] selection={""+id};
		String[] columns={"chapter","pageNum","totalPage"};
		Cursor cursor = db.query("bookmark",null,"_id="+id, null, null, null, null);
		return cursor;
	}

	public Cursor getAllBookmarks() {
		// TODO Auto-generated method stub
		SQLiteDatabase db = this.getReadableDatabase();
		String[] columns={"_id","MD5Code"};
		Cursor cursor = db.query("bookmark",columns,null, null, null, null, null);
		return cursor;
	}

	public Cursor getRecentBooks() {
		// TODO Auto-generated method stub
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query("recentbooks", null,null, null, null, null, "time DESC");
		return cursor;
	}

	public void changeTime(String path, long time) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = this.getWritableDatabase();
		String[] args={path};
		ContentValues cv = new ContentValues();
		cv.put("time", time);
		db.update("recentbooks", cv, "filePath=?", args);
	}

	public void deleteOpened(String path) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = this.getWritableDatabase(); 
	    String[] args={path};
	    db.delete("recentbooks", "filePath=?", args);
	}

	public Cursor getRecent(String filePath) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = this.getReadableDatabase();
		String[] args={filePath};
		Cursor cursor = db.query("recentbooks", null,"filePath=?", args, null, null, null);
		return cursor;
	}

	public void addRecent(String filePath, String name, long time) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("filePath", filePath);
		cv.put("fileName", name);
		cv.put("time", time);
		db.insert("recentbooks", "filePath", cv);
	}
	

}
