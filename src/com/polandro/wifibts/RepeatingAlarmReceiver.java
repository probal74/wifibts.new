package com.polandro.wifibts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;


public class RepeatingAlarmReceiver extends BroadcastReceiver {

	private WifiManager wifiMgr;

	@Override
	public void onReceive(Context context, Intent intent) {		
		wifiMgr = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		if (intent.getExtras().getBoolean("ToggleWifi")) {
			wifiMgr.setWifiEnabled(true);			
		} else {
			wifiMgr.setWifiEnabled(false);			
		}
	}

}
