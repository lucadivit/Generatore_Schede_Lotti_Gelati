package com.example.lucad.schedelotti.Foundation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.lucad.schedelotti.Model.Ingrediente;
import com.example.lucad.schedelotti.Model.Lotto;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class SQLiteRepositoryLotto extends SQLiteRepository{
    public final String tableName= "lotto";
    public final String primaryKey = "numerolotto";
    public final String scadenzaColName = "scadenza";
    public final int pkIDX = 0;
    public final int scadenzaIDX=1;
    private List<Object> listaLotti = new ArrayList<>();

    public SQLiteRepositoryLotto(Context context){
        super(context);
        this.createTable();
        this.initializeCache();
    }

    private void initializeCache(){
        try{
            List<Lotto> lottos = (List<Lotto>)(List) this.getItemsList();
            Iterator iterator = lottos.iterator();
            while (iterator.hasNext()){
                this.addElementToLocalCache((Lotto) iterator.next());
            }
        }catch (Exception e){
            Log.d("Caricamento Cache", "Lotti", e);
        }
    }

    @Override
    public List<Object> getItemsList() {
        List<Object> objectList = new ArrayList<>();
        String query = "Select * from " + this.tableName;
        Cursor data = sqLiteDatabase.rawQuery(query, null);
        if(data.getCount() > 0){
            while (data.moveToNext()){
                String date = data.getString(this.scadenzaIDX);
                Lotto lotto = new Lotto(data.getString(this.pkIDX), date);
                objectList.add(lotto);
            }
        }
        return objectList;
    }

    @Override
    public boolean addItem(Object o) {
        long res = 0;
        Lotto lotto = (Lotto) o;
        try{
            ContentValues contentValues = new ContentValues();
            contentValues.put(this.primaryKey, lotto.getNumeroLotto().replaceAll("'", ""));
            contentValues.put(this.scadenzaColName, lotto.getScadenza().replaceAll("'", ""));
            res = sqLiteDatabase.insert(this.tableName, null, contentValues);
            Log.d("Lotto", "Aggiunto Lotto");
        }catch (Exception e){
            Log.d("Errore", "Aggiunta Lotto", e);
        }
        if(res != -1){
            this.addElementToLocalCache(lotto);
            return true;
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
                Lotto lotto = (Lotto) o;
                res = sqLiteDatabase.delete(this.tableName, this.primaryKey + "=" + "\'" +  lotto.getNumeroLotto() + "\'", null);
                if(res > 0){
                    this.removeFromLocalCache(lotto.getNumeroLotto());
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
            String query = "CREATE TABLE IF NOT EXISTS " + this.tableName + "( " + this.primaryKey + " varchar PRIMARY KEY, " + this.scadenzaColName + " varchar)";
            sqLiteDatabase.execSQL(query);
            Log.d("SQLite", "Tabella Lotto Creata");
        }catch (Exception e){
            Log.d("SQLite", "Tabella Lotto non Creata");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public boolean addElementToLocalCache(Object o) {
        return this.listaLotti.add((Lotto) o);
    }

    @Override
    public Lotto getItem(String numeroLotto) {
        Lotto lotto = null;
        //Cerco nella cache locale
        lotto = (Lotto) this.searchIntoLocalCache(numeroLotto);
        if(lotto != null){
            Log.d("Cache Lotto", "Loaded " + lotto.getNumeroLotto());
            return lotto;
        }else{
            try{
                //Se non è nella cache lo prelevo da DB e lo aggiungo alla cache
                String query = "SELECT * FROM " + this.tableName + " WHERE " + this.primaryKey + "=" + "\'" + numeroLotto + "\'";
                Cursor data = sqLiteDatabase.rawQuery(query, null);
                if(data.getCount() > 0){
                    while (data.moveToNext()){
                        String date = data.getString(this.scadenzaIDX);
                        lotto = new Lotto(data.getString(this.pkIDX), date);
                        if(searchIntoLocalCache(lotto.getNumeroLotto())==null){
                            this.addElementToLocalCache(lotto);
                        }else {
                            this.removeFromLocalCache(lotto.getNumeroLotto());
                            this.addElementToLocalCache(lotto);
                        }
                    }
                }else {
                    lotto = null;
                }
            }catch (Exception e){
                Log.d("","",e);
                lotto = null;
            }
            return lotto;
        }
    }

    @Override
    public boolean updateItem(Object o) {
        ContentValues contentValues = new ContentValues();
        Lotto lotto = (Lotto) o;
        this.removeFromLocalCache(lotto.getNumeroLotto().replaceAll("'", ""));
        contentValues.put(this.scadenzaColName, lotto.getScadenza().replaceAll("'", ""));
        int res = sqLiteDatabase.update(this.tableName, contentValues, this.primaryKey+ "=" + "\'" + lotto.getNumeroLotto() + "\'", null);
        if (res > 0) {
            this.addElementToLocalCache(lotto);
            return true;
        }else {
            return false;
        }
    }
    @Override
    public Cursor rawQuery(String query, String[] args) {
        Cursor cursor = this.sqLiteDatabase.rawQuery(query, args, null);
        return cursor;
    }

    @Override
    public boolean removeFromLocalCache(String numeroLotto) {
        boolean trovato = false;
        Lotto lotto = null;
        Iterator iterator = this.listaLotti.iterator();
        while (iterator.hasNext()){
            lotto = (Lotto) iterator.next();
            if (lotto.getNumeroLotto().equals(numeroLotto)){
                trovato = true;
                break;
            }
        }
        if(trovato){
            return this.listaLotti.remove(lotto);
        }else {
            return false;
        }
    }

    @Override
    public List<Object> getLocalCache() {
        return this.listaLotti;
    }

    @Override
    public Object searchIntoLocalCache(String numeroLotto) {
        Lotto lotto = null;
        ArrayList<Lotto> lottos = (ArrayList<Lotto>)(List) this.getLocalCache();
        try{
            for (int i = 0; i<lottos.size(); i++){
                if(lotto.equals(lottos.get(i).getNumeroLotto())){
                    lotto = lottos.get(i);
                    break;
                }
            }
        }catch (Exception e){

        }
        return lotto;
    }

}
