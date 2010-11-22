package com.polandro.wifibts;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
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

public class WifiBTSService extends Service {

	private DBAdapter wifiBTSdb;
	private Vector<Integer> Cells;
	private TelephonyManager telMgr;
    private WifiManager wifiMgr;
    private GsmCellLocation GCL;
    private PhoneStateListener listener;
    private Timer NoDataModeON = new Timer();
    private Timer NoDataModeOFF = new Timer();
    
	public WifiBTSService() {				
	}
	
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
        Date NoNetworkModeOn = new Date();
        Date NoNetworkModeOff = new Date();
        
        wifiMgr = (WifiManager)getSystemService(Context.WIFI_SERVICE);                                  //wifiMgr - connect to the system wifi service
        telMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);                 //telMgr - connect to the system telephony service			
				
		listener = new PhoneStateListener() {                                                                                   //Listener for events from telephony manager
            public void onCellLocationChanged(CellLocation location) {                          //GsmCellLocation changed event
                GCL = (GsmCellLocation)telMgr.getCellLocation();
                if (GCL != null) {             //If current location is available
                	if (isCellhere(GCL.getCid())) {
                		// włącz
                		if (Settings.System.getInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1) {
                			if( ! wifiMgr.isWifiEnabled() ) {
                				wifiMgr.setWifiEnabled(true);
                                triggerNotification(1,"WifiBTS", "Wifi enabled");
                			}
                		}
                	}
                	else {
                		//wyłącz
                		wifiMgr.setWifiEnabled(false);
                        triggerNotification(1,"WifiBTS", "Wifi disabled");

                	}
                }
            }
		};
		telMgr.listen(listener, PhoneStateListener.LISTEN_CELL_LOCATION);   //Register the listener with the telephony manager
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());		

		NoNetworkModeOn.setHours(Integer.parseInt(settings.getString("NoNetworkModeOn", "00:00").split(":")[0]));		
		NoNetworkModeOn.setMinutes(Integer.parseInt(settings.getString("NoNetworkModeOn", "00:00").split(":")[1]));
		
		NoNetworkModeOff.setHours(Integer.parseInt(settings.getString("NoNetworkModeOff", "00:00").split(":")[0]));		
		NoNetworkModeOff.setMinutes(Integer.parseInt(settings.getString("NoNetworkModeOff", "00:00").split(":")[1]));
		
		if (settings.getBoolean("NoNetworkMode", false)) {
		
		NoDataModeOFF.schedule( new TimerTask() {  //register switch-on on the time from the preferences
			public void run() {
				// włącz
        		if (Settings.System.getInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1) {
        			if( ! wifiMgr.isWifiEnabled() ) {
        				wifiMgr.setWifiEnabled(true);
        			}
        		}
			}
			}, NoNetworkModeOff);
		
		NoDataModeON.schedule( new TimerTask() {  //register switch-on on the time from the preferences
			public void run() {
				wifiMgr.setWifiEnabled(false);        		
			}
			}, NoNetworkModeOn);
			
		}
	}
	
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
