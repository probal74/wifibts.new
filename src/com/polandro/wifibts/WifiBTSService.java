package com.polandro.wifibts;

import java.util.Calendar;
import java.util.Vector;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

public class WifiBTSService extends Service {

	private DBAdapter wifiBTSdb;
	private Vector<Integer> Cells;
	private TelephonyManager telMgr;
    private WifiManager wifiMgr;
    private GsmCellLocation GCL;
    private PhoneStateListener listener;
    private AlarmManager alarmManager;    
    
    @Override
    public void onCreate() {
            super.onCreate();
            startservice();
    }

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void startservice() {
		wifiBTSdb = new DBAdapter(this);
        wifiBTSdb.open_rw();
        Cells = wifiBTSdb.getAllCells();        
        wifiBTSdb.close();
        wifiBTSdb = null;
        
        wifiMgr = (WifiManager)getSystemService(Context.WIFI_SERVICE);                                  //wifiMgr - connect to the system wifi service
        telMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);                 //telMgr - connect to the system telephony service			
				
		listener = new PhoneStateListener() {                                                                                   //Listener for events from telephony manager
            public void onCellLocationChanged(CellLocation location) {                          //GsmCellLocation changed event
            	
            	if (telMgr.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
            		GCL = (GsmCellLocation)telMgr.getCellLocation();
            	}
                
                if (GCL != null) {             //If current location is available
                	Log.v("WifiBTS", "listener activated");
                	if (isCellhere(GCL.getCid())) {
                		// włącz
                		Log.v("WifiBTS", "listener wifi on");
                		if (Settings.System.getInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 0) {
                			Log.v("WifiBTS", "listener not in the airplane mode");
                			if( ! wifiMgr.isWifiEnabled() ) {
                				Log.v("WifiBTS", "listener wifi not enabled, so enable");
                				wifiMgr.setWifiEnabled(true);
                                triggerNotification(1,"WifiBTS", "Wifi enabled @"+ Calendar.getInstance().getTime().toLocaleString() );
                			}
                		}
                	}
                	else {
                		//wyłącz
                		Log.v("WifiBTS", "listener wifi off");
                		if( wifiMgr.isWifiEnabled() ) {
                			Log.v("WifiBTS", "listener wifi enabled, so disable");
                			wifiMgr.setWifiEnabled(false);
                			triggerNotification(1,"WifiBTS", "Wifi disabled @"+ Calendar.getInstance().getTime().toLocaleString() );
                        }
                	}
                }                
                else {
                	triggerNotification(1,"WifiBTS", "Not a GSM phone type");
                }
            }
		};
		telMgr.listen(listener, PhoneStateListener.LISTEN_CELL_LOCATION);   //Register the listener with the telephony manager
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());		
		if (settings.getBoolean("NoDataMode", false)) {		
			Calendar NoDataModeOn = Calendar.getInstance();
			NoDataModeOn.set(Calendar.HOUR_OF_DAY, Integer.valueOf(settings.getString("NoDataModeOn", "00:00").split(":")[0]));
			NoDataModeOn.set(Calendar.MINUTE, Integer.valueOf(settings.getString("NoDataModeOn", "00:00").split(":")[1]));
			NoDataModeOn.set(Calendar.SECOND, 0);
			
			Calendar NoDataModeOff = Calendar.getInstance();		
			NoDataModeOff.set(Calendar.HOUR_OF_DAY, Integer.valueOf(settings.getString("NoDataModeOff", "00:00").split(":")[0]));
			NoDataModeOff.set(Calendar.MINUTE, Integer.valueOf(settings.getString("NoDataModeOff", "00:00").split(":")[1]));
			NoDataModeOff.set(Calendar.SECOND, 0);
			
	    	Intent wifi_on = new Intent(this, RepeatingAlarmReceiver.class);	    	
	    	wifi_on.putExtra("ToggleWifi", true);
	    	PendingIntent pending_wifi_on = PendingIntent.getBroadcast(getApplicationContext(), 0, wifi_on, PendingIntent.FLAG_CANCEL_CURRENT);
		    	    	
	    	Intent wifi_off = new Intent(this, RepeatingAlarmReceiver.class);	    	
	    	wifi_off.putExtra("ToggleWifi", false);
	    	PendingIntent pending_wifi_off = PendingIntent.getBroadcast(getApplicationContext(), 1, wifi_off, PendingIntent.FLAG_CANCEL_CURRENT);        
       
	    	alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
	    	alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, NoDataModeOff.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pending_wifi_on); // every 24h
	    	alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, NoDataModeOn.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pending_wifi_off);
		}
	}
	
/*	@Override
	public void onDestroy() {
		alarmManager.cancel(pending_wifi_on);
		alarmManager.cancel(pending_wifi_off);
		super.onDestroy();
	}*/
	
	/*private static String pad(int c) {
	    if (c >= 10)
	        return String.valueOf(c);
	    else
	        return "0" + String.valueOf(c);
	}*/
	
	public boolean isCellhere(int cell){
        int i=0;
        for(i=0;i<Cells.size();i++){
                if(Cells.elementAt(i) == cell){
                        return true;
                }
        }
        return false;
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
