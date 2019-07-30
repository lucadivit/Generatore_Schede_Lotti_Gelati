package com.example.lucad.schedelotti.Foundation;

import android.content.Context;

public class RepositoryIngredientiFactory {
    private static final RepositoryIngredientiFactory ourInstance = new RepositoryIngredientiFactory();
    private Repository repository_ingredienti = null;

    public static RepositoryIngredientiFactory getInstance() {
        return ourInstance;
    }

    private RepositoryIngredientiFactory() {
    }

    public Repository getRepositoryIngredienti(Context context){
        if(repository_ingredienti == null){
            //TODO Implementare politica di scelta per la costruzione della repository
            repository_ingredienti = new SQLiteRepositoryIngredienti(context);
        }
        return repository_ingredienti;
    }
}
