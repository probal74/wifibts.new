package com.polandro.wifibts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {
        public static final String KEY_LOC_ROWID = "_id";
        public static final String KEY_LOC_NAME = "name";

        private static final String DATABASE_NAME = "wifibts";
        private static final String DATABASE_TABLE_LOC = "locations";        
        private static final int DATABASE_VERSION = 1;

        private static final String DATABASE_CREATE_TABLE_LOC = "create table " + DATABASE_TABLE_LOC 
        + "(_id integer primary key autoincrement, name text not null);";
        
        private final Context context;

        private DatabaseHelper DBHelper;
        private SQLiteDatabase db;

        //Constructor
        public DBAdapter(Context ctx) {
                this.context = ctx;
                DBHelper = new DatabaseHelper(context);
        }

        // ---opens the database for RW---
        public DBAdapter open_rw() throws SQLException {
                db = DBHelper.getWritableDatabase();
                return this;
        }

        // ---closes the database---
        public void close() {
                DBHelper.close();
        }

        private static class DatabaseHelper extends SQLiteOpenHelper {
                DatabaseHelper(Context context) {
                        super(context, DATABASE_NAME, null, DATABASE_VERSION);
                }

                @Override
                public void onCreate(SQLiteDatabase db) {
                        db.execSQL(DATABASE_CREATE_TABLE_LOC);                        
                }

                @Override
                public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                        db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE_LOC);                        
                        onCreate(db);
                }
        }

        // ---inserts a location into the database---
        public long addLoc(String location) {
                ContentValues initialValues = new ContentValues();
                initialValues.put(KEY_LOC_NAME, location);                
                return db.insert(DATABASE_TABLE_LOC, null, initialValues);
        }

        // ---deletes location from the database---
        public boolean delLoc(int _id) {
                return db.delete(DATABASE_TABLE_LOC, KEY_LOC_ROWID + "=" + _id, null) > 0;
        }
        
        //---retrieves all locations---
        public Cursor getLoc() {        	
        	return db.query(DATABASE_TABLE_LOC, new String[] {KEY_LOC_ROWID, KEY_LOC_NAME}, null, null, null, null, null);                        
        }
    
   /* //---checks if cid is already in db---
    public boolean checkCID(int cid){
        if (db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_CID, KEY_SSID}, KEY_CID + "=" + cid, null, null, null, null).getCount() > 0)
                return true;
        else
                return false;
    }*/  
}

