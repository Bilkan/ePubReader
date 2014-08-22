package net.uyghurdev.uyghurepubreader.re;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ColorAdapter extends BaseAdapter {
	
	private Activity activity;
	private ArrayList<HashMap<String,String>> map;
	private static LayoutInflater inflater = null;
	
	public ColorAdapter(Activity a, ArrayList<HashMap<String,String>> colors){
		activity = a;
		map = colors;
		inflater = (LayoutInflater) activity
		.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return map.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return map.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		View vi = convertView;
		if (convertView == null) {
			vi = inflater.inflate(R.layout.color, null);
		}
		TextView color=(TextView)vi.findViewById(R.id.coltext);
		color.setTypeface(Properties.UIFont);
		color.setText(ArabicUtilities.reshape(map.get(position).get("ArColor")));
		
		return vi;
	}

}
