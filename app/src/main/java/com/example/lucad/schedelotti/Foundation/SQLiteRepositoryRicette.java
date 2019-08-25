package com.example.lucad.schedelotti.Foundation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.lucad.schedelotti.Model.DescrizioneIngrediente;
import com.example.lucad.schedelotti.Model.DescrizioneRicetta;
import com.example.lucad.schedelotti.Model.Ingrediente;
import com.example.lucad.schedelotti.Model.Lotto;
import com.example.lucad.schedelotti.Model.Ricetta;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class SQLiteRepositoryRicette extends SQLiteRepository {

    List<Object> listaRicette = new ArrayList<>();
    public static final String primary_key = "nome";
    public static final String descrizioneColName = "descrizione";
    public static final String table_name = "ricetta";
    public static final int descrizioneColIDX = 1;
    public static final int pkColIDX = 0;

    public SQLiteRepositoryRicette(Context context){
        super(context);
        this.createTable();
        this.initializeCache();
    }

    private void initializeCache(){
        try{
            List<HashMap<String, String>> infoRecipes = (List<HashMap<String, String>>)(List) this.getItemsList();
            Iterator iterator = infoRecipes.iterator();
            while (iterator.hasNext()){
                this.addElementToLocalCache((HashMap<String, String>) iterator.next());
            }
        }catch (Exception e){
            Log.d("Caricamento Cache", "InfoRecipes", e);
        }
    }

    @Override
    public List<Object> getItemsList() {
        List<Object> objectList = new ArrayList<>();
        HashMap<String, String> hashMap = new HashMap<>();
        String query = "SELECT * FROM " + this.table_name;
        Cursor data = sqLiteDatabase.rawQuery(query, null);
        if(data.getCount() > 0){
            while (data.moveToNext()){
                String nomeRicetta = data.getString(this.pkColIDX);
                String noteRicetta = data.getString(this.descrizioneColIDX);
                hashMap.put(nomeRicetta, noteRicetta);
                objectList.add(hashMap);
            }
        }
        return objectList;
    }

    @Override
    public HashMap<String, String> getItem(String nomeRicetta) {
        //HashMap<String, String> hashMap = (HashMap) this.searchIntoLocalCache(nomeRicetta);
        HashMap<String, String> hashMap = null;
        //Cerco nella cache
        if(hashMap != null){
            Log.d("Cache InfoRic.", "Loaded " + hashMap.containsKey(nomeRicetta));
            return hashMap;
        }else {//Se non ho risultati cerco nel DB e lo aggiungo alla cache
            try{
                hashMap = new HashMap<>();
                String query = "SELECT * FROM " + this.table_name + " WHERE " + this.primary_key + "=" + "\'" + nomeRicetta + "\'";
                Cursor cursor = sqLiteDatabase.rawQuery(query, null);
                if (cursor.getCount() > 0){
                    while (cursor.moveToNext()){
                        hashMap.put(cursor.getString(this.pkColIDX),cursor.getString(this.descrizioneColIDX));
                    }
                }
                if(searchIntoLocalCache(nomeRicetta)==null){
                    this.addElementToLocalCache(hashMap);
                }else {
                    this.removeFromLocalCache(nomeRicetta);
                    this.addElementToLocalCache(hashMap);
                }

            }catch (Exception e){
                Log.d("","",e);
                hashMap = null;
            }

        }
        return hashMap;
    }
    @Override
    public Cursor rawQuery(String query, String[] args) {
        Cursor cursor = this.sqLiteDatabase.rawQuery(query, args, null);
        return cursor;
    }

    @Override
    public boolean updateItem(Object o) {
        ContentValues contentValues = new ContentValues();
        Ricetta ricetta = (Ricetta) o;
        this.removeFromLocalCache(ricetta.getNomeRicetta());
        contentValues.put(this.descrizioneColName, ricetta.getNoteRicetta().replaceAll("'", ""));
        int res = sqLiteDatabase.update(this.table_name, contentValues, this.primary_key + "=" + "\'" + ricetta.getNomeRicetta() + "\'", null);
        if (res > 0) {
            this.addElementToLocalCache(new HashMap<String,String>().put(ricetta.getNomeRicetta(), ricetta.getNoteRicetta()));
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean addItem(Object o) {
        long res = 0;
        HashMap<String, String> hashMap = new HashMap<>();
        Ricetta ricetta = (Ricetta) o;
        try{
            ContentValues contentValues = new ContentValues();
            contentValues.put(this.primary_key, ricetta.getNomeRicetta().replaceAll("'", ""));
            contentValues.put(this.descrizioneColName, ricetta.getNoteRicetta().replaceAll("'", ""));
            res = sqLiteDatabase.insert(this.table_name, null ,contentValues);
            hashMap.put(ricetta.getNomeRicetta(), ricetta.getNoteRicetta());
        }catch (Exception e){
            Log.d("Errore", "Aggiunta Ricetta", e);
        }
        if(res != -1){
            if(searchIntoLocalCache(ricetta.getNomeRicetta())==null){
                this.addElementToLocalCache(hashMap);
            }
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean removeFromLocalCache(String nomeRicetta) {
        boolean trovato = false;
        HashMap<String,String> hash = null;
        Iterator iterator = this.listaRicette.iterator();
        while (iterator.hasNext()){
            hash = (HashMap<String,String>) iterator.next();
            if (hash.containsKey(nomeRicetta)){
                trovato = true;
                break;
            }
        }
        if(trovato){
            return this.listaRicette.remove(hash);
        }else {
            return false;
        }
    }

    @Override
    public int removeItem(Object o) {
        int res = 0;
        if(o == null){
            res = 0;
        }else {
            try{
                Ricetta ricetta = (Ricetta) o;
                res = sqLiteDatabase.delete(this.table_name, this.primary_key + "=" + "\'" +  ricetta.getNomeRicetta() + "\'", null);
                if(res > 0){
                    this.removeFromLocalCache(ricetta.getNomeRicetta());
                    res = 2;
                }else {
                    res = 1;
                }
            }catch (Exception e){
                res = 3;
            }
        }
        return res;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    private void createTable(){
        try {
            String query_ricetta = "CREATE TABLE IF NOT EXISTS " + this.table_name + "(" + this.primary_key + " varchar PRIMARY KEY, " + this.descrizioneColName + " text)";
            sqLiteDatabase.execSQL(query_ricetta);
            Log.d("SQLite", "Tabella Ricette Creata");
        }catch (Exception e){
            Log.d("SQLite Error", "Tabella Ricette Non Creata");
        }
    }

    @Override
    public Object searchIntoLocalCache(String nomeRicetta) {
        HashMap<String, String> hashMap = new HashMap<>();
        List<Ricetta> listaRicette = (ArrayList<Ricetta>)(List) this.getLocalCache();
        boolean trovato = false;
        try{
            Iterator iterator = listaRicette.iterator();
            while (iterator.hasNext()){
                hashMap = (HashMap<String, String>) iterator.next();
                if(hashMap.containsKey(nomeRicetta)){
                    trovato = true;
                    break;
                }
            }
        }catch (Exception e){

        }
        if(trovato){
            return hashMap;
        }else {
            return null;
        }
    }

    @Override
    public boolean addElementToLocalCache(Object o) {
        boolean res = false;
        try{
            HashMap<String, String> hashMap = (HashMap<String, String>) o;
            res = this.listaRicette.add(hashMap);
        }catch (Exception e){

        }
        return res;
    }

    @Override
    public List<Object> getLocalCache(){
        return this.listaRicette;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
