package net.uyghurdev.uyghurepubreader.re;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class Settings extends Activity {

	 private Spinner spnFC, spnFS, spnM, spnS;
	 private  TextView tv1,tv2, tvFF,tvFFL,tvFSL,tvFCL,tvBgL,tvML,tvPSL;
	 private Button btnok, btncancel;
	 private SharedPreferences sharedPreferences, getPreferences;
	 ArrayList<HashMap<String,String>> fonts, colors;
	 int fontpos;
	 
	 boolean fontchanged=false;
	 ProgressBar prog;
	 Handler handler;
	 AlertDialog dlg = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		Context epubContext = null;
		try {
			epubContext = createPackageContext("net.uyghurdev.uyghurepubreader.re", 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

			getPreferences = epubContext.getSharedPreferences("UyghurEpubReaderSettings",
				Context.MODE_PRIVATE);
		tvFFL= (TextView)findViewById(R.id.tvFFLabel);
		tvFSL= (TextView)findViewById(R.id.tvFSLabel);
		tvFCL= (TextView)findViewById(R.id.tvFCLabel);
//		tvBgL= (TextView)findViewById(R.id.tvBCLabel);
		tvML= (TextView)findViewById(R.id.tvMLabel);
		tvPSL= (TextView)findViewById(R.id.tvSLabel);
		tvFFL.setTypeface(Properties.UIFont);
		tvFSL.setTypeface(Properties.UIFont);
		tvFCL.setTypeface(Properties.UIFont);
//		tvBgL.setTypeface(Properties.UIFont);
		tvML.setTypeface(Properties.UIFont);
		tvPSL.setTypeface(Properties.UIFont);
		tvFFL.setText(ArabicUtilities.reshape(getString(R.string.settings_fflabel)));
		tvFSL.setText(ArabicUtilities.reshape(getString(R.string.settings_fslabel)));
		tvFCL.setText(ArabicUtilities.reshape(getString(R.string.settings_fclabel)));
//		tvBgL.setText(ArabicUtilities.reshape(getString(R.string.settings_bclabel)));
		tvML.setText(ArabicUtilities.reshape(getString(R.string.settings_mlabel)));
		tvPSL.setText(ArabicUtilities.reshape(getString(R.string.settings_pslabel)));
		spnFC= (Spinner)findViewById(R.id.spnFontColor);
		prog = (ProgressBar)findViewById(R.id.progress);
		prog.setVisibility(View.GONE);
		tv1=(TextView) findViewById(R.id.tvSample1);
		tv2=(TextView) findViewById(R.id.tvSample2);
		tv1.setText(ArabicUtilities.reshape(getString(R.string.sample_1)));
		tv2.setText(ArabicUtilities.reshape(getString(R.string.sample_2)));
		tvFF = (TextView) findViewById(R.id.tvFontFace);
		spnFS = (Spinner) findViewById(R.id.spnFontSize);
		spnM = (Spinner) findViewById(R.id.spnMargin);
		spnS = (Spinner) findViewById(R.id.spnSeperator);
		btnok = (Button) findViewById(R.id.btnOK);
		btncancel = (Button) findViewById(R.id.btnCancel);
		btnok.setTypeface(Properties.UIFont);
		btncancel.setTypeface(Properties.UIFont);
		btnok.setText(ArabicUtilities.reshape(getString(R.string.settings_btn_ok)));
		btncancel.setText(ArabicUtilities.reshape(getString(R.string.settings_btn_cancel)));
		fonts= new ArrayList<HashMap<String,String>>();
		fontpos=Properties.FontPosition;
		fontchanged=Properties.FontChanged;
		if(fontchanged){
			fonts=Properties.Fonts;
		}
		fillFontSize();
		fillColor();
		 
		spnFC.setSelection(getColorPositionFromAdapter(getPreferences.getString("FontColor", "BLACK")));
		if(fontchanged){
			tvFF.setText(fonts.get(fontpos).get("fontName"));
		}else{
			tvFF.setText(getPreferences.getString("FontName", "SystemFont"));
		}
		spnFS.setSelection(getPositionFromAdapter(spnFS.getAdapter(),String.valueOf(getPreferences.getString("FontSize", "20"))));
		spnM.setSelection(getPositionFromAdapter(spnM.getAdapter(),String.valueOf(getPreferences.getString("Margin", "10"))));
		spnS.setSelection(getPositionFromAdapter(spnS.getAdapter(),String.valueOf(getPreferences.getString("PartSeperator", "10"))));
		tv1.setBackgroundColor(Color.WHITE);
		tv1.setTextColor(Color.parseColor(getPreferences.getString("FontColor", "BLACK")));
		tv1.setPadding((int)Properties.tryParse(spnM.getSelectedItem().toString()), 
				(int)Properties.tryParse(spnM.getSelectedItem().toString()), 
				(int)Properties.tryParse(spnM.getSelectedItem().toString()), 0);
		tv2.setBackgroundColor(Color.parseColor(getPreferences.getString("BackgroundColor", "WHITE")));
		tv2.setTextColor(Color.WHITE);
		tv2.setPadding((int)Properties.tryParse(spnM.getSelectedItem().toString()), 
				(int)Properties.tryParse(spnS.getSelectedItem().toString()), 
				(int)Properties.tryParse(spnM.getSelectedItem().toString()), 
				(int)Properties.tryParse(spnM.getSelectedItem().toString()));
		
		showFontEffect();
		
		//select font face
		tvFF.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				prog.setVisibility(View.VISIBLE);
				fonts.clear();
				Properties.FontChanged=true;
				setFontsToList();
			}

		});
		

		//select font size
		spnFS.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				tv1.setTextSize((float)Properties.tryParse(spnFS.getSelectedItem().toString()));
				tv2.setTextSize((float)Properties.tryParse(spnFS.getSelectedItem().toString()));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		
		spnM.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				tv1.setPadding((int)Properties.tryParse(spnM.getSelectedItem().toString()),
						(int)Properties.tryParse(spnM.getSelectedItem().toString()), 
						(int)Properties.tryParse(spnM.getSelectedItem().toString()),0);
				tv2.setPadding((int)Properties.tryParse(spnM.getSelectedItem().toString()),
						(int)Properties.tryParse(spnS.getSelectedItem().toString()), 
						(int)Properties.tryParse(spnM.getSelectedItem().toString()),
						(int)Properties.tryParse(spnM.getSelectedItem().toString()));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		
		spnS.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				tv2.setPadding((int)Properties.tryParse(spnM.getSelectedItem().toString()),
						(int)Properties.tryParse(spnS.getSelectedItem().toString()), 
						(int)Properties.tryParse(spnM.getSelectedItem().toString()),
						(int)Properties.tryParse(spnM.getSelectedItem().toString()));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});

		
		//select Text Color
		spnFC.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				tv1.setTextColor(Color.parseColor(colors.get(spnFC.getSelectedItemPosition()).get("EnColor")));
				tv2.setTextColor(Color.parseColor(colors.get(spnFC.getSelectedItemPosition()).get("EnColor")));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		
		//OK
		btnok.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				setConfig();
				gotoTextActivity();
			}
		});

		//Cancel
		btncancel.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoTextActivity();
			}
		}); 
	}
	
	//get position by name search in adapter
	private int getPositionFromAdapter(SpinnerAdapter adapter, String strValue) {
		int nPosition=0;
		for(int nIndex=0;nIndex<adapter.getCount();nIndex++)
		{
			if(adapter.getItem(nIndex).toString().trim().toLowerCase().equalsIgnoreCase(strValue.trim().toLowerCase()))
			{
				nPosition= nIndex;
				
			}
		}
		return nPosition;
	}
	
	private int getColorPositionFromAdapter( String strValue) {
		int nPosition=0;
		for(int nIndex=0;nIndex<colors.size();nIndex++)
		{
			if(colors.get(nIndex).get("EnColor").trim().toLowerCase().equalsIgnoreCase(strValue.trim().toLowerCase()))
			{
				nPosition= nIndex;
				
			}
		}
		return nPosition;
	}
	
	private int getFontPositionFromAdapter( String strValue) {
		int nPosition=0;
		for(int nIndex=0;nIndex<fonts.size();nIndex++)
		{
			if(fonts.get(nIndex).get("fullpath").trim().toLowerCase().equalsIgnoreCase(strValue.trim().toLowerCase()))
			{
				nPosition= nIndex;
			}
		}
		return nPosition;
	}
	
	private int getFontPositionFromTextView( String strValue) {
		int nPosition=0;
		for(int nIndex=0;nIndex<fonts.size();nIndex++)
		{
			if(fonts.get(nIndex).get("fontName").trim().toLowerCase().equalsIgnoreCase(strValue.trim().toLowerCase()))
			{
				nPosition= nIndex;
			}
		}
		return nPosition;
	}
	
	//goto Text Activity when press OK or Cancel
	private void gotoTextActivity() {
		Properties.FontChanged=false;
		Intent intent = new Intent();
		intent.setClass(Settings.this, ShowBook.class);
		Settings.this.startActivity(intent);
		finish();
	}
	
	//fill colors:text color and Background color
	private void fillColor() {
		colors= new ArrayList<HashMap<String,String>>();
		HashMap<String,String> map;
		map=new HashMap<String, String>();
		map.put("ArColor", getString(R.string.black)); //ArabicUtilities.reshape(getString(R.string.black)));// "قارا");
		map.put("EnColor", "BLACK");
		colors.add(map);
		map=new HashMap<String, String>();
		map.put("ArColor", getString(R.string.blue)); //ArabicUtilities.reshape(getString(R.string.blue)));//"كۆك");
		map.put("EnColor", "BLUE");
		colors.add(map);
		map=new HashMap<String, String>();
		map.put("ArColor", getString(R.string.green)); //ArabicUtilities.reshape(getString(R.string.green)));//"يىشىل");
		map.put("EnColor", "GREEN");
		colors.add(map);
		map=new HashMap<String, String>();
		map.put("ArColor", getString(R.string.red)); //ArabicUtilities.reshape(getString(R.string.red)));//"قىزىل");
		map.put("EnColor", "RED");
		colors.add(map);
		map=new HashMap<String, String>();
		map.put("ArColor", getString(R.string.white)); //ArabicUtilities.reshape(getString(R.string.white)));//"ئاق");
		map.put("EnColor", "WHITE");
		colors.add(map);
		ColorAdapter coladapter=new ColorAdapter(this,colors);
		spnFC.setAdapter(coladapter);
	}

	//fill Font Size
	private void fillFontSize() {
		String[] items = new String[] { "7", "8", "9", "10", "11",
				"12", "13", "14", "15", "16", "17", "18", "19", "20", "21",
				"22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
				"32", "33", "34", "35", "36", "37", "38", "39", "40" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnFS.setAdapter(adapter);
		spnM.setAdapter(adapter);
		spnS.setAdapter(adapter);
	}
	
	private void setFontsToList() {

		handler = new Handler() {
			public void handleMessage(Message msg) {
				prog.setVisibility(View.GONE);
				Properties.Fonts=fonts;
				fontAlert();
			}

			
		};

		Thread checkUpdate = new Thread() {
			public void run() {
				try {
					// ------------
					fillFontNames();
					// ------------

					handler.sendEmptyMessage(0);
				} catch (Throwable t) {

				}
			}
		};
		checkUpdate.start();
	}

	private void fontAlert() {
		// TODO Auto-generated method stub
		LayoutInflater inflater = LayoutInflater.from(this);
		final View textEntryView = inflater.inflate(R.layout.fontdialog, null);
		final TextView title = (TextView) textEntryView
				.findViewById(R.id.about_title);
		final ListView fontlist = (ListView) textEntryView
				.findViewById(R.id.content);
		title.setTypeface(Properties.UIFont);
		title.setText(ArabicUtilities.reshape(getString(R.string.settings_fflabel)));
		
		FontAdapter adapter = new FontAdapter(this, fonts);
		fontlist.setAdapter(adapter);
		final AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
		builder.setCancelable(true);
		builder.setView(textEntryView);

		fontlist.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				fontpos=Properties.FontPosition=arg2;
				fontchanged=true;
				tvFF.setText(fonts.get(fontpos).get("fontName"));
				showFontEffect();
				dlg.dismiss();
			}

		});
		dlg = builder.create();
		dlg.show();
