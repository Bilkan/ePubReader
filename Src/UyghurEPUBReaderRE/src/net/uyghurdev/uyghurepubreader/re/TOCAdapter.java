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

public class TOCAdapter extends BaseAdapter {

	private Activity activity;
	private String[] map;
	private static LayoutInflater inflater = null;
	
	public TOCAdapter(Activity a, String[] chapters){
		
		activity = a;
		map = chapters;
		inflater = (LayoutInflater) activity
		.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return map.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return map[position];
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
			vi = inflater.inflate(R.layout.toclist, null);
		}
		TextView color=(TextView)vi.findViewById(R.id.item);
		color.setTypeface(Properties.UIFont);
		color.setText(ArabicUtilities.reshape(map[position]));
		
		return vi;
	}


}
