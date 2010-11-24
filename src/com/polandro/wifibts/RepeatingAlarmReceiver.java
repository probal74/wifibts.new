package com.polandro.wifibts;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.util.Log;


public class RepeatingAlarmReceiver extends BroadcastReceiver {
	private WifiManager wifiMgr;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v("WifiBTS", "broadcase receive");

		 wifiMgr = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		 if (intent.getExtras().getBoolean("ToggleWifi")) {
			Log.v("WifiBTS", "Alarmreceiver wifi on");
			wifiMgr.setWifiEnabled(true);
			renameDefaultAPN(context, true);
		 } else {
			Log.v("WifiBTS", "Alarmreceiver wifi off");
			wifiMgr.setWifiEnabled(false);
			renameDefaultAPN(context, false);
		 }

	}
	
	public boolean renameDefaultAPN(Context context, boolean toggle){
		if (toggle == true) {
			//przywróć nazwę
			
			ContentValues updateValues = new ContentValues();
	        updateValues.put("apn", "internet"); 
			
			int result = context.getContentResolver().update(Uri.parse("content://telephony/carriers"), updateValues, "current=1 and upper(name) not like '%MMS%'", null);
			Log.v("WifiBTS", "APN's renamed "+ result);
			
			return true;
		} else {		
			
			
			
			/*Cursor APNs = context.getContentResolver().query(Uri.parse("content://telephony/carriers"), new String[] {"name"}, "current=1 and upper(name) not like '%MMS%'", null, null);
			if (APNs!=null) {
			try {
			if (APNs.moveToFirst()) {
			String name = APNs.getString(0);		
			Log.v("WifiBTS",name);
			}
			}
			finally {
			APNs.close();
			}
			}*/
			
			//zmień nazwę
			ContentValues updateValues = new ContentValues();
	        updateValues.put("apn", "internet2"); 
			
			int result = context.getContentResolver().update(Uri.parse("content://telephony/carriers"), updateValues, "current=1 and upper(name) not like '%MMS%'", null);
			Log.v("WifiBTS", "APN's renamed "+ result);
			return false;
		}
	}

}
