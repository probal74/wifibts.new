package com.polandro.wifibts;

import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {
        public static final String KEY_LOC_ROWID = "_id";
        public static final String KEY_LOC_NAME = "name";
        public static final String KEY_CELL_ROWID = "_id";
        public static final String KEY_CELL_LAC = "lac";
        public static final String KEY_CELL_CELLID = "cellid";
        public static final String KEY_CELL_LOCATION = "location";

        private static final String DATABASE_NAME = "wifibts";
        private static final String DATABASE_TABLE_LOC = "locations";
        private static final String DATABASE_TABLE_CELL = "cells";        
        private static final int DATABASE_VERSION = 4;

        private static final String DATABASE_CREATE_TABLE_LOC = "create table " + DATABASE_TABLE_LOC 
        + "(_id integer primary key autoincrement, name text not null);";
        
        private static final String DATABASE_CREATE_TABLE_CELL = "create table " + DATABASE_TABLE_CELL +
        		"(_id integer primary key autoincrement," +
        		//"location REFERENCES locations(_id), " +
        		"location integer, " +
        		"lac integer," +
        		"cellid integer," +        		
        		"UNIQUE(lac, cellid));";
        
        
        private final Context context;

        private DatabaseHelper DBHelper;
        private SQLiteDatabase db;
        Vector<Integer> Cells;

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
                        db.execSQL(DATABASE_CREATE_TABLE_CELL);
                }

                @Override
                public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                        db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE_LOC);
                        db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE_CELL);
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
        // ---renames location---
        public long renameLoc(String _id, String value) {
        	ContentValues updateValues = new ContentValues();
            updateValues.put(KEY_LOC_NAME, value); 
            return db.update(DATABASE_TABLE_LOC, updateValues, KEY_LOC_ROWID+"="+_id, null);            
        }        
        // ---retrieves all locations---
        public Cursor getLoc() {        	
        	return db.query(DATABASE_TABLE_LOC, new String[] {KEY_LOC_ROWID, KEY_LOC_NAME}, null, null, null, null, null);                        
        }
        // ---retrieves all cells for an location---
        public Cursor getCells(String location) {        	
        	return db.query(DATABASE_TABLE_CELL, new String[] {KEY_CELL_ROWID, KEY_CELL_LOCATION, KEY_CELL_LAC, KEY_CELL_CELLID}, KEY_CELL_LOCATION + "=" + location , null, null, null, null);                        
        }
        // ---retrieves all cells for an location---
        public Vector<Integer> getAllCells() {        	 
        	 Cursor c = db.query(DATABASE_TABLE_CELL, new String[] {KEY_CELL_CELLID}, null, null, null, null, null);
        	 Cells = new Vector<Integer>();
        	 if (c.moveToFirst())
             {
                 do {
                     Cells.add(c.getInt(0));
                 } while (c.moveToNext());
             }
        	 return Cells;
        }
        // ---inserts a lac and cellid into the locations table---
        public long addCells(String location_id, int lac, int cellid) {
                ContentValues initialValues = new ContentValues();
                initialValues.put(KEY_CELL_LOCATION, location_id);
                initialValues.put(KEY_CELL_LAC, lac);
                initialValues.put(KEY_CELL_CELLID, cellid);
                return db.insert(DATABASE_TABLE_CELL, null, initialValues);
        }
        // ---deletes Cell from the database---
        public boolean delCell(int _id) {
                return db.delete(DATABASE_TABLE_CELL, KEY_CELL_ROWID + "=" + _id, null) > 0;
        }
}

