package com.example.lucad.schedelotti.Foundation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lucad.schedelotti.Model.DescrizioneRicetta;
import com.example.lucad.schedelotti.Model.Ingrediente;
import com.example.lucad.schedelotti.Model.Lotto;
import com.example.lucad.schedelotti.Model.Ricetta;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class SQLiteRepositoryComposto extends SQLiteRepository {
    List<Object> listaRicette = new ArrayList<>();
    public final String nomeRicettaColName = "nomeRicetta";
    public final String nomeIngredienteColName = "nomeIngrediente";
    public final int nomeRicettaColIDX = 0;
    public final int nomeIngredienteColIDX = 1;
    public final String primary_key = "nomeRicetta, nomeIngrediente";
    public final String table_name = "composto";
    private SQLiteRepositoryRicette sqLiteRepositoryRicette = (SQLiteRepositoryRicette) RepositoryRicetteFactory.getInstance().getRepositoryRicette(this.context);
    private SQLiteRepositoryIngredienti sqLiteRepositoryIngredienti = (SQLiteRepositoryIngredienti) RepositoryIngredientiFactory.getInstance().getRepositoryIngredienti(this.context);

    public SQLiteRepositoryComposto(@Nullable Context context) {
        super(context);
        this.createTable();
        initializeCache();
    }

    private void initializeCache(){
        try{
            List<Ricetta> ricettas = (List<Ricetta>)(List) this.getItemsList();
            Iterator iterator = ricettas.iterator();
            while (iterator.hasNext()){
                this.addElementToLocalCache((Ricetta) iterator.next());
            }
        }catch (Exception e){
            Log.d("Caricamento Cache", "Lotti", e);
        }
    }

    public Ricetta getItem(String nomeRicetta) {
        //Ricetta ricetta = (Ricetta) this.searchIntoLocalCache(nomeRicetta);
        Ricetta ricetta = null;
        ArrayList<Ingrediente> ingredienti = new ArrayList<>();
        //Cerco nella cache
        if(ricetta != null) {
            Log.d("Cache Ric", "Loaded " + ricetta.getNomeRicetta());
            return ricetta;
        }else {
            HashMap<String,String> info = sqLiteRepositoryRicette.getItem(nomeRicetta);
            if(info != null){
                try{
                    String query = "SELECT * FROM " + this.table_name + " WHERE " + this.nomeRicettaColName + "=" + "\'" + nomeRicetta + "\'";
                    Cursor composto = sqLiteDatabase.rawQuery(query, null);
                    if (composto.getCount()>0){
                        while (composto.moveToNext()){
                            ingredienti.add(sqLiteRepositoryIngredienti.getItem(composto.getString(this.nomeIngredienteColIDX)));
                        }
                        ricetta = new Ricetta(ingredienti, new DescrizioneRicetta(nomeRicetta, info.get(nomeRicetta)));
                        if(searchIntoLocalCache(ricetta.getNomeRicetta())==null){
                            this.addElementToLocalCache(ricetta);
                        }else {
                            this.removeFromLocalCache(ricetta.getNomeRicetta());
                            this.addElementToLocalCache(ricetta);
                        }
                    }else {
                        ricetta = null;
                    }
                }catch (Exception e){
                    Log.d("", "", e);
                    ricetta = null;
                }

            }else {
                ricetta = null;
            }

        }
        return ricetta;
    }

    @Override
    public List<Object> getItemsList() {
        List<Object> objectList = new ArrayList<>();
        String query = "SELECT DISTINCT " + this.nomeRicettaColName + " FROM " + this.table_name;
        Cursor data = sqLiteDatabase.rawQuery(query, null);
        if(data.getCount() > 0){
            while (data.moveToNext()){
                objectList.add(this.getItem(data.getString(this.nomeRicettaColIDX)));
            }
        }
        return objectList;
    }

    @Override
    public boolean addItem(Object o) {
        long res = 0;
        Ricetta ricetta = (Ricetta) o;
        try{
            Iterator iterator = ricetta.getListaIngredienti().iterator();
            while (iterator.hasNext()){
                Ingrediente ingrediente = (Ingrediente) iterator.next();
                ContentValues contentValues = new ContentValues();
                contentValues.put(this.nomeRicettaColName, ricetta.getNomeRicetta().replaceAll("'", ""));
                contentValues.put(this.nomeIngredienteColName, ingrediente.getNomeIngrediente().replaceAll("'", ""));
                res = sqLiteDatabase.replace(this.table_name, null ,contentValues);
                if(res==-1){
                    Log.d("Errore", "Problema inserimento composto");
                    break;
                }
            }
        }catch (Exception e){
            Log.d("Errore", "Aggiunta Ricetta composto", e);
        }
        if(res != -1){
            if(searchIntoLocalCache(ricetta.getNomeRicetta())==null){
                this.addElementToLocalCache(ricetta);
            }
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
                Ricetta ricetta = (Ricetta) o;
                Iterator iterator = ricetta.getListaIngredienti().iterator();
                /*
                while (iterator.hasNext()){
                    Ingrediente ingrediente = (Ingrediente) iterator.next();
                    Log.d("RimozioneComposto", ingrediente.getNomeIngrediente());
                    res = sqLiteDatabase.delete(this.table_name, this.nomeRicettaColName + "=? AND " + this.nomeIngredienteColName + "=?", new String[]{ricetta.getNomeRicetta(), ingrediente.getNomeIngrediente()});
                    Log.d("RimozioneComposto", String.valueOf(res));
                }*/
                res = sqLiteDatabase.delete(this.table_name, this.nomeRicettaColName + "=" + "\'" +  ricetta.getNomeRicetta() + "\'", null);
                //sqLiteDatabase.execSQL("DELETE FROM " + this.table_name + " WHERE " + this.nomeRicettaColName + " = " + "\'" + ricetta.getNomeRicetta() + "\'");
                this.removeFromLocalCache(ricetta.getNomeRicetta());
                if(res > 0){
                    this.removeFromLocalCache(ricetta.getNomeRicetta());
                    return 2;
                }else {
                    return 1;
                }
            }catch (Exception e){
                Log.d("Eccezione", "RM COmp", e);
                res = 3;
            }
        }
        return res;
    }

    private void createTable(){
        try{
            String query_composto = "CREATE TABLE IF NOT EXISTS " + this.table_name + "(" + this.nomeRicettaColName + " varchar, " + this.nomeIngredienteColName + " varchar," +
                    "PRIMARY KEY(" + this.primary_key + "), FOREIGN KEY (" + this.nomeRicettaColName + ") REFERENCES" +
                    " " + this.sqLiteRepositoryRicette.table_name + "(" + this.sqLiteRepositoryRicette.primary_key + ") ON DELETE CASCADE," +
                    " FOREIGN KEY (" + this.nomeIngredienteColName + ") REFERENCES " + this.sqLiteRepositoryIngredienti.tableName + "(" + this.sqLiteRepositoryIngredienti.primaryKey + ") ON DELETE CASCADE)";
            sqLiteDatabase.execSQL(query_composto);
            Log.d("SQLite", "Tabella Composto Creata");
        }catch (Exception e){
            Log.d("SQLite", "Tabella Composto Non Creata");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public boolean addElementToLocalCache(Object o) {
        boolean res = false;
        try{
            Ricetta ricetta = (Ricetta) o;
            this.listaRicette.add(ricetta);
            res = true;
        }catch (Exception e){
            Log.d("Errore", "Aggiunta Ingrediente", e);
        }
        return res;
    }

    @Override
    public Cursor rawQuery(String query, String[] args) {
        Cursor cursor = this.sqLiteDatabase.rawQuery(query, args, null);
        return cursor;
    }

    @Override
    public boolean removeFromLocalCache(String nomeRicetta) {
        boolean trovato = false;
        Ricetta ricetta = null;
        Iterator iterator = this.listaRicette.iterator();
        while (iterator.hasNext()){
            ricetta = (Ricetta) iterator.next();
            if (ricetta.getNomeRicetta().equals(nomeRicetta)){
                trovato = true;
                break;
            }
        }
        if(trovato){
            return this.listaRicette.remove(ricetta);
        }else {
            return false;
        }
    }

    @Override
    public List<Object> getLocalCache() {
        return this.listaRicette;
    }

    @Override
    public Object searchIntoLocalCache(String nomeRicetta) {
        Ricetta ricetta = null;
        List<Ricetta> listaRicette = (ArrayList<Ricetta>)(List) this.getLocalCache();
        try{
            for (int i = 0; i<listaRicette.size(); i++){
                if(nomeRicetta.equals(listaRicette.get(i).getNomeRicetta())){
                    ricetta = listaRicette.get(i);
                    break;
                }
            }
        }catch (Exception e){

        }
        return ricetta;
    }

    @Override
    public boolean updateItem(Object o) {
        return false;
    }
}
