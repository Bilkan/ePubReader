package net.uyghurdev.uyghurepubreader.re;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TableOfContent extends Activity {

	BookmarkData bookmark;
	int bookid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listforchoose);
		setTitle("");
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		bookmark=new BookmarkData(this);
		bookid=Properties.BookId;
		String[] chapters= getIntent().getStringArrayExtra("chaptitles");

		TOCAdapter adapter=new TOCAdapter(this, chapters);
		ListView lv=(ListView)findViewById(R.id.optionlist);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				// TODO Auto-generated method stub
				bookmark.setBookmark(bookid, arg2, 1, 100);
				Properties.CurrentOrder=arg2;
				Intent intent = new Intent(TableOfContent.this, ShowBook.class);
				startActivity(intent);
				finish();
			}
			
		});
	}

}
