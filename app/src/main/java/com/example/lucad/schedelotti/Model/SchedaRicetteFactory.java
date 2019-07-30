package com.example.lucad.schedelotti.Model;

import android.content.Context;

public class SchedaRicetteFactory {
    private static final SchedaRicetteFactory ourInstance = new SchedaRicetteFactory();
    private Scheda scheda = null;

    public static SchedaRicetteFactory getInstance() {
        return ourInstance;
    }

    private SchedaRicetteFactory() {
    }

    public Scheda getGeneratoreScheda(Context context){
        if(scheda == null){
            //TODO Qui ci va politica di scelta in base alle opzioni
            scheda = new SchedaPDFRicette("item.pdf", context);
        }
        return scheda;
    }
}
