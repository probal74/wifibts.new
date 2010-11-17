package com.polandro.wifibts;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class Locations extends ListActivity {

	private DBAdapter wifiBTSdb;
	private Cursor c;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locations);
       
        openDB();
        c = wifiBTSdb.getLoc();
        startManagingCursor(c);
        
        ListAdapter adapter = new SimpleCursorAdapter(this, 
                // Use a template that displays a text view
                android.R.layout.simple_list_item_1, 
                // Give the cursor to the list adatper
                c, 
                // Map the NAME column in the people database to...
                new String[] {"locations.name"} ,
                // The "text1" view defined in the XML template
                new int[] {android.R.id.text1}); 
        
        setListAdapter(adapter);
        registerForContextMenu(getListView());
        final Context ctx = getApplicationContext();
        
		final Button button = (Button) findViewById(R.id.AddLocButton);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent foo = new Intent(ctx, TextEntryActivity.class);
				foo.putExtra("value", "");
				startActivityForResult(foo, 1);				
			}
		});
   	}
	
	public void onListItemClick(ListView parent, View v, int position, long id) {   
		Cursor c = (Cursor)getListAdapter().getItem(position);
		Toast.makeText(this, "You have selected " + c.getString(1), Toast.LENGTH_SHORT).show();
	}	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	  super.onCreateContextMenu(menu, v, menuInfo);
	  MenuInflater inflater = getMenuInflater();
	  inflater.inflate(R.menu.locations_context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	  Cursor c = (Cursor)getListAdapter().getItem(info.position);
	  switch (item.getItemId()) {
	  case R.id.loc_remove:
		if (wifiBTSdb.delLoc(c.getInt(0))) {
			Toast.makeText(this, "Location " + c.getString(1) + " removed", Toast.LENGTH_SHORT).show();
			c.requery();
		}		
	    return true;	
	  case R.id.loc_rename:		
		Toast.makeText(this, "You have selected to Rename " + c.getString(1), Toast.LENGTH_SHORT).show();	    
	    return true;	    
	  default:
	    return super.onContextItemSelected(item);
	  }
	}
	
	private void openDB() {
		wifiBTSdb = new DBAdapter(this);
        wifiBTSdb.open_rw();
	}
	
	public void onDestroy() {
        wifiBTSdb.close();         
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                try {
                    String value = data.getStringExtra("value");
                    if (value != null && value.length() > 0) {                    	
                    	if (wifiBTSdb.addLoc(value) > 0){
                    		Toast.makeText(this, "Location " + value + " added", Toast.LENGTH_SHORT).show();
                    		c.requery();                    	
                    	}
                    }
                } catch (Exception e) {
                }
                break;
            default:
                break;
        }
    }
}
