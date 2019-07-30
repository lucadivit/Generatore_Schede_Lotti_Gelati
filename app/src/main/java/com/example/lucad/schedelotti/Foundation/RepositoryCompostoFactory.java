package com.example.lucad.schedelotti.Foundation;

import android.content.Context;

public class RepositoryCompostoFactory {
    private static final RepositoryCompostoFactory ourInstance = new RepositoryCompostoFactory();
    private Repository repositoryComposto = null;

    public static RepositoryCompostoFactory getInstance() {
        return ourInstance;
    }

    private RepositoryCompostoFactory() {
    }

    public Repository getRepositoryComposto(Context context){
        //TODO Implementare politica di scelta per la costruzione della repository
        if (repositoryComposto == null){
            repositoryComposto = new SQLiteRepositoryComposto(context);
        }
        return repositoryComposto;
    }
}
