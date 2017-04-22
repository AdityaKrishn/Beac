package com.unotrack.device.demo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by aditya on 30/3/17.
 */

public class DatabaseHandler extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "StoredData";

    private static final String TABLE_CONTACTS = "Notes";

// Contacts Table Columns names

    private static final String KEY_key = "key";
    private static final String KEY_value = "value";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_key + " TEXT,"
                + KEY_value + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        // Create tables again
        onCreate(db);
    }

    /**
          * All CRUD(Create, Read, Update, Delete) Operations
          */
    // Adding new Entry
    void addEntry(StoredData dt) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_key, dt.getkey()); // Contact Name
        values.put(KEY_value, dt.getvalue()); // Contact Phone

        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single Entry
    StoredData getEntry(String k) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[]
                        { KEY_key, KEY_value }, KEY_key + "=?",
                new String[] { k }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        StoredData entry = new StoredData(cursor.getString(0), cursor.getString(1));
        // return contact
        return entry;
    }


    // Deleting single Entry
    public void deleteEntry(StoredData dt)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_key + " = ?",new String[] { dt.getkey() });
        db.close();
    }
}