//		builder.show();
	}
	

	private void showFontEffect() {
		// TODO Auto-generated method stub
		if(fontchanged){
//			Log.d("Selected Font",fonts.get(fontpos).get("fullpath"));
			if(fonts.get(fontpos).get("fullpath").equals("SystemFont")){
				tv1.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
				tv2.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
			}else{
				tv1.setTypeface(Typeface.createFromFile((fonts.get(fontpos).get("fullpath"))));
				tv2.setTypeface(Typeface.createFromFile((fonts.get(fontpos).get("fullpath"))));
			}
		}else{
			if(getPreferences.getString("Font", "SystemFont").equals("SystemFont")){
				tv1.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
				tv2.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
			}else{
				tv1.setTypeface(Typeface.createFromFile((getPreferences.getString("Font", "SystemFont"))));
				tv2.setTypeface(Typeface.createFromFile((getPreferences.getString("Font", "SystemFont"))));
			}
		}
	}
	
	
	//fill fonts names
	private void fillFontNames() {
		HashMap<String,String> map;
		List<String> fontNames = new ArrayList<String>();
//		File temp = new File("/system/fonts/");
//		final String fontSuffix = ".ttf";
//		FilenameFilter myFileNameFilter = new FilenameFilter() {
//			public boolean accept(File f, String name) {
//				return name.endsWith(fontSuffix);
//			}
//		};
//
//		for (File font : temp.listFiles(myFileNameFilter)) {
			map=new HashMap<String,String>();
//			String fontName = font.getName();
			map.put("fullpath", "SystemFont");
			map.put("fontName", "SystemFont");
			fonts.add(map);
//		}
		searchFonts("/sdcard/");

	}
	
	private void searchFonts(String resource) {
		// TODO Auto-generated method stub
		File directory=new File(resource);
		File[] files=directory.listFiles();
		if(files==null){
			files=new File[0];
		}

		HashMap<String,String> map;
		
		for (int nIndex = 0; nIndex < files.length; nIndex++) {
			map= new HashMap<String,String>();
			if(files[nIndex].isDirectory()){
				resource+=files[nIndex].getName()+"/";
				searchFonts(resource);
				resource=files[nIndex].getParent()+"/";
			}
			else if (files[nIndex].getName().toLowerCase().endsWith(".ttf")) {
				map=new HashMap<String,String>();
				map.put("fullpath", resource+files[nIndex].getName());
				map.put("fontName", files[nIndex].getName().subSequence(0,
						files[nIndex].getName().lastIndexOf(".")).toString());
				fonts.add(map);
			}
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			gotoTextActivity();
		}
		return super.onKeyDown(keyCode, event);
	}

	//set all settings when press OK
	private void setConfig()
	{
		sharedPreferences = getSharedPreferences("UyghurEpubReaderSettings", Context.MODE_PRIVATE);
		Editor prefsPrivateEditor = sharedPreferences.edit();
		if(fontchanged){
			prefsPrivateEditor.putString("Font", fonts.get(fontpos).get("fullpath"));
		}else{
			prefsPrivateEditor.putString("Font", getPreferences.getString("Font", "SystemFont"));
		}
		prefsPrivateEditor.putString("FontName", tvFF.getText().toString());
		prefsPrivateEditor.putString("FontSize", spnFS.getSelectedItem().toString());
		prefsPrivateEditor.putString("FontColor", colors.get(spnFC.getSelectedItemPosition()).get("EnColor"));
		prefsPrivateEditor.putString("Margin", spnM.getSelectedItem().toString());
		prefsPrivateEditor.putString("PartSeperator", spnS.getSelectedItem().toString());
		prefsPrivateEditor.commit();
		

	}

}
