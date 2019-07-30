package com.example.lucad.schedelotti.Foundation;

import android.content.Context;

public class RepositoryRicetteFactory {
    private static final RepositoryRicetteFactory ourInstance = new RepositoryRicetteFactory();
    private Repository repositoryRicette = null;

    public static RepositoryRicetteFactory getInstance() {
        return ourInstance;
    }

    private RepositoryRicetteFactory() {
    }

    public Repository getRepositoryRicette(Context context){
        //TODO Implementare politica di scelta per la costruzione della repository
        if(repositoryRicette == null){
            repositoryRicette = new SQLiteRepositoryRicette(context);
        }
        return repositoryRicette;
    }
}
