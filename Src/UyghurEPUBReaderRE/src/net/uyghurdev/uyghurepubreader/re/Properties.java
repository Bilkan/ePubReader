package net.uyghurdev.uyghurepubreader.re;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Typeface;

public class Properties {

	public static boolean FontChanged = false;
	public static ArrayList<HashMap<String, String>> Fonts = null;
	public static int CurrentOrder=0;
	public static int CurrentPage=0;
	public static String FilePath="";
	public static int FontSize=20;
	public static int Margin=20;
	public static int PartSeperator=10;
	public static String FontName="Alkatip tor";
	public static int BookId=1;
	protected static int FontPosition=0;
	public static Typeface UIFont;
	
	public static Integer tryParse(String text) {
		  try {
		    return new Integer(text);
		  } catch (NumberFormatException e) {
		    return 10;
		  }
	}
}
