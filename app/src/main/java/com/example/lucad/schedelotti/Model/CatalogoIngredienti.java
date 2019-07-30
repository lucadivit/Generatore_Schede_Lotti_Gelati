package com.example.lucad.schedelotti.Model;

import android.content.Context;
import android.util.Log;

import com.example.lucad.schedelotti.Foundation.RepositoryIngredientiFactory;
import com.example.lucad.schedelotti.Foundation.RepositoryLottiFactory;
import com.example.lucad.schedelotti.Foundation.SQLiteRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CatalogoIngredienti {
    private static SQLiteRepository repositoryIngredienti;
    private static SQLiteRepository repositoryLotti;
    private static CatalogoRicette catalogoRicette;
    private static final CatalogoIngredienti instance = new CatalogoIngredienti();

    public static CatalogoIngredienti getInstance(Context context){
        repositoryIngredienti = (SQLiteRepository) RepositoryIngredientiFactory.getInstance().getRepositoryIngredienti(context);
        repositoryLotti = (SQLiteRepository) RepositoryLottiFactory.getInstance().getRepositoryLotti(context);
        catalogoRicette = CatalogoRicette.getInstance(context);
        return instance;
    }

    private CatalogoIngredienti(){
    }

    //I nomi li prendo sempre dal DB in modo da non dovermi preoccupare di eventuali inconsistenze della cache
    public List<String> getNomiIngredienti(){
        List<String> nomi = new ArrayList<>();
        ArrayList<Object> ingredienti = (ArrayList<Object>) repositoryIngredienti.getItemsList();
        if(ingredienti.size()>0){
            Iterator iterator = ingredienti.iterator();
            while (iterator.hasNext()){
                Ingrediente ingrediente = (Ingrediente) iterator.next();
                nomi.add(ingrediente.getNomeIngrediente());
            }
            Collections.sort(nomi);
        }
        return nomi;
    }

    private List<String> getNumoroLotti(){
        List<String> lotti = new ArrayList<>();
        ArrayList<Object> lottiList = (ArrayList<Object>) repositoryLotti.getItemsList();
        if(lottiList.size()>0){
            Iterator iterator = lottiList.iterator();
            while (iterator.hasNext()){
                Lotto lotto = (Lotto) iterator.next();
                if(lotto != null){
                    lotti.add(lotto.getNumeroLotto());
                }

            }
        }
        return lotti;
    }

    public int rimuoviIngredienteDalCatagolo(Ingrediente ingrediente){
        catalogoRicette.removeRecipeByIngredient(ingrediente.getNomeIngrediente());
        if(repositoryIngredienti.removeItem(ingrediente) == 2){
            return 2;
        }else {
            return 1;
        }
    }

    public Ingrediente getIngredienteByName(String nomeIngrediente){
        return (Ingrediente) this.repositoryIngredienti.getItem(nomeIngrediente);
    }

    public int aggiungiIngredienteAlCatalogo(Ingrediente ingrediente){
        int res = 0;
        int res_lotto = 0;
        //Modify Lotto

        if(this.getNumoroLotti().contains(ingrediente.getNumeroLotto())){
            if(repositoryLotti.updateItem(ingrediente.getLotto())){
                res_lotto = 3;
            }else {
                res_lotto = 2;
                res = 2;
            }
        }else {//Add Lotto
            if(repositoryLotti.addItem(ingrediente.getLotto())){
                res_lotto = 1;
            }else {
                res_lotto = 0;
                res = 0;
            }
        }
        if(res_lotto == 1 || res_lotto == 3){
            //Modify Ingrediente
            if(this.getNomiIngredienti().contains(ingrediente.getNomeIngrediente())){
                if(repositoryIngredienti.updateItem(ingrediente)){
                    res = 3;
                }else {
                    res = 2;
                }
            } else {//Add Ingrediente
                if(repositoryIngredienti.addItem(ingrediente)){
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
