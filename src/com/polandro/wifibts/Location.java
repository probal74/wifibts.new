package com.polandro.wifibts;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class Location extends ListActivity {

    private TelephonyManager telMgr;
    private GsmCellLocation GCL;
	private DBAdapter wifiBTSdb;
	private Cursor c;
	private String location_id;
    
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);
        
        openDB();
        location_id = getIntent().getExtras().getString("location");
        c = wifiBTSdb.getCells(location_id);
        startManagingCursor(c);
        
        ListAdapter adapter = new SimpleCursorAdapter(this, 
                // Use a template that displays a text view
                R.layout.location_list,                
                // Give the cursor to the list adatper
                c, 
                // Map the NAME column in the database to...
                new String[] {"lac","cellid"},
                // The "text1" view defined in the XML template
                new int[] {android.R.id.text1, android.R.id.text2}); 
        
        setListAdapter(adapter);
        registerForContextMenu(getListView());
        
        final Button button = (Button) findViewById(R.id.btnAdd);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				telMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);                 //telMgr - connect to the system telephony service				
				GCL = (GsmCellLocation)telMgr.getCellLocation();
				
				if (wifiBTSdb.addCells(location_id, GCL.getLac(), GCL.getCid()) > 0){            		
            		Toast.makeText(getApplicationContext(), "Cell added", Toast.LENGTH_SHORT).show();
            		c.requery();
            	}
				else {
            		Toast.makeText(getApplicationContext(), "Cell already exists in database", Toast.LENGTH_SHORT).show();		
				}
				
				
			}
		});
	}
	
	public void onListItemClick(ListView parent, View v, int position, long id) {
		try {
			displayMap(c.getInt(3), c.getInt(2));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	  super.onCreateContextMenu(menu, v, menuInfo);
	  MenuInflater inflater = getMenuInflater();
	  inflater.inflate(R.menu.location_context_menu, menu);
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  switch (item.getItemId()) {
	  case R.id.cell_remove:
		if (wifiBTSdb.delCell(c.getInt(0))) {
			Toast.makeText(this, "Location " + c.getString(2) + c.getString(3) + " removed", Toast.LENGTH_SHORT).show();
			c.requery();
		}		
	    return true;	  	    
	  default:
	    return super.onContextItemSelected(item);
	  }
	}
	
	private void openDB() {
		wifiBTSdb = new DBAdapter(this);
        wifiBTSdb.open_rw();
	}
	
	@Override
	public void onDestroy() {
        wifiBTSdb.close();
        super.onDestroy();
	}
	
	private boolean displayMap(int cellID, int lac) throws Exception 
    {
        String urlString = "http://www.google.com/glm/mmap";            
    
        //---open a connection to Google Maps API---
        URL url = new URL(urlString); 
        URLConnection conn = url.openConnection();
        HttpURLConnection httpConn = (HttpURLConnection) conn;        
        httpConn.setRequestMethod("POST");
        httpConn.setDoOutput(true); 
        httpConn.setDoInput(true);
        httpConn.connect(); 
        
        //---write some custom data to Google Maps API---
        OutputStream outputStream = httpConn.getOutputStream();
        WriteData(outputStream, cellID, lac);       
        
        //---get the response---
        InputStream inputStream = httpConn.getInputStream();  
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        
        //---interpret the response obtained---
        dataInputStream.readShort();
        dataInputStream.readByte();
        int code = dataInputStream.readInt();
        if (code == 0) {
            double lat = (double) dataInputStream.readInt() / 1000000D;
            double lng = (double) dataInputStream.readInt() / 1000000D;
            dataInputStream.readInt();
            dataInputStream.readInt();
            dataInputStream.readUTF();
            
            //---display Google Maps---
            String uriString = "geo:" + lat + "," + lng;
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uriString));
            startActivity(intent);
            return true;
        }
        else
        {        	
        	return false;
        }
    }  
	
	private void WriteData(OutputStream out, int cellID, int lac) throws IOException
    {    	
        DataOutputStream dataOutputStream = new DataOutputStream(out);
        dataOutputStream.writeShort(21);
        dataOutputStream.writeLong(0);
        dataOutputStream.writeUTF("en");
        dataOutputStream.writeUTF("Android");
        dataOutputStream.writeUTF("1.0");
        dataOutputStream.writeUTF("Web");
        dataOutputStream.writeByte(27);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(3);
        dataOutputStream.writeUTF("");

        dataOutputStream.writeInt(cellID);  
        dataOutputStream.writeInt(lac);     

        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.flush();    	
    }

}
