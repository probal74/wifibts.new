package com.polandro.wifibts;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
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
    private Timer timer = new Timer();
	
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
                			}
                		}
                	}
                	else {
                		//wyłącz
                		wifiMgr.setWifiEnabled(false);                		
                	}
                }
            }
		};
		telMgr.listen(listener, PhoneStateListener.LISTEN_CELL_LOCATION);   //Register the listener with the telephony manager
		
		timer.schedule( new TimerTask() {  //register switch-on on the time from the preferences
			public void run() {
				// włącz
        		if (Settings.System.getInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1) {
        			if( ! wifiMgr.isWifiEnabled() ) {
        				wifiMgr.setWifiEnabled(true);
        			}
        		}
			}
			}, 0);
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


}
