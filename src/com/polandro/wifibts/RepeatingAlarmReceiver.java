package com.polandro.wifibts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
		 } else {
			Log.v("WifiBTS", "Alarmreceiver wifi off");
			wifiMgr.setWifiEnabled(false);			
		 }

	}

}
