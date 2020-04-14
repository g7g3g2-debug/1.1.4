package com.kong.lutech.apartment.utils.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gimdonghyeog on 2017. 5. 25..
 */

public class DBManager {
    public static final class CreateDelivery {
        public static final String ID = "id";
        public static final String _TABLENAME = "delivery";
        public static final String _CREATE =
                "create table " + _TABLENAME + "("
                    + ID + " integer primary key autoincrement)";
    }

    public static final class CreateNotice {
        public static final String ID = "id";
        public static final String _TABLENAME = "pushNotice";
        public static final String _CREATE =
                "create table " + _TABLENAME + "("
                        + ID + " integer primary key)";
    }

    public static final class CreateReadNotice {
        public static final String ID = "id";
        public static final String _TABLENAME = "readNotice";
        public static final String _CREATE =
                "create table " + _TABLENAME + "("
                        + ID + " integer primary key)";
    }

    private static final String DATABASE_NAME = "push.db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
            super(context, name, factory, version, errorHandler);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CreateDelivery._CREATE);
            db.execSQL(CreateNotice._CREATE);
            db.execSQL(CreateReadNotice._CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public DBManager(Context context) {
        mCtx = context;
    }

    public DBManager open() {
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void removeAll() {
        mDB.delete(CreateReadNotice._TABLENAME, null, null);
        mDB.delete(CreateNotice._TABLENAME, null, null);
        mDB.delete(CreateDelivery._TABLENAME, null, null);
    }

    //Delivery
    public boolean insertDelivery(int id) {
        if (!isExistDelivery(id)) {
            ContentValues values = new ContentValues();
            values.put(CreateDelivery.ID, id);
            mDB.insert(CreateDelivery._TABLENAME, null, values);
            return true;
        }
        return false;
    }

    public void deleteDelivery(int id) {
        mDB.execSQL("DELETE FROM " + CreateDelivery._TABLENAME+ " WHERE " + CreateDelivery.ID + "=" + id);
    }

    public void clearDeliveries() {
        mDB.execSQL("delete from " + CreateDelivery._TABLENAME);
    }

    public boolean isExistDelivery(int id) {
        Cursor mCursor = mDB.query(CreateDelivery._TABLENAME, null, CreateDelivery.ID + "=\"" + id+"\"", null, null, null, null);
        if (mCursor.moveToNext()) {
            mCursor.close();
            return true;
        } else {
            mCursor.close();
            return false;
        }
    }

    public int deliveriesCount() {
        Cursor mCursor = mDB.rawQuery("SELECT COUNT(*) FROM " + CreateDelivery._TABLENAME, null);
        mCursor.moveToNext();
        int count = mCursor.getInt(0);
        mCursor.close();

        return count;
    }

    //Notice
    public boolean insertNotice(int id) {
        if (!isExistNotice(id)) {
            ContentValues values = new ContentValues();
            values.put(CreateNotice.ID, id);
            mDB.insert(CreateNotice._TABLENAME, null, values);
            return true;
        }
        return false;
    }

    public void deleteNotice(int id) {
        mDB.execSQL("DELETE FROM " + CreateNotice._TABLENAME+ " WHERE " + CreateNotice.ID + "=" + id);
    }

    public void clearNotices() {
        mDB.execSQL("delete from " + CreateNotice._TABLENAME);
    }

    public boolean isExistNotice(int id) {
        Cursor mCursor = mDB.query(CreateNotice._TABLENAME, null, CreateNotice.ID + "=\"" + id+"\"", null, null, null, null);
        if (mCursor.moveToNext()) {
            mCursor.close();
            return true;
        } else {
            mCursor.close();
            return false;
        }
    }

    public int noticesCount() {
        Cursor mCursor = mDB.rawQuery("SELECT COUNT(*) FROM " + CreateNotice._TABLENAME, null);
        mCursor.moveToNext();
        int count = mCursor.getInt(0);
        mCursor.close();

        return count;
    }


    //Read Notice
    public boolean insertReadNotice(int id) {
        if (!isExistReadNotice(id)) {
            ContentValues values = new ContentValues();
            values.put(CreateReadNotice.ID, id);
            mDB.insert(CreateReadNotice._TABLENAME, null, values);
            return true;
        }
        return false;
    }

    public void clearReadNotices() {
        mDB.execSQL("delete from " + CreateReadNotice._TABLENAME);
    }

    public boolean isExistReadNotice(int id) {
        Cursor mCursor = mDB.query(CreateReadNotice._TABLENAME, null, CreateReadNotice.ID + "=\"" + id+"\"", null, null, null, null);
        if (mCursor.moveToNext()) {
            mCursor.close();
            return true;
        } else {
            mCursor.close();
            return false;
        }
    }

    public int readNoticesCount() {
        Cursor mCursor = mDB.rawQuery("SELECT COUNT(*) FROM " + CreateReadNotice._TABLENAME, null);
        mCursor.moveToNext();
        int count = mCursor.getInt(0);
        mCursor.close();

        return count;
    }

    public void close() {
        mDB.close();
    }
}
