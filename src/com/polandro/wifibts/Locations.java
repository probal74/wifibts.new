package com.polandro.wifibts;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Locations extends ListActivity {

	String[] lv_arr={"Android","iPhone","BlackBerry","AndroidPeople","2","3","4","5","6","7"};
	 
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locations);        
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , lv_arr));
        registerForContextMenu(getListView());        
   	}
	
	public void onListItemClick(ListView parent, View v, int position, long id) {   
		Toast.makeText(this, "You have selected " + lv_arr[position], Toast.LENGTH_SHORT).show();
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
	  switch (item.getItemId()) {
	  case R.id.loc_remove:
		Toast.makeText(this, "You have selected to Remove " + lv_arr[(int) info.id], Toast.LENGTH_SHORT).show();	    
	    return true;	
	  case R.id.loc_rename:
		Toast.makeText(this, "You have selected to Rename " + lv_arr[(int) info.id], Toast.LENGTH_SHORT).show();	    
	    return true;	    
	  default:
	    return super.onContextItemSelected(item);
	  }
	}
	
}
