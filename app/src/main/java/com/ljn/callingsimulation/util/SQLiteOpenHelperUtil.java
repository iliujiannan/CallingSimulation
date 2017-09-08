package com.ljn.callingsimulation.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.ljn.callingsimulation.bean.Calling;

import java.util.LinkedList;
import java.util.Vector;

import static android.content.ContentValues.TAG;

/**
 * Created by 12390 on 2017/8/30.
 * Written by ljn , used to operate database easily.
 */
public class SQLiteOpenHelperUtil extends SQLiteOpenHelper {
    public static final String[] args = new String[] {"calling_id", "caller", "pattern", "content", "start_time", "caller_sex", "dialect","voice", "is_open", "del"};
    private static final String table_name = "calling";
    public SQLiteOpenHelperUtil(Context context) {
        super(context, table_name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists "+ table_name +"(" +
                args[0] + " integer  PRIMARY KEY autoincrement," +
                args[1] + " varchar(255), " +
                args[2] + " varchar(255), " +
                args[3] + " varchar(255), " +
                args[4] + " text, " +
                args[5] + " varchar(255), " +
                args[6] + " varchar(255), " +
                args[7] + " varchar(255), " +
                args[8] + " varchar(255)," +
                args[9] + " varchar(255));";
        Log.i(TAG,sql);
        Log.i(TAG, "create Database------------->");
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Vector<Calling> doQuery(String selection, String[] selectionArgs){
        SQLiteDatabase db = this.getReadableDatabase();
        Vector<Calling> callings = new Vector<Calling>();
        Cursor cursor = db.query(table_name, args,selection,selectionArgs,null,null,"calling_id");
        while(cursor.moveToNext()){
            Calling calling = new Calling();
            calling.setCallingId(cursor.getInt(cursor.getColumnIndex(args[0])));
            calling.setCaller(cursor.getString(cursor.getColumnIndex(args[1])));
            calling.setPattern(cursor.getString(cursor.getColumnIndex(args[2])));
            calling.setContent(cursor.getString(cursor.getColumnIndex(args[3])));
            calling.setStartTime(cursor.getString(cursor.getColumnIndex(args[4])));
            calling.setCallerSex(cursor.getString(cursor.getColumnIndex(args[5])));
            calling.setDialect(cursor.getString(cursor.getColumnIndex(args[6])));
            calling.setVoice(cursor.getString(cursor.getColumnIndex(args[7])));
            calling.setIsOpen(cursor.getString(cursor.getColumnIndex(args[8])));
            calling.setDel(cursor.getString(cursor.getColumnIndex(args[9])));
            callings.add(calling);
        }
        db.close();
        return callings;
    }

    public boolean doInsert(String[] values){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for (int i = 1; i <= 9; i++) {
            contentValues.put(args[i],values[i]);
        }
        db.insert(table_name,null,contentValues);
        db.close();
        return true;
    }

    public boolean doUpdate(String[] values, String selecttion, String[] selectionValues){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for (int i = 1; i <= 9; i++) {
            if(values[i]!="") {
                contentValues.put(args[i], values[i]);
            }
        }
        db.update(table_name, contentValues, selecttion, selectionValues);
        return true;
    }

    public boolean doDelete(String selecttion, String[] selectionValues){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table_name, selecttion, selectionValues);
        return true;
    }
}
