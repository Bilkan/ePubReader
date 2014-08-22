package net.uyghurdev.uyghurepubreader.re;

import android.util.Log;

public class PlayOrder {

	private String fileName=null;
	private String chapterName=null;
	PlayOrder (){}
	
	
	void setFileName(String fn){
		fileName=fn;
//		Log.d("My Log", "FileName is done!");
	}
	
	String getFileName(){
		return fileName;
	}
	
	void setChapterName(String fn){
		chapterName=fn;
//		Log.d("My Log", "FileName is done!");
	}
	
	String getChapterName(){
		return chapterName;
	}
}
