package net.uyghurdev.uyghurepubreader.re;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ShowBook extends Activity {

	int margin, p = 0, height, fontSize, pageNum, totalPage=1, bookid=1, partseperator, currentpage = 0, currentOrder = 0;
	char a = 1600;
	String kashida = a + "", md5="";
	float kash;
	boolean reshape, PreChapLastPage = false;
	Line line;
	Page page;
//	Canvas canvas;
	Paint paint;
//	Bitmap newb;
	ImageView img;
	ImageView imgback;
	private NCX ncx;
	private Chapter chapter = null;
	Context otherAppsContext = null;
	private SharedPreferences getPreferences;
	String fileName;
	private DecompressReader reader;
	Display display;
	private BookmarkData bookmark;
	ProgressBar prog;
	private Md5Handler md5handler;
	boolean toRight = true;
	boolean containsError = false;
	int frontPage, prePage, nextPage;
	int xDown, yDown, xTouch = 0, yTouch = 0, xClickOffset = 0,
			yClickOffset = 0, xOffset = 0, yOffset = 0, xCurrent, yCurrent,
			xUp, yUp;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.paint);

		// ===================Initialize Variables=========================
		try {
			otherAppsContext = createPackageContext("net.uyghurdev.uyghurepubreader.re", 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getPreferences = otherAppsContext.getSharedPreferences("UyghurEpubReaderSettings",
				Context.MODE_PRIVATE);
		prog = (ProgressBar)findViewById(R.id.progress);
		margin = Integer.parseInt(getPreferences.getString("Margin", "10"));
		display = getWindowManager().getDefaultDisplay();
		img = (ImageView) findViewById(R.id.image);
		img.setPadding(margin, margin, margin, 0);
		imgback = (ImageView) findViewById(R.id.imageback);
		imgback.setPadding(margin, margin, margin, 0);
		fontSize = Integer.parseInt(getPreferences.getString("FontSize", "20"));
		partseperator = Properties.PartSeperator;
		bookmark = new BookmarkData(this);
		md5handler= new Md5Handler();
		frontPage = 0;
		prePage = -display.getWidth();
		nextPage = display.getWidth();
		paint = new Paint();
		if(getPreferences.getString("Font", "SystemFont").equals("SystemFont"))
		{
		}else{
			if(new File(getPreferences.getString("Font", "SystemFont")).exists()){
				paint.setTypeface(Typeface.createFromFile(getPreferences.getString("Font", "SystemFont")));
			}else{
				Editor prefsPrivateEditor = getPreferences.edit();
				prefsPrivateEditor.putString("Font", "SystemFont");
				prefsPrivateEditor.putString("FontName", "SystemFont");
				prefsPrivateEditor.commit();
			}
			
		}
		paint.setColor(Color.parseColor(getPreferences.getString("FontColor", "BLACK")));
		paint.setTextSize(fontSize);
		paint.setAntiAlias(true);
		height = getFontHeight(paint);
		paint.setSubpixelText(true);
		paint.setTextAlign(Paint.Align.RIGHT);
		kash = getKashidaLength(paint, kashida);
		fileName = Properties.FilePath;
		
		currentOrder = Properties.CurrentOrder;
		// ==================================================================

		// =====================Get Book Order==========================
		try {

			reader = new DecompressReader(fileName);
			reader.initialWork();
			ncx = reader.getNCX();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			makeToast(0);

		}
		// ==============================================================

		showContent();

		// ===================Turn Page Over by Touching==================
		img.setOnTouchListener(new ImageView.OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				int tileSize = display.getWidth();
//				Log.d("Desplay", "screen width: " + tileSize);
				// TODO Auto-generated method stub

				// ---------------Action Down---------------------
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Log.d("Action",
							"down: " + event.getX() + "," + event.getY());
					xTouch = (int) event.getX();
					yTouch = (int) event.getY();
					xDown = (int) event.getX();
					yDown = (int) event.getY();
					xClickOffset = xTouch; // *****************
					yClickOffset = yTouch; // ****************

					// -----------------Action move------------------------
				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
//					Log.d("Action", "moved.");
					xOffset += xTouch - (int) event.getX();
					// offsets for scrolling the game board and other stuff
					// here, isn't related to clicking
					yOffset += yTouch - (int) event.getY();
					xCurrent = (int) event.getX();
					yCurrent = (int) event.getY();
					toRight = xCurrent - xTouch >= 0 ? true : false;
//					Log.d("Action Move", "moved." + xTouch + ", " + yDown
//							+ "  to  " + xCurrent + ", " + yCurrent
//							+ ".   Distance:" + (xCurrent - xTouch));
//					Log.d("toRight", ""+toRight);
					movePage();
					xTouch = xCurrent;
					yTouch = yCurrent;

					// ------------------Action Up----------------------------
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
//					Log.d("Action", "up: " + event.getX() + "," + event.getY());
//					Log.d("Action", "distance: "
//							+ (event.getX() - xClickOffset) + "," + tileSize
//							/ 4);

					// ++++++++++++++Moved to Right, X scale > 1/4 Screen
					// Size+++++++++
					
					xUp = (int) event.getX();
					yUp = (int) event.getY();

					// ++++++++++++++Moved to Right, X scale > 1/4 Screen
					// Size+++++++++
					if (toRight) {
						if (xUp - xDown > tileSize / 4) {

							if (currentpage < chapter.getPageNum() - 1)// This is
								// not Last
								// Page,
								// Move to
								// Next Page
							{
								currentpage++;
								Properties.CurrentOrder = currentOrder;
//								Log.d("Total Page",""+chapter.getPageNum());
								bookmark.setBookmark(bookid, currentOrder, currentpage+1, chapter.getPageNum());

								showPageToRight();

							} else// This is Last Page
							{
								if (currentOrder == ncx.getAllOrder().size()-1)// This
									// is
									// Last
									// Chapter,
									// Do
									// Nothing!
								{
									moveBack();
								} else// This is not Last Chapter, Show Next Chapter
								{
									moveBack();
									currentOrder++;
									Properties.CurrentOrder = currentOrder;
									currentpage = 0;
//									Log.d("Total Page",""+chapter.getPageNum());
									bookmark.setBookmark(bookid, currentOrder, currentpage, chapter.getPageNum());

									showContent();

}

}
						} else {
							moveBack();
						}

					} else {

						// ++++++++++++++Moved to Left, X scale > 1/4 Screen
						// Size+++++++++
						
//						Log.d("Up",(xUp-xDown)+", "+(-tileSize/4));
						if ((xUp - xDown) < -tileSize / 4) {
							
							if (currentpage == 0) // This is First Page
							{

								if (currentOrder == 0)// This is First Chapter, Do
														// Nothing!
								{
									moveBack();
								} else// Move to Previous Chapter
								{
									moveBack();
									PreChapLastPage = true;
									currentOrder--;
									Properties.CurrentOrder = currentOrder;
//									Log.d("Total Page",""+chapter.getPageNum());
									bookmark.setBookmark(bookid, currentOrder, currentpage+1, chapter.getPageNum());
									showContent();
								}
								
							}

							else // This is not First Page, Move to Previous
									// Page
							{
								currentpage--;
								Properties.CurrentOrder = currentOrder;
//								Log.d("Total Page",""+chapter.getPageNum());
								bookmark.setBookmark(bookid, currentOrder, currentpage+1, chapter.getPageNum());
								showPageToLeft();
							}
							
							
						} else {
							moveBack();
						}

					}
					frontPage = 0;
					prePage = display.getWidth();
					nextPage = -display.getWidth();
				}
				return true;
			}


			

		});
		// ==========================================================
	}

	private void showContent() {
		// TODO Auto-generated method stub

		prog.setVisibility(View.VISIBLE);
		md5 = md5handler.md5Calc(new File(fileName));
		bookid=bookId(md5);
		if(bookid==-1){
			bookmark.addBookmark(fileName, md5, 0, 1, 100);
			Properties.BookId=bookid;
		}else{
			Properties.BookId=bookid;
			Cursor bm = bookmark.getBookmark(bookid);
			bm.moveToFirst();
			currentOrder=bm.getInt(3);
			pageNum=bm.getInt(4);
			totalPage=bm.getInt(5);
		}

		
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				prog.setVisibility(View.GONE);
				if (containsError) {
					makeToast(1);
			
				} else {
					if (PreChapLastPage) {
						currentpage = chapter.getPageNum() - 1;
						bookmark.setBookmark(bookid, currentOrder, currentpage+1, chapter.getPageNum());
						PreChapLastPage=false;
					}else{
						currentpage=chapter.getPageNum()*pageNum/totalPage-1<0?0:chapter.getPageNum()*pageNum/totalPage-1;
					}
//					Log.d("Current Page",""+currentpage);
					img.setImageBitmap(showPage(currentpage));
				}
			}
		};

		Thread checkUpdate = new Thread() {
			public void run() {
				

				String filePath = ncx.getOrder(currentOrder);
//				Log.d("My Log", "Done!" + ncx.getAllOrder().size());
				try {
					chapter = reader.getChapter(filePath);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					containsError = true;
				}
				if (!containsError) {
					setPages();
				}
				handler.sendEmptyMessage(0);

			}

			
		};

		checkUpdate.start();
	}

	private int bookId(String md5) {
		// TODO Auto-generated method stub
		Cursor allBookmark=bookmark.getAllBookmarks();
		int id=-1;
		allBookmark.moveToFirst();
		for(int x=0;x<allBookmark.getCount();x++){
//			Log.d("MD5", allBookmark.getString(1));
			if(allBookmark.getString(1).equals(md5)){
				id=allBookmark.getInt(0);
				break;
			}
		
			allBookmark.moveToNext();
		}
		return id;
	}

	private void setPages() {
		// TODO Auto-generated method stub
		page = new Page();
		int start = 0;
		int x = 0;
		int prg = 0;
		int y_axis = height;
		int x_axis;
		boolean hasMoreText = true;
		boolean firstLine = true;
		String para = chapter.getParagraph(prg);
		while (hasMoreText) {
			
			boolean endOfPar = false;
			int last;
			line = new Line();
			String lineString;
			int linelength;

				if (firstLine) {
					linelength = paint.breakText(ArabicUtilities.reshape(para), true, display.getWidth()
							- 2 * margin - fontSize, null);
				} else {
					linelength = paint.breakText(ArabicUtilities.reshape(para), true, display.getWidth()
							- 2 * margin, null);
				}
			

			if (linelength + 1 >= para.length()) {
				lineString = para;
				last = start + linelength;
				endOfPar = true;
			} else if (para.substring(start, start + linelength + 1).endsWith(
					".")) {
				last = para.substring(start, start + linelength + 1)
						.lastIndexOf(".");
				lineString = para.substring(start, start + linelength + 1)
						.substring(0, last + 1);
			} else {
				last = para.substring(start, start + linelength + 1)
						.lastIndexOf(' ');
				
				if(last==-1){
					last=linelength-1;
					lineString = para.substring(start, start + linelength + 1)
					.substring(0, last)+"-";
				}else{
					lineString = para.substring(start, start + linelength + 1)
						.substring(0, last);
				}
				
				
			}
			int end = start + last;
			int kashnum;
			if (firstLine) {
				kashnum = (int) ((display.getWidth() - 2 * margin - fontSize - getStringLength(
						paint, lineString)) / kash );
			} else {
				kashnum = (int) ((display.getWidth() - 2 * margin - getStringLength(
						paint, lineString)) / kash );
			}

			if (endOfPar) {
				line.setKashNum(0);
			} else {
				line.setKashNum(kashnum);
			}
			BiDi bd = new BiDi();
			line.setParNum(prg);
			line.setStart(start);
			line.setEnd(end);
			line.setLineString(bd.reverse(lineString));
			if (prg == 0) {
				x_axis = (int) ((display.getWidth() + getStringLength(paint,
						lineString))) / 2;
			} else {
				x_axis = firstLine ? display.getWidth() - 2* margin - fontSize
						: display.getWidth() - 2* margin;
			}
			line.setXAxis(x_axis);
			line.setYAxis(y_axis);
			page.addLine(line);
			// start = end + 1;
			if (endOfPar) {
				y_axis += height + partseperator;
				prg++;
				start = 0;
				firstLine = true;
				if (prg < chapter.getParSize()) {
					para = chapter.getParagraph(prg);
				}
			} else {
				y_axis += height;
				firstLine = false;
				para = para.substring(last + 1);
			}

			if (y_axis + height / 2 > display.getHeight() - 2 * margin) {
				chapter.addPage(page);
				page = new Page();
				y_axis = height;
			}

			if (prg >= chapter.getParSize()) {
				if (y_axis <= display.getHeight() - 2 * margin) {
					chapter.addPage(page);
				}
				hasMoreText = false;
			}
			x++;
//			Log.d("Break Line", "Current Line:" + x);
		}
		
	}

	private void showPageToRight() {
		// TODO Auto-generated method stub
		imgback.setImageBitmap(showPage(currentpage-1));
		
			Animation animback = new TranslateAnimation(xUp-xDown, display.getWidth(), 0, 0);
			animback.setDuration(500*(display.getWidth()-xUp+xDown)/display.getWidth());
			animback.setFillAfter(true);
			imgback.startAnimation(animback);
			
			img.setImageBitmap(showPage(currentpage));
			Animation anim = new TranslateAnimation(-display.getWidth()+xUp-xDown, 0, 0, 0);
			anim.setDuration(500*(display.getWidth()-xUp+xDown)/display.getWidth());
			anim.setFillAfter(true);
			img.startAnimation(anim);
		
	}
	
	private void showPageToLeft() {
		// TODO Auto-generated method stub
		imgback.setImageBitmap(showPage(currentpage+1));
		
		Animation animback = new TranslateAnimation(xUp - xDown,
				-display.getWidth(), 0, 0);
		animback.setDuration(500*(display.getWidth()-xUp+xDown)/display.getWidth());
		animback.setFillAfter(true);
		imgback.startAnimation(animback);
		img.setImageBitmap(showPage(currentpage));
		Animation anim = new TranslateAnimation(display.getWidth() + xUp- xDown, 0, 0, 0);
		anim.setDuration(500*(display.getWidth()-xUp+xDown)/display.getWidth());
		anim.setFillAfter(true);
		img.startAnimation(anim);
	}
	
	private void moveBack() {
		// TODO Auto-generated method stub
		if(xUp-xDown>=0){
				if (currentpage == chapter.getPageNum() - 1) {
					img.setImageBitmap(showPage(currentpage));
					Animation anim = new TranslateAnimation(xUp - xDown, 0, 0, 0);
					anim.setDuration(500*(xUp-xDown)/display.getWidth());
					anim.setFillAfter(true);
					img.startAnimation(anim);

				}else {
					imgback.setImageBitmap(showPage(currentpage + 1));
					Animation animback = new TranslateAnimation(xUp - xDown
							- display.getWidth(), -display.getWidth(), 0, 0);
					animback.setDuration(500*(xUp-xDown)/display.getWidth());
					animback.setFillAfter(true);
					imgback.startAnimation(animback);
					img.setImageBitmap(showPage(currentpage));
					Animation anim = new TranslateAnimation(xUp - xDown, 0, 0, 0);
					anim.setDuration(500*(xUp-xDown)/display.getWidth());
					anim.setFillAfter(true);
					img.startAnimation(anim);
				}
		}else{
			
				if (currentpage == 0) {
					img.setImageBitmap(showPage(currentpage));
					Animation anim = new TranslateAnimation(xUp - xDown, 0, 0, 0);
					anim.setDuration(500*(-xUp+xDown)/display.getWidth());
					anim.setFillAfter(true);
					img.startAnimation(anim);

				}else{
					imgback.setImageBitmap(showPage(currentpage - 1));
					Animation animback = new TranslateAnimation(xUp - xDown
							+ display.getWidth(), display.getWidth(), 0, 0);
					animback.setDuration(500*(-xUp+xDown)/display.getWidth());
					animback.setFillAfter(true);
					imgback.startAnimation(animback);
					img.setImageBitmap(showPage(currentpage));
					Animation anim = new TranslateAnimation(xUp - xDown, 0, 0, 0);
					anim.setDuration(500*(-xUp+xDown)/display.getWidth());
					anim.setFillAfter(true);
					img.startAnimation(anim);
				}
			
		}
		

	}

	private void movePage() {
		// TODO Auto-generated method stub
//		Log.d("Posotion",""+(xCurrent - xDown));
		if (xCurrent - xDown >= 0) {
			
			if (currentpage < chapter.getPageNum() - 1) {
				imgback.setImageBitmap(showPage(currentpage + 1));
			}else{
				imgback.setImageBitmap(null);
			}
				Animation animback = new TranslateAnimation(nextPage, nextPage
						+ xCurrent - xTouch, 0, 0);
				animback.setDuration(0);
				animback.setFillAfter(true);
				imgback.startAnimation(animback);
			
			img.setImageBitmap(showPage(currentpage));
			Animation anim = new TranslateAnimation(frontPage, frontPage
					+ xCurrent - xTouch, 0, 0);
			anim.setDuration(0);
			anim.setFillAfter(true);
			img.startAnimation(anim);
		} else {
			if (currentpage > 0) {
				imgback.setImageBitmap(showPage(currentpage - 1));
			}else{
				imgback.setImageBitmap(null);
			}
				Animation animback = new TranslateAnimation(prePage, prePage
						+ xCurrent - xTouch, 0, 0);
				animback.setDuration(0);
				animback.setFillAfter(true);
				imgback.startAnimation(animback);
			
			img.setImageBitmap(showPage(currentpage));
			Animation anim = new TranslateAnimation(frontPage, frontPage
					+ xCurrent - xTouch, 0, 0);
			anim.setDuration(0);
			anim.setFillAfter(true);
			img.startAnimation(anim);

		}
		frontPage += xCurrent - xTouch;
		prePage += xCurrent - xTouch;
		nextPage += xCurrent - xTouch;

	}
	
	
	
	private Bitmap showPage(int crp) {
		// TODO Auto-generated method stub
		Page currentPage = chapter.getPage(crp);
		Bitmap newb = Bitmap.createBitmap(display.getWidth() - 2 * margin,
				display.getHeight() -  margin, Config.ARGB_8888);

		Canvas canvas = new Canvas(newb);
		
			for (int l = 0; l < currentPage.getPageLines().size(); l++) {
				String line = currentPage.getPageLines().get(l).getLineString();

				int n = 0;
				for (int k = 0; k < currentPage.getPageLines().get(l)
						.getKashNum(); k++) {

					while (n < line.length() - 1) {
						if (
						// ((line.charAt(n) != ' '
						// && line.charAt(n) != '.'
						// && line.charAt(n) != '،'
						// && line.charAt(n) != '!'
						// && line.charAt(n) != '؟'
						// && line.charAt(n) != ':'
						// && (line.charAt(n + 1) == 'ى' || line.charAt(n + 1) ==
						// 'ې') || line.charAt(n + 1) == 'ي'))
						//
						// ||
						((line.charAt(n) == 'ن' || line.charAt(n) == 'ت'
								|| line.charAt(n) == 'ر' || line.charAt(n) == 'ز'
								|| line.charAt(n) == 'س' || line.charAt(n) == 'ش'
								|| line.charAt(n) == 'ب' || line.charAt(n) == 'پ'
								|| line.charAt(n) == 'ي' || line.charAt(n) == 'ث'
								|| line.charAt(n) == 'ح' || line.charAt(n) == 'ذ'
								|| line.charAt(n) == 'ص' || line.charAt(n) == 'ض'
								|| line.charAt(n) == 'ط' || line.charAt(n) == 'ظ'
								|| line.charAt(n) == 'ع' || line.charAt(n) == 'ج'
								|| line.charAt(n) == 'چ' || line.charAt(n) == 'خ'
								|| line.charAt(n) == 'غ' || line.charAt(n) == 'ق'
								|| line.charAt(n) == 'ف' || line.charAt(n) == 'د'
								|| line.charAt(n) == 'ھ' || line.charAt(n) == 'ۋ'
								|| line.charAt(n) == 'ژ' || line.charAt(n) == 'ل'
								|| line.charAt(n) == 'ك' || line.charAt(n) == 'گ'
								|| line.charAt(n) == 'ڭ' || line.charAt(n) == 1600) && (line
								.charAt(n + 1) == 'ن'
								|| line.charAt(n + 1) == 'ت'
								|| line.charAt(n + 1) == 'ث'
								|| line.charAt(n + 1) == 'ح'
								|| line.charAt(n + 1) == 'ص'
								|| line.charAt(n + 1) == 'ض'
								|| line.charAt(n + 1) == 'ط'
								|| line.charAt(n + 1) == 'ظ'
								|| line.charAt(n + 1) == 'ع'
								|| line.charAt(n + 1) == 'س'
								|| line.charAt(n + 1) == 'ش'
								|| line.charAt(n + 1) == 'ب'
								|| line.charAt(n + 1) == 'پ'
								|| line.charAt(n + 1) == 'ي'
								|| line.charAt(n + 1) == 'م'
								|| line.charAt(n + 1) == 'ج'
								|| line.charAt(n + 1) == 'چ'
								|| line.charAt(n + 1) == 'خ'
								|| line.charAt(n + 1) == 'غ'
								|| line.charAt(n + 1) == 'ق'
								|| line.charAt(n + 1) == 'ف'
								|| line.charAt(n + 1) == 'ھ'
								|| line.charAt(n + 1) == 'ل'
								|| line.charAt(n + 1) == 'ك'
								|| line.charAt(n + 1) == 'گ' || line.charAt(n + 1) == 'ڭ'))
								|| ((line.charAt(n) == 'ا' || line.charAt(n) == 'ە' || line
										.charAt(n) == 1600) && (line.charAt(n + 1) == 'ئ'
										|| line.charAt(n + 1) == 'ن'
										|| line.charAt(n + 1) == 'ت'
										|| line.charAt(n + 1) == 'س'
										|| line.charAt(n + 1) == 'ش'
										|| line.charAt(n + 1) == 'ب'
										|| line.charAt(n + 1) == 'پ'
										|| line.charAt(n + 1) == 'ي'
										|| line.charAt(n + 1) == 'م'
										|| line.charAt(n + 1) == 'ج'
										|| line.charAt(n + 1) == 'چ'
										|| line.charAt(n + 1) == 'خ'
										|| line.charAt(n + 1) == 'غ'
										|| line.charAt(n + 1) == 'ق'
										|| line.charAt(n + 1) == 'ف'
										|| line.charAt(n + 1) == 'ھ'
										|| line.charAt(n + 1) == 'ك'
										|| line.charAt(n + 1) == 'گ' || line
										.charAt(n + 1) == 'ڭ'))) {
							line = line.substring(0, n + 1) + kashida
									+ line.substring(n + 1);
							n += 2;
							break;
						}
						n++;
						if (n > line.length() - 2) {
							if (k == 0) {
								break;
							} else {
								n = 0;
							}
						}
					}


				}
				int x = currentPage.getPageLines().get(l).getXAxis();
				int h = currentPage.getPageLines().get(l).getYAxis();
				canvas.drawText(ArabicReshape.reshape_reverse(line), 0, ArabicReshape.reshape_reverse(line).length(), x, h, paint);
			}
			
			canvas.drawText((crp+1)+"/"+chapter.getPageNum(), display.getWidth()/2, display.getHeight()-3*margin/2, paint);
		
//		Drawable draw = new BitmapDrawable(newb);
		return newb;
//		mImage.setImageDrawable(draw);
	}

	public int getFontHeight(Paint mPaint) {
		FontMetrics fm = mPaint.getFontMetrics();
		return (int) Math.ceil(fm.descent - fm.top) + 2;
	}

	public float getStringLength(Paint mPaint, String text) {

		return mPaint.measureText(ArabicUtilities.reshape(text));

	}

	public float getKashidaLength(Paint mPaint, String text) {

		return mPaint.measureText(text);

	}
	
	private void makeToast(int i){
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.toast,null);

		TextView text = (TextView) layout.findViewById(R.id.toast);
		text.setTypeface(Properties.UIFont);
		if (i==0){
			text.setText(ArabicUtilities.reshape(getString(R.string.file_has_err)));
		}else if(i==1){
			text.setText(ArabicUtilities.reshape(getString(R.string.chapter_has_err)));
		}
		Toast toast = new Toast(this);
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		toast.show();
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 0, "").setIcon(R.drawable.list);
		menu.add(0, 1, 0, "").setIcon(R.drawable.setting);
		menu.add(0, 2, 0, "").setIcon(R.drawable.file_open);
		menu.add(0, 3, 0, "").setIcon(R.drawable.about);
