package com.example.lucad.schedelotti.Foundation;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;

import com.example.lucad.schedelotti.Model.Ricetta;

import java.util.List;

public interface Repository {
    Object getItem(String itemName);
    List<Object> getItemsList();
    List<Object> getLocalCache();
    boolean addItem(Object o);
    boolean updateItem(Object o);
    Object searchIntoLocalCache(String o);
    boolean addElementToLocalCache(Object o);
    boolean removeFromLocalCache(String s);
    int removeItem(Object o);
    Cursor rawQuery(String query, String[] args);

}
