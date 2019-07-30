package com.example.lucad.schedelotti.Model;

import android.content.Context;

public class SchedaIngredientiFactory {
    private static final SchedaIngredientiFactory ourInstance = new SchedaIngredientiFactory();
    private Scheda scheda = null;

    public static SchedaIngredientiFactory getInstance() {
        return ourInstance;
    }

    private SchedaIngredientiFactory() {
    }

    public Scheda getGeneratoreScheda(Context context){
        if(scheda == null){
            //TODO Qui ci va politica di scelta in base alle opzioni
            scheda = new SchedaPDFIngredienti("item.pdf", context);
        }
        return scheda;
    }
}