//		menu.add(0,4,0,"Trih");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			TableOfContent();
			break;
		case 1:
			Set();
			break;
		case 2:
			OpenBook();
			break;
		case 3:
			AboutUyghurDev();
			break;
		case 4:
			RecentOpened();
			break;

		}

		return false;

	}

	private void RecentOpened() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(ShowBook.this, RecentBooks.class);
		startActivity(intent);
	}

	private void OpenBook() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(ShowBook.this, EpubActivity.class);
		startActivity(intent);
		finish();
	}

	private void AboutUyghurDev() {
		// TODO Auto-generated method stub
		LayoutInflater inflater = LayoutInflater.from(this);
		final View textEntryView = inflater.inflate(R.layout.aboutdialog, null);
		final TextView title = (TextView) textEntryView
				.findViewById(R.id.about_title);
		final TextView txtlink = (TextView) textEntryView
				.findViewById(R.id.content);
		title.setTypeface(Properties.UIFont);
		txtlink.setTypeface(Properties.UIFont);
		title.setText(ArabicUtilities.reshape(getString(R.string.app_name)));
		txtlink.setText(ArabicUtilities
				.reshape(getString(R.string.About_content)));
		final AlertDialog.Builder builder = new AlertDialog.Builder(ShowBook.this);
		builder.setCancelable(false);

		builder.setView(textEntryView);
		builder.setNegativeButton(ArabicUtilities.reshape("OK"),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				});
		builder.show();
	}

	private void Set() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(ShowBook.this, Settings.class);
		startActivity(intent);
		finish();
	}

	private void TableOfContent() {
		// TODO Auto-generated method stub
		List<PlayOrder> allorder = ncx.getAllOrder();
		String[] chapters = new String[allorder.size()];
		for (int i = 0; i < allorder.size(); i++) {
			chapters[i] = allorder.get(i).getChapterName();
		}

		Intent intent = new Intent(ShowBook.this, TableOfContent.class);
		intent.putExtra("chaptitles", chapters);
		startActivity(intent);
	}

}
