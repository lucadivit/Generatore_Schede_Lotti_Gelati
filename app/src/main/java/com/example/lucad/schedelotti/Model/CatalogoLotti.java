package com.example.lucad.schedelotti.Model;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.lucad.schedelotti.Foundation.RepositoryIngredientiFactory;
import com.example.lucad.schedelotti.Foundation.RepositoryLottiFactory;
import com.example.lucad.schedelotti.Foundation.SQLiteRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CatalogoLotti {
    private static final CatalogoLotti instance = new CatalogoLotti();
    private static SQLiteRepository repositoryLotti;
    private static SQLiteRepository repositoryIngredienti;

    public static CatalogoLotti getInstance(Context context){
        repositoryLotti = (SQLiteRepository) RepositoryLottiFactory.getInstance().getRepositoryLotti(context);
        repositoryIngredienti = (SQLiteRepository) RepositoryIngredientiFactory.getInstance().getRepositoryIngredienti(context);
        return instance;
    }

    private CatalogoLotti(){

    }

    public int cancellaVecchiLottiDalCatalogo(){
        List listLotti = repositoryLotti.getItemsList();
        List listIngredienti = repositoryIngredienti.getItemsList();
        List<Lotto> lottiInutilizzati = new ArrayList<>();
        Iterator iterator = listLotti.iterator();
        Iterator iteratorIngredienti = listIngredienti.iterator();
        boolean trovato;
        while (iterator.hasNext()){
            trovato = false;
            Lotto lotto = (Lotto) iterator.next();
            while (iteratorIngredienti.hasNext()){
                Ingrediente ingrediente = (Ingrediente) iteratorIngredienti.next();
                if(lotto.getNumeroLotto().matches(ingrediente.getNumeroLotto())){
                    trovato = true;
                    break;
                }
            }
            if(!trovato){
                lottiInutilizzati.add(lotto);
            }
        }
        if(lottiInutilizzati.size() > 0){
            Iterator iterator1 = lottiInutilizzati.iterator();
            while (iterator1.hasNext()){
                Lotto lotto = (Lotto) iterator1.next();
                if(repositoryLotti.removeItem(lotto) != 2){
                    return 3;
                }
            }
        }else {
            return 1;
        }
        return 0;
    }
}
