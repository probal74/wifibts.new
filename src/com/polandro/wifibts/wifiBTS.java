package com.polandro.wifibts;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;


public class wifiBTS extends TabActivity {
	final static String VERSION = "0.1";
	static final int DIALOG_ABOUT = 1;	
	public static final String FIRST_LOAD = "FirstLoad";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
                
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());        
                
        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Reusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab
        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, Locations.class);        
        spec = tabHost.newTabSpec("Locations").setIndicator("Locations", 
        		res.getDrawable(android.R.drawable.ic_menu_mylocation)).setContent(intent);        
        tabHost.addTab(spec);
        
        // Do the same for the other tabs
        intent = new Intent().setClass(this, Logs.class);
        
        spec = tabHost.newTabSpec("Logs").setIndicator("Logs", 
        		res.getDrawable(android.R.drawable.ic_menu_recent_history)).setContent(intent);        
        tabHost.addTab(spec);
        
        boolean FirstLoad = settings.getBoolean(FIRST_LOAD, true);
        if (FirstLoad) {
                showDialog(DIALOG_ABOUT);
                triggerNotification(1,"WifiBTS", "First start of the application");
        }
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
        case R.id.preferences:
        	Intent settingsActivity = new Intent(getBaseContext(), Preferences.class);
        	startActivity(settingsActivity);
        	return true;
        case R.id.startservice:
        	startService(new Intent(wifiBTS.this, WifiBTSService.class));
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
                    	 SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                         SharedPreferences.Editor editor = settings.edit();
                         editor.putBoolean(FIRST_LOAD, false);
                         editor.commit();
                     }
            	 });            	 
            	 return dialog;
            }
            return null;
    }
    
    protected void triggerNotification(int id, String Title, String Message) {
    	//Get a reference to the NotificationManager
    	String ns = Context.NOTIFICATION_SERVICE;
    	NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
    	
    	//Instantiate the Notification
    	int icon = R.drawable.icon;
    	CharSequence tickerText = Title;
    	long when = System.currentTimeMillis();
    	
    	Notification notification = new Notification(icon, tickerText, when);
    	
    	//Define the Notification's expanded message and Intent
    	Context context = getApplicationContext();
    	CharSequence contentTitle = Title;
    	CharSequence contentText = Message;
    	Intent notificationIntent = new Intent(this, wifiBTS.class);
    	PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

    	notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
    	notification.flags |= Notification.FLAG_AUTO_CANCEL;
    	
    	//Pass the Notification to the NotificationManager
    	mNotificationManager.notify(id, notification);
    }    
}