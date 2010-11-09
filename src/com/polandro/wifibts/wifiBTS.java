package com.polandro.wifibts;

import android.app.Activity;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;

public class wifiBTS extends TabActivity {
	final static String VERSION = "0.1";
	static final int DIALOG_ABOUT = 1;
	public static final String PREFS_NAME = "WifiBTSPrefs";
	public static final String FIRST_LOAD = "FirstLoad";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        
        boolean FirstLoad = settings.getBoolean(FIRST_LOAD, true);
        if (FirstLoad) {
                showDialog(DIALOG_ABOUT);
        }
        
/*        TabHost tabHost = getTabHost(); // The activity TabHost
        TabHost.TabSpec spec; // Resusable TabSpec for each tab

        TabContentFactory factory = new MyTabFactory();

        spec = tabHost.newTabSpec("All").setIndicator("All");
        spec.setContent(factory);
        tabHost.addTab(spec);*/
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {                
        case R.id.about:
        	showDialog(DIALOG_ABOUT);
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
            final Dialog dialog;
            switch (id) {
             case DIALOG_ABOUT:
            	 dialog = new Dialog(this);
            	 dialog.setContentView(R.layout.aboutdialog);
            	 dialog.setTitle("WifiBTS About");
            	 Button dismiss = (Button) dialog.findViewById(R.id.dismiss);
            	 dismiss.setOnClickListener(new View.OnClickListener() {                     
                     public void onClick(View arg0) {
                             dismissDialog(DIALOG_ABOUT);
                     }
            	 });
            	 dialog.setOnDismissListener(new OnDismissListener() {
                     public void onDismiss(DialogInterface dialog) {
                    	 SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                         SharedPreferences.Editor editor = settings.edit();
                         editor.putBoolean(FIRST_LOAD, false);
                         editor.commit();
                     }
            	 });            	 
            	 return dialog;
            }
            return null;
    }
    
}