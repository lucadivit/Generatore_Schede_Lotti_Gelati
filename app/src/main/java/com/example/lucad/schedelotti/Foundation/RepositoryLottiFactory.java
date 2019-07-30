package com.example.lucad.schedelotti.Foundation;

import android.content.Context;

public class RepositoryLottiFactory {
    private static final RepositoryLottiFactory ourInstance = new RepositoryLottiFactory();
    private Repository repositoryLotti;

    public static RepositoryLottiFactory getInstance() {
        return ourInstance;
    }

    private RepositoryLottiFactory() {
    }

    public Repository getRepositoryLotti(Context context){
        //TODO Implementare politica di scelta per la costruzione della repository
        if(repositoryLotti == null){
            repositoryLotti = new SQLiteRepositoryLotto(context);
        }
        return repositoryLotti;
    }
}
