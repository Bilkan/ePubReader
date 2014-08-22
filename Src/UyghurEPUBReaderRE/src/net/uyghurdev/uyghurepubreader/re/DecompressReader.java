package net.uyghurdev.uyghurepubreader.re;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

public class DecompressReader {
	
	private String _encode = "UTF-8";
	
	private String _fileName ;
	
	Chapter chapter;
	
	private String _path;
	
	private ZipFile _zipFile;
	
	public DecompressReader(String fileName) throws IOException
	{
		_fileName = fileName;
		_zipFile = new ZipFile(_fileName);
	}
	
	public void initialWork() throws IOException, SAXException, ParserConfigurationException{
		InputStream is=this.getRootfilePath("META-INF/container.xml");
		String path=this.getPath(is);
		setRelativePath(path);
	}
	
	public NCX getNCX() throws IOException{
		InputStream nis=this.ncxFileStream();
		NCX ncx=this.setBookOrder(nis);
		return ncx;
	}
	
	public Chapter getChapter(String file) throws IOException, SAXException, ParserConfigurationException, XmlPullParserException{
		
		InputStream chins=this.getRootfilePath(_path+file);
		Writer writer = new StringWriter();
		char[] buffer = new char[1024];
		Reader reader = new BufferedReader(new InputStreamReader(chins, "UTF-8"));
		int n;
		while ((n = reader.read(buffer)) != -1) {
			writer.write(buffer, 0, n);
		}
		chins.close();
		String chapContent=writer.toString();
		
		Chapter chap=this.getChapterObject(chapContent);
		return chap;
	}
	
	
	
	private Chapter getChapterObject(String chapContent) throws XmlPullParserException, IOException{
		
		int start=chapContent.indexOf("<body");
//		Log.d("My log", "Start index: " + start);
		String body="<html>" + chapContent.substring(start);
		String content=body.replace("&nbsp;", " ");
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput(new StringReader(content));
		int eventType = xpp.getEventType();
		
//		loop1:
//		while (eventType != XmlPullParser.END_DOCUMENT){
//			Log.d("My log", "Next eventType");
//			if(eventType==XmlPullParser.START_TAG){
//				Log.d("My log", xpp.getName());
//				if(xpp.getName().equals("body")){
//					break loop1;
//				}
//			}
//			eventType=xpp.next();
//		}
//		
//		loop2:
//		while (eventType != XmlPullParser.END_DOCUMENT){
//			Log.d("My log", "In while loop2");
//			Log.d("My log", xpp.getName());
//			xpp.next();
//			if(eventType==XmlPullParser.START_TAG){
//				if(xpp.getName().equals("p")||xpp.getName().equals("b")){
//					break loop2;
//				}
//			}
//			if(eventType==XmlPullParser.START_TAG){
//				if (xpp.getName().equals("h1")||xpp.getName().equals("h2")||xpp.getName().equals("h3")||xpp.getName().equals("h4")||xpp.getName().equals("br")){
//					Log.d("My log", "Line break!");
//					chapter.setTitle("\n");
//				}
//			}
//			if(eventType == XmlPullParser.TEXT) {
//				chapter.setTitle(xpp.getText());
//			}
//			eventType=xpp.next();
//		}
//		
//		eventType = xpp.getEventType();
//		while(eventType != XmlPullParser.END_DOCUMENT){
//			if(eventType==XmlPullParser.START_TAG){
//				if(xpp.getName().equals("p") || xpp.getName().equals("br")){
//					Log.d("My log", "Line break!");
//					chapter.setText("\n");
//				}
//			}
//			if(eventType == XmlPullParser.TEXT) {
//				chapter.setText(xpp.getText());
//			}
//			eventType=xpp.next();
//		}
			
		String prgrph="";
		chapter = new Chapter();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if(eventType == XmlPullParser.START_DOCUMENT) {
//				Log.d("My log", "Start document");
			} 
			
			if(eventType == XmlPullParser.TEXT) {
				String strTemp=xpp.getText();
//				Log.d("My log", strTemp);
				if (xpp.isWhitespace()){
					//Do nothing
				}else{
					if(xpp.getText().contains("\n"))
					{
						prgrph+=xpp.getText().replace("\n", " ");
					}
					else
					{
						prgrph+=xpp.getText();
					}
				}
			}
			
			if (eventType == XmlPullParser.END_TAG) {
//				Log.d("My log", "Tag name: "+xpp.getName());
				if (xpp.getName().equals("h1") || xpp.getName().equals("h2")
						|| xpp.getName().equals("h3")
						|| xpp.getName().equals("h4")
						|| xpp.getName().equals("div")
						|| xpp.getName().equals("li")
						|| xpp.getName().equals("p")
						|| xpp.getName().equals("br")) {
					if(prgrph.length()>0){
						chapter.addParagraph(prgrph);
						prgrph="";
					}
				}
			}
			
//			if(eventType == XmlPullParser.START_TAG) {
//				Log.d("My log", "Start tag "+xpp.getName());
//			}
//			
//			if(eventType == XmlPullParser.END_TAG) {
//				Log.d("My log", "End tag "+xpp.getName());
//				
//				if(xpp.getName().equals("h1")||xpp.getName().equals("h2")||xpp.getName().equals("h3")||xpp.getName().equals("span")||xpp.getName().equals("b")||xpp.getName().equals("br")){
//					chapter.setTitle(xpp.getName()+xpp.nextText());
//				}else if(xpp.getName().equals("strong")||xpp.getName().equals("a")||xpp.getName().equals("li")||xpp.getName().equals("div")||xpp.getName().equals("em")||xpp.getName().equals("p")){
//					Log.d("My log", "Start tag add "+xpp.getName()+xpp.nextText());
//					chapter.setText(xpp.getName()+xpp.nextText());
					
//				}else if(xpp.getName().equals("link")){
//					_item.setLink(xpp.nextText());
//				}else if(xpp.getName().equals("author")){
//					_item.setAuthor(xpp.nextText());
//				}else if(xpp.getName().equals("pubDate")){
//					_item.setPubDate(xpp.nextText());
				
			
			
			eventType = xpp.next();
		}
		return chapter;
		
	}


