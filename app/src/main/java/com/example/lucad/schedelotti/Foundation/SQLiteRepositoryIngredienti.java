package com.example.lucad.schedelotti.Foundation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.lucad.schedelotti.Model.DescrizioneIngrediente;
import com.example.lucad.schedelotti.Model.Ingrediente;
import com.example.lucad.schedelotti.Model.Lotto;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class SQLiteRepositoryIngredienti extends SQLiteRepository{
    public final String tableName = "ingrediente";
    public final String primaryKey = "nome";
    public final String lottoColName = "lotto";
    public final String descrizioneColName = "descrizione";
    public final int pkIDX = 0;
    public final int lottoIDX=1;
    public final int noteIDX=2;
    private SQLiteRepositoryLotto sqLiteRepositoryLotto = (SQLiteRepositoryLotto) RepositoryLottiFactory.getInstance().getRepositoryLotti(this.context);
    private List<Object> listaIngredienti = new ArrayList<>();

    public SQLiteRepositoryIngredienti(Context context){
        super(context);
        this.createTable();
        initializeCache();
    }

    private void initializeCache(){
        try{
            List<Ingrediente> ingredientes = (List<Ingrediente>)(List) this.getItemsList();
            Iterator iterator = ingredientes.iterator();
            while (iterator.hasNext()){
                this.addElementToLocalCache((Ingrediente) iterator.next());
            }
        }catch (Exception e){
            Log.d("Caricamento Cache", "Ingredienti", e);
        }
    }
    @Override
    public Cursor rawQuery(String query, String[] args) {
        Cursor cursor = this.sqLiteDatabase.rawQuery(query, args, null);
        return cursor;
    }
    @Override
    public Ingrediente getItem(String nomeIngrediente) {
        //Cerco nella cache locale
        Ingrediente ingrediente = (Ingrediente) this.searchIntoLocalCache(nomeIngrediente);
        if(ingrediente != null){
            Log.d("Cache Ing.", "Loaded " + ingrediente.getNomeIngrediente());
            return ingrediente;
        }else{
            try{
                //Se non è nella cache lo prelevo da DB e lo aggiungo alla cache
                String query = "SELECT * FROM " + this.tableName + " WHERE " + this.primaryKey + "=" + "\'" + nomeIngrediente + "\'";
                Cursor data = sqLiteDatabase.rawQuery(query, null);
                if(data.getCount() > 0){
                    while (data.moveToNext()){
                        Lotto lotto = sqLiteRepositoryLotto.getItem(data.getString(this.lottoIDX));
                        if(lotto != null){
                            ingrediente = new Ingrediente(lotto, new DescrizioneIngrediente(data.getString(this.pkIDX), data.getString(this.noteIDX)));
                            if(searchIntoLocalCache(ingrediente.getNomeIngrediente()) == null){
                                this.addElementToLocalCache(ingrediente);
                            }else {
                                this.removeFromLocalCache(ingrediente.getNomeIngrediente());
                                this.addElementToLocalCache(ingrediente);
                            }
                        }else {
                            ingrediente = null;
                        }
                    }
                }else {
                    ingrediente = null;
                }
            }catch (Exception e){
                Log.d("", "", e);
                ingrediente = null;
            }
            return ingrediente;
        }
    }

    @Override
    public boolean updateItem(Object o) {
        ContentValues contentValues = new ContentValues();
        Ingrediente ingrediente = (Ingrediente) o;
        this.removeFromLocalCache(ingrediente.getNomeIngrediente().replaceAll("'", ""));
        contentValues.put(this.lottoColName, ingrediente.getNumeroLotto().replaceAll("'", ""));
        contentValues.put(this.descrizioneColName, ingrediente.getNote().replaceAll("'", ""));
        int res = sqLiteDatabase.update(this.tableName, contentValues, this.primaryKey+ "=" + "\'" + ingrediente.getNomeIngrediente() + "\'", null);
        if (res > 0) {
            this.addElementToLocalCache(ingrediente);
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean addItem(Object o) {
        long res = 0;
        Ingrediente ingrediente = (Ingrediente) o;
        try{
            ContentValues contentValues = new ContentValues();
            contentValues.put(this.primaryKey, ingrediente.getNomeIngrediente().replaceAll("'", ""));
            contentValues.put(this.lottoColName, ingrediente.getNumeroLotto().replaceAll("'", ""));
            contentValues.put(this.descrizioneColName, ingrediente.getNote().replaceAll("'", ""));
            res = sqLiteDatabase.insert(this.tableName, null, contentValues);
        }catch (Exception e){
            Log.d("Errore", "Aggiunta Ingrediente", e);
        }
        if(res != -1){
            if(searchIntoLocalCache(ingrediente.getNomeIngrediente()) == null){
                this.addElementToLocalCache(ingrediente);
            }
            return true;
        }else {
            return false;
        }
    }

    @Override
    public int removeItem(Object o) {
        if(o == null){
            return 0;
        }else {
            try {
                int res = 0;
                Ingrediente ingrediente = (Ingrediente) o;
                res = sqLiteDatabase.delete(this.tableName, this.primaryKey + "=" + "\'" +  ingrediente.getNomeIngrediente() + "\'", null);
                if(res > 0){
                    this.removeFromLocalCache(ingrediente.getNomeIngrediente());
                    return 2;
                }else {
                    return 1;
                }
            }catch (Exception e){
                return 3;
            }
        }
    }

    @Override
    public List<Object> getItemsList() {
        List<Object> objectList = new ArrayList<>();
        String query = "SELECT * FROM " + this.tableName;
        Cursor data = sqLiteDatabase.rawQuery(query, null);
        if(data.getCount() > 0){
            while (data.moveToNext()){
                Lotto lotto = sqLiteRepositoryLotto.getItem(data.getString(this.lottoIDX));
                Ingrediente ingrediente = new Ingrediente(lotto, new DescrizioneIngrediente(data.getString(this.pkIDX), data.getString(this.noteIDX)));
                objectList.add(ingrediente);
            }
        }
        return objectList;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    private void createTable(){
        try {
            String query = "CREATE TABLE IF NOT EXISTS " + this.tableName + " ( " + this.primaryKey + " varchar PRIMARY KEY, " + this.lottoColName + " varchar, "+ this.descrizioneColName + " text, " +
                    "FOREIGN KEY( " + this.lottoColName + " ) REFERENCES " + this.sqLiteRepositoryLotto.tableName + "("+ this.sqLiteRepositoryLotto.primaryKey +") ON DELETE CASCADE)";
            sqLiteDatabase.execSQL(query);
            Log.d("SQLite", "Tabella Ingredienti Creata");
        }catch (Exception e){
            Log.d("SQLite Error", "Tabella Ingredienti Non Creata", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public boolean addElementToLocalCache(Object o) {
        return this.listaIngredienti.add((Ingrediente) o);
    }

    @Override
    public boolean removeFromLocalCache(String nomeIngrediente) {
        boolean trovato = false;
        Ingrediente ingrediente = null;
        Iterator iterator = this.listaIngredienti.iterator();
        while (iterator.hasNext()){
            ingrediente = (Ingrediente) iterator.next();
            if (ingrediente.getNomeIngrediente().equals(nomeIngrediente)){
                trovato = true;
                break;
            }
        }
        if(trovato){
            return this.listaIngredienti.remove(ingrediente);
        }else {
            return false;
        }
    }

    @Override
    public List<Object> getLocalCache() {
        return this.listaIngredienti;
    }

    @Override
    public Object searchIntoLocalCache(String nomeIngrediente) {
        Ingrediente ingrediente = null;
        ArrayList<Ingrediente> ingredientes = (ArrayList<Ingrediente>)(List) this.getLocalCache();
        try{
            for (int i = 0; i<ingredientes.size(); i++){
                if(ingredientes.equals(ingredientes.get(i).getNomeIngrediente())){
                    ingrediente = ingredientes.get(i);
                    break;
                }
            }
        }catch (Exception e){

        }
        return ingrediente;
    }
}
