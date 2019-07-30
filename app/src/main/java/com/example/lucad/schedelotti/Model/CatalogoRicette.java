package com.example.lucad.schedelotti.Model;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.lucad.schedelotti.Foundation.Repository;
import com.example.lucad.schedelotti.Foundation.RepositoryCompostoFactory;
import com.example.lucad.schedelotti.Foundation.RepositoryRicetteFactory;
import com.example.lucad.schedelotti.Foundation.SQLiteRepository;
import com.example.lucad.schedelotti.Foundation.SQLiteRepositoryComposto;
import com.example.lucad.schedelotti.Foundation.SQLiteRepositoryRicette;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class CatalogoRicette {
    private static final CatalogoRicette ourInstance = new CatalogoRicette();
    private static SQLiteRepositoryRicette repositoryRicette;
    private static SQLiteRepositoryComposto repositoryComposti;

    public static CatalogoRicette getInstance(Context context) {
        repositoryRicette = (SQLiteRepositoryRicette) RepositoryRicetteFactory.getInstance().getRepositoryRicette(context);
        repositoryComposti = (SQLiteRepositoryComposto) RepositoryCompostoFactory.getInstance().getRepositoryComposto(context);
        return ourInstance;
    }

    private CatalogoRicette() {

    }

    public List<String> getNomiRicette(){
        List<String> nomi = new ArrayList<>();
        ArrayList<Object> ricette = (ArrayList<Object>) repositoryComposti.getItemsList();
        if(ricette.size() > 0){
            Iterator iterator = ricette.iterator();
            while (iterator.hasNext()){
                Ricetta ricetta = (Ricetta) iterator.next();
                if(ricetta != null){
                    nomi.add(ricetta.getNomeRicetta());
                }
            }
            Collections.sort(nomi);
        }
        return nomi;
    }

    public List<HashMap<String, String>> getInfoRicette(){
        List<HashMap<String, String>> hashMaps = new ArrayList<>();
        hashMaps = (List<HashMap<String, String>>)(List) repositoryRicette.getItemsList();
        return hashMaps;
    }

    public Ricetta getRicettaByName(String nomeRicetta){
        return (Ricetta) this.repositoryComposti.getItem(nomeRicetta);
    }

    public int rimuoviRicettaDalCatalogo(String nomeRicetta){
        int res = 0;
        Ricetta ricetta = (Ricetta) repositoryComposti.getItem(nomeRicetta);
        if(repositoryRicette.removeItem(ricetta) == 2){
            return 1;
        }else {
            return 0;
        }
    }

    public int removeRecipeByIngredient(String nomeIngrediente){
        int res = 0;
        String query ="SELECT DISTINCT " + repositoryComposti.nomeRicettaColName + " FROM " + repositoryComposti.table_name +
                " WHERE " + repositoryComposti.nomeIngredienteColName + " = ?";
        Cursor cursor = repositoryComposti.rawQuery(query, new String[] {nomeIngrediente});
        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                String nomeRicetta = cursor.getString(repositoryComposti.nomeRicettaColIDX);
                res = this.rimuoviRicettaDalCatalogo(nomeRicetta);
            }
        }else {
            res = 0;
        }
        return res;
    }

    public int aggiungiRicettaAlCatalogo(Ricetta ricetta){
        int res = 0;
        int res_info;
        boolean trovato = false;
        List<HashMap<String, String>> hashMaps = this.getInfoRicette();
        Iterator iterator = hashMaps.iterator();
        while (iterator.hasNext()){
            HashMap<String, String> map = (HashMap<String, String>) iterator.next();
            if (map.containsKey(ricetta.getNomeRicetta())){
                trovato = true;
                break;
            }
        }
        //Modify Info Ric.
        if(trovato){
            //Modifica ricette non implementata
            res_info = 4;
            res = 4;
            /*
            if(repositoryRicette.updateItem(ricetta)){
                res_info = 3;

            }else {
                res_info = 2;
                res = 2;
            }*/
        }else {//Add info
            if(repositoryRicette.addItem(ricetta)){
                res_info = 1;
            }else {
                res_info = 0;
                res = 0;
            }
        }
        if(res_info == 1 || res_info == 3){
            //Modify
            if(this.getNomiRicette().contains(ricetta.getNomeRicetta())){
                if(repositoryComposti.updateItem(ricetta)){
                    res = 3;
                }else {
                    res = 2;
                }
            }else {//add
                if(repositoryComposti.addItem(ricetta)){
                    res = 1;
                }else {
                    res = 0;
                }
            }
        }else {

        }
        return res;
    }
}
