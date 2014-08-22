package net.uyghurdev.uyghurepubreader.re;

import java.util.List;
import java.util.Vector;

import android.util.Log;

public class NCX {

	private  List<PlayOrder> order;
	public NCX(){
		order=new Vector<PlayOrder>();
		
	}
	
	public List<PlayOrder> getAllOrder(){
		return order;
	}
	
	public void addPlayOrder(PlayOrder pOrder){
		order.add(pOrder);
//		Log.d("My Log", "PlayOrder is added!"+order.size());
	}
	
	public String getOrder(int location){
		return order.get(location).getFileName();
	}
	
	public String getChapterName(int location){
		return order.get(location).getChapterName();
	}
}
