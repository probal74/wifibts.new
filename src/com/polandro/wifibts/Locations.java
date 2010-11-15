package com.polandro.wifibts;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Locations extends Activity {

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locations);        
   
        String lv_arr[]={"Android","iPhone","BlackBerry","AndroidPeople","2","3","4","5","6","7"};
        ListView locations=(ListView)findViewById(R.id.ListViewLocations);
        locations.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 , lv_arr));
	}
}
