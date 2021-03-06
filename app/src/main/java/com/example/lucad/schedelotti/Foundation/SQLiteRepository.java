package com.example.lucad.schedelotti.Foundation;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lucad.schedelotti.Model.Ricetta;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.List;

public abstract class SQLiteRepository extends SQLiteOpenHelper implements Repository {
    private static final String DATABASE = "generatore_schede.db";
    private static final int VERSION = 1;
    protected SQLiteDatabase sqLiteDatabase;
    protected Context context;
    protected boolean isOpen = false;

    @Override
    public abstract Cursor rawQuery(String query, String[] args);

    public SQLiteRepository(@Nullable Context context) {
        super(context, DATABASE, null, VERSION);
        this.sqLiteDatabase = getWritableDatabase();
        this.isOpen = true;
        try{
            this.sqLiteDatabase.execSQL("PRAGMA foreign_keys=ON;");
            //this.sqLiteDatabase.execSQL("PRAGMA cache_size=0;");
            //this.sqLiteDatabase.execSQL("PRAGMA auto_vacuum=0;");
        }catch (Exception e){
            Log.d("rep", "ex", e);
        }
        this.context = context;
    }

    public void closeDB(){
        if(this.isOpen){
            try{
                this.sqLiteDatabase.close();
                this.isOpen = false;
                Log.d("DB", "Closed");
            }catch (Exception e){
                Log.d("exc", "closedb", e);
            }
        }else {
            Log.d("DB", "Not Closed");
        }
    }

    public void openDB(){
        if(!this.isOpen){
            try{
                this.sqLiteDatabase = getWritableDatabase();
                this.isOpen = true;
                Log.d("DB", "Opened");
            }catch (Exception e){
                Log.d("exc", "opendb", e);
            }
        }else {
            Log.d("DB", "Not Opened");
        }
    }

    @Override
    public Object getItem(String itemName) {
        return null;
    }

    @Override
    public abstract List<Object> getItemsList();

    @Override
    public abstract boolean addItem(Object o);

    @Override
    public abstract int removeItem(Object o);

    @Override
    public abstract void onCreate(SQLiteDatabase db);

    @Override
    public abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

    @Override
    public abstract boolean addElementToLocalCache(Object o);

    @Override
    public abstract boolean removeFromLocalCache(String s);

    @Override
    public abstract List<Object> getLocalCache();

    @Override
    public abstract Object searchIntoLocalCache(String o);

    @Override
    public abstract boolean updateItem(Object o);
}
