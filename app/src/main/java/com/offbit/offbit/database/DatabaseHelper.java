package com.offbit.offbit.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.offbit.offbit.model.CallRecord;
import com.offbit.offbit.model.Contact;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "offbit.db";
    private static final int DATABASE_VERSION = 1;
    
    // Table names
    private static final String TABLE_CALL_RECORDS = "call_records";
    private static final String TABLE_CONTACTS = "contacts";
    
    // Column names for call records
    private static final String KEY_CALL_ID = "call_id";
    private static final String KEY_PEER_ID = "peer_id";
    private static final String KEY_PEER_NAME = "peer_name";
    private static final String KEY_START_TIME = "start_time";
    private static final String KEY_END_TIME = "end_time";
    private static final String KEY_CALL_TYPE = "call_type";
    private static final String KEY_CALL_STATUS = "call_status";
    private static final String KEY_DURATION = "duration";
    
    // Column names for contacts
    private static final String KEY_CONTACT_ID = "contact_id";
    private static final String KEY_DISPLAY_NAME = "display_name";
    private static final String KEY_PUBLIC_KEY = "public_key";
    private static final String KEY_LAST_SEEN = "last_seen";
    private static final String KEY_IS_FAVORITE = "is_favorite";
    private static final String KEY_ADDED_DATE = "added_date";
    
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create call records table
        String CREATE_CALL_RECORDS_TABLE = "CREATE TABLE " + TABLE_CALL_RECORDS + "("
                + KEY_CALL_ID + " TEXT PRIMARY KEY,"
                + KEY_PEER_ID + " TEXT,"
                + KEY_PEER_NAME + " TEXT,"
                + KEY_START_TIME + " INTEGER,"
                + KEY_END_TIME + " INTEGER,"
                + KEY_CALL_TYPE + " TEXT,"
                + KEY_CALL_STATUS + " TEXT,"
                + KEY_DURATION + " INTEGER" + ")";
        db.execSQL(CREATE_CALL_RECORDS_TABLE);
        
        // Create contacts table
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_CONTACT_ID + " TEXT PRIMARY KEY,"
                + KEY_PEER_ID + " TEXT UNIQUE,"
                + KEY_DISPLAY_NAME + " TEXT,"
                + KEY_PUBLIC_KEY + " TEXT,"
                + KEY_LAST_SEEN + " INTEGER,"
                + KEY_IS_FAVORITE + " INTEGER,"
                + KEY_ADDED_DATE + " INTEGER" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
        
        Log.d(TAG, "Database tables created");
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALL_RECORDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        
        // Create tables again
        onCreate(db);
    }
    
    /**
     * Add a call record
     */
    public void addCallRecord(CallRecord callRecord) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_CALL_ID, callRecord.getCallId());
        values.put(KEY_PEER_ID, callRecord.getPeerId());
        values.put(KEY_PEER_NAME, callRecord.getPeerName());
        values.put(KEY_START_TIME, callRecord.getStartTime().getTime());
        values.put(KEY_END_TIME, callRecord.getEndTime() != null ? callRecord.getEndTime().getTime() : 0);
        values.put(KEY_CALL_TYPE, callRecord.getCallType().name());
        values.put(KEY_CALL_STATUS, callRecord.getCallStatus().name());
        values.put(KEY_DURATION, callRecord.getDuration());
        
        // Inserting Row
        db.insert(TABLE_CALL_RECORDS, null, values);
        db.close();
        
        Log.d(TAG, "Call record added: " + callRecord.getCallId());
    }
    
    /**
     * Get all call records
     */
    public List<CallRecord> getAllCallRecords() {
        List<CallRecord> callRecordList = new ArrayList<>();
        
        String selectQuery = "SELECT  * FROM " + TABLE_CALL_RECORDS + " ORDER BY " + KEY_START_TIME + " DESC";
        
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if (cursor.moveToFirst()) {
            do {
                CallRecord callRecord = new CallRecord();
                callRecord.setCallId(cursor.getString(0));
                callRecord.setPeerId(cursor.getString(1));
                callRecord.setPeerName(cursor.getString(2));
                
                long startTimeMillis = cursor.getLong(3);
                callRecord.setStartTime(new Date(startTimeMillis));
                
                long endTimeMillis = cursor.getLong(4);
                if (endTimeMillis > 0) {
                    callRecord.setEndTime(new Date(endTimeMillis));
                }
                
                callRecord.setCallType(CallRecord.CallType.valueOf(cursor.getString(5)));
                callRecord.setCallStatus(CallRecord.CallStatus.valueOf(cursor.getString(6)));
                callRecord.setDuration(cursor.getLong(7));
                
                callRecordList.add(callRecord);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        
        return callRecordList;
    }
    
    /**
     * Get recent call records
     */
    public List<CallRecord> getRecentCallRecords(int limit) {
        List<CallRecord> callRecordList = new ArrayList<>();
        
        String selectQuery = "SELECT  * FROM " + TABLE_CALL_RECORDS + " ORDER BY " + KEY_START_TIME + " DESC LIMIT " + limit;
        
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if (cursor.moveToFirst()) {
            do {
                CallRecord callRecord = new CallRecord();
                callRecord.setCallId(cursor.getString(0));
                callRecord.setPeerId(cursor.getString(1));
                callRecord.setPeerName(cursor.getString(2));
                
                long startTimeMillis = cursor.getLong(3);
                callRecord.setStartTime(new Date(startTimeMillis));
                
                long endTimeMillis = cursor.getLong(4);
                if (endTimeMillis > 0) {
                    callRecord.setEndTime(new Date(endTimeMillis));
                }
                
                callRecord.setCallType(CallRecord.CallType.valueOf(cursor.getString(5)));
                callRecord.setCallStatus(CallRecord.CallStatus.valueOf(cursor.getString(6)));
                callRecord.setDuration(cursor.getLong(7));
                
                callRecordList.add(callRecord);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        
        return callRecordList;
    }
    
    /**
     * Add a contact
     */
    public void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_CONTACT_ID, contact.getContactId());
        values.put(KEY_PEER_ID, contact.getPeerId());
        values.put(KEY_DISPLAY_NAME, contact.getDisplayName());
        values.put(KEY_PUBLIC_KEY, contact.getPublicKeyBase64());
        values.put(KEY_LAST_SEEN, contact.getLastSeen().getTime());
        values.put(KEY_IS_FAVORITE, contact.isFavorite() ? 1 : 0);
        values.put(KEY_ADDED_DATE, contact.getAddedDate().getTime());
        
        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        db.close();
        
        Log.d(TAG, "Contact added: " + contact.getDisplayName());
    }
    
    /**
     * Get all contacts
     */
    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<>();
        
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " ORDER BY " + KEY_DISPLAY_NAME;
        
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setContactId(cursor.getString(0));
                contact.setPeerId(cursor.getString(1));
                contact.setDisplayName(cursor.getString(2));
                contact.setPublicKeyBase64(cursor.getString(3));
                
                long lastSeenMillis = cursor.getLong(4);
                contact.setLastSeen(new Date(lastSeenMillis));
                
                contact.setFavorite(cursor.getInt(5) == 1);
                
                long addedDateMillis = cursor.getLong(6);
                contact.setAddedDate(new Date(addedDateMillis));
                
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        
        return contactList;
    }
    
    /**
     * Get favorite contacts
     */
    public List<Contact> getFavoriteContacts() {
        List<Contact> contactList = new ArrayList<>();
        
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " WHERE " + KEY_IS_FAVORITE + " = 1 ORDER BY " + KEY_DISPLAY_NAME;
        
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setContactId(cursor.getString(0));
                contact.setPeerId(cursor.getString(1));
                contact.setDisplayName(cursor.getString(2));
                contact.setPublicKeyBase64(cursor.getString(3));
                
                long lastSeenMillis = cursor.getLong(4);
                contact.setLastSeen(new Date(lastSeenMillis));
                
                contact.setFavorite(cursor.getInt(5) == 1);
                
                long addedDateMillis = cursor.getLong(6);
                contact.setAddedDate(new Date(addedDateMillis));
                
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        
        return contactList;
    }
    
    /**
     * Update contact favorite status
     */
    public void updateContactFavorite(String contactId, boolean isFavorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_IS_FAVORITE, isFavorite ? 1 : 0);
        
        db.update(TABLE_CONTACTS, values, KEY_CONTACT_ID + " = ?", new String[]{contactId});
        db.close();
        
        Log.d(TAG, "Contact favorite status updated: " + contactId + ", favorite: " + isFavorite);
    }
    
    /**
     * Delete a contact
     */
    public void deleteContact(String contactId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_CONTACT_ID + " = ?", new String[]{contactId});
        db.close();
        
        Log.d(TAG, "Contact deleted: " + contactId);
    }
}