//	private Chapter getChapterObject(InputStream chins) {
//		// TODO Auto-generated method stub
//		try {
//			
//			SAXParserFactory factory = SAXParserFactory.newInstance();
//			SAXParser parser = factory.newSAXParser();		
//			XMLReader xmlreader = parser.getXMLReader();
//			ContentReader theEpubHandler = new ContentReader();
//			xmlreader.setContentHandler(theEpubHandler);
//			xmlreader.parse(new InputSource(chins));			
//			return theEpubHandler.getChapter();			
//		} catch (Exception ee) {
//			return null;
//		}	
//	}

	public InputStream getRootfilePath(String filePath) throws IOException, SAXException, ParserConfigurationException
	{
		ZipEntry entry = this._zipFile.getEntry(filePath);
	
		if(entry == null)
			return null;
		InputStream inputStream = this._zipFile.getInputStream(entry);
		return inputStream;
	}
	
	private String getPath(InputStream in) {
		// TODO Auto-generated method stub
		try {
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();		
			XMLReader xmlreader = parser.getXMLReader();
			ContentReader theEpubHandler = new ContentReader();
			xmlreader.setContentHandler(theEpubHandler);
			xmlreader.parse(new InputSource(in));			
			return theEpubHandler.getFullPath();			
		} catch (Exception ee) {
			return null;
		}	
	}
	
	public InputStream ncxFileStream() throws IOException
	{
		InputStream inputStream=null;
      Enumeration entries = _zipFile.entries();
      while(entries.hasMoreElements())
      {
    	  ZipEntry currentEntry = (ZipEntry) entries.nextElement();
    	  if(currentEntry.getName().endsWith(".ncx"))
    	  {
    		   inputStream = this._zipFile.getInputStream(currentEntry); 
    	  }
    	  
      }
     return inputStream; 
	}

	private void setRelativePath(String path) {
		// TODO Auto-generated method stub
		for (int i=0; i<path.length();i++){
		String str=path.substring(0, path.length()-i);
			if(str.endsWith("/")){
				_path=str;
//				Log.d("My Log", _path);
				return;
			}
		}
	}
	
	
	private NCX setBookOrder(InputStream nis) {
		// TODO Auto-generated method stub
		try {
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();		
			XMLReader xmlreader = parser.getXMLReader();
			ContentReader theEpubHandler = new ContentReader();
			xmlreader.setContentHandler(theEpubHandler);
			xmlreader.parse(new InputSource(nis));			
			return theEpubHandler.getNCX();			
		} catch (Exception ee) {
			
		}
		return null;	
	}
	
	
	
}





