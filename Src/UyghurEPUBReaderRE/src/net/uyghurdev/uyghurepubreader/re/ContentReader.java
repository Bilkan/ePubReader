package net.uyghurdev.uyghurepubreader.re;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class ContentReader  extends DefaultHandler {

	
	NCX ncx=new NCX();
	public PlayOrder order;
	Chapter chapter;
	
	final int TITLE = 1;
	final int H1 = 2;
	final int H2 = 3;
	final int H3 = 4;
	final int SPAN = 5;
	final int B = 6;
	final int BR = 7;
	final int STRONG = 8;
	final int A = 9;
	final int LI = 10;
	final int DIV = 11;
	final int EM = 12;
	final int P = 13;
	final int TEXT=14;
	String fullPath="";
	
	int currentstate = 0;

	/*
	 * Constructor
	 */
	public ContentReader() {
	}

	public String getFullPath() {
		// TODO Auto-generated method stub
		return fullPath;
	}
	
	public Chapter getChapter(){
		return chapter;
	}
	
	public NCX getNCX(){
		return ncx;
	}
	
	public void setFullPath(String path){
		fullPath=path;
	}

	public void startDocument() throws SAXException {
		
		chapter = new Chapter();
		order=new PlayOrder();
	}

	public void endDocument() throws SAXException {
//		Log.d("My Log", "Document ended!");
	}

	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		
		if (localName.equals("rootfile"))
		{
			setFullPath(atts.getValue("full-path"));
		}
		
		
		if (localName.equals("navPoint"))
		{
			order=new PlayOrder();
	//		String attr = atts.getValue("playOrder");
	//		order.setOrder(Integer.parseInt(attr));
			return;
		} 
		
		if (localName.equals("content")) {
			String attr = atts.getValue("src");
			order.setFileName(attr);
			return;
		}
		
		if (localName.equals("text")){
			currentstate=TEXT;
			return;
		}
		
//		Log.d("localName", localName);
		
		if (localName.equals("channel")) {
			currentstate = 0;
			return;
		}
		
		if (localName.equals("title")) {
			currentstate =TITLE;
			return;
		}
		if (localName.equals("h1")) {
			currentstate = H1;
			return;
		}
		if (localName.equals("h2")) {
			currentstate = H2;
			return;
		}
		if (localName.equals("h3")) {
			currentstate = H3;
			return;
		}
		if (localName.equals("span")) {
			currentstate = SPAN;
			return;
		}
		if (localName.equals("b")) {
			currentstate = B;
			return;
		}
		if (localName.equals("br")) {
			currentstate = BR;
			return;
		}
		if (localName.equals("strong")) {
			currentstate = STRONG;
			return;
		}
		if (localName.equals("a")) {
			currentstate = A;
			return;
		}
		if (localName.equals("li")) {
			currentstate = LI;
			return;
		}
		if (localName.equals("div")) {
			currentstate = DIV;
			return;
		}
		if (localName.equals("em")) {
			currentstate = EM;
			return;
		}
		if (localName.equals("p")) {
			currentstate = P;
			return;
		}

		currentstate = 0;
	}

	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		
		if (localName.equals("navPoint")){
			
			ncx.addPlayOrder(order);
			return;
		}
	}

	public void characters(char ch[], int start, int length) {
		String theString = new String(ch, start, length);
//		Log.i("My log", "characters[" + theString + "]");

		switch (currentstate) {
		case TEXT:
			if(theString.length()>0){
				order.setChapterName(theString);
			}
			currentstate = 0;
			break;
		case TITLE:
			currentstate = 0;
			break;
		case H1:
			if(theString.length()>0){
			chapter.setTitle(theString);
			}
			currentstate = 0;
			break;
		case H2:
			if(theString.length()>0){
			chapter.setTitle(theString);
			}
			currentstate = 0;
			break;
		case H3:
			if(theString.length()>0){
			chapter.setTitle(theString);
			}
			currentstate = 0;
			break;
		case SPAN:
			if(theString.length()>0){
			chapter.setTitle(theString);
			}
			currentstate = 0;
			break;
		case B:
			if(theString.length()>0){
			chapter.setTitle(theString);
			}
			currentstate = 0;
			break;
		case BR:
			if(theString.length()>0){
			chapter.setTitle(theString);
			}
			currentstate = 0;
			break;
		case STRONG:
			chapter.setText(theString);
			currentstate = 0;
			break;
		case A:
			chapter.setText(theString);
			currentstate = 0;
			break;
		case LI:
			chapter.setText(theString);
			currentstate = 0;
			break;
		case DIV:
			chapter.setText(theString);
			currentstate = 0;
			break;
		case EM:
			chapter.setText(theString);
			currentstate = 0;
			break;
		case P:
			chapter.setText(theString);
			currentstate = 0;
			break;

		default:
			return;
		}

	}
}
