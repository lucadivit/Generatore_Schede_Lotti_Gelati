package com.example.lucad.schedelotti.Controller;

import android.content.Context;
import android.util.Log;

import com.example.lucad.schedelotti.Model.CatalogoIngredienti;
import com.example.lucad.schedelotti.Model.CatalogoRicette;
import com.example.lucad.schedelotti.Model.DescrizioneRicetta;
import com.example.lucad.schedelotti.Model.Ingrediente;
import com.example.lucad.schedelotti.Model.Ricetta;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AggiungiRicettaHandler {
    private static final AggiungiRicettaHandler ourInstance = new AggiungiRicettaHandler();
    private static CatalogoRicette catalogoRicette;
    private static CatalogoIngredienti catalogoIngredienti;
    private ArrayList<String> nomeIngredienteRicetta = new ArrayList<>();
    private ArrayList<Ingrediente> ingredientiRicetta = new ArrayList<>();

    public static AggiungiRicettaHandler getInstance(Context context) {
        catalogoRicette = CatalogoRicette.getInstance(context);
        catalogoIngredienti = CatalogoIngredienti.getInstance(context);
        return ourInstance;
    }

    private AggiungiRicettaHandler() {
    }

    public String[] getNomiRicette(){
        List<String> names = catalogoRicette.getNomiRicette();
        String[] nomi = new String[names.size()];
        for (int i=0; i<names.size(); i++){
            nomi[i]=names.get(i);
        }
        return nomi;
    }

    public List<String> getNomiIngredientiRicetta(String nomeRicetta){
        Ricetta ricetta = this.catalogoRicette.getRicettaByName(nomeRicetta);
        List<Ingrediente> ing = ricetta.getListaIngredienti();
        List<String> nomiIngredienti = new ArrayList<>();
        Iterator iterator = ing.iterator();
        int i = 0;
        while (iterator.hasNext()){
            Ingrediente ingrediente = (Ingrediente) iterator.next();
            nomiIngredienti.add(ingrediente.getNomeIngrediente());
        }
        return nomiIngredienti;
    }

    public List<String> getNomiIngredienti(){
        List<String> names = this.catalogoIngredienti.getNomiIngredienti();
        return names;
    }

    public boolean aggiungiIngredienteARicetta(String nomeIngrediente){
        return this.nomeIngredienteRicetta.add(nomeIngrediente);
    }

    public int rimuoviIngredienteRicetta(String nomeIngrediente){
        int res = 0;
        if(this.nomeIngredienteRicetta.contains(nomeIngrediente)){
            if(this.nomeIngredienteRicetta.remove(nomeIngrediente)){
                res = 1;
            }else {
                res = 2;
            }
        }else {
            res = 0;
        }
        return res;
    }

    public int aggiungiRicetta(String nomeRicetta){
        int res = 0;
        if(this.nomeIngredienteRicetta.size() > 0){
            ArrayList<Ingrediente> listIng = new ArrayList<>();
            Iterator iterator = this.nomeIngredienteRicetta.iterator();
            while (iterator.hasNext()){
                String nomeIng = (String) iterator.next();
                listIng.add((Ingrediente) catalogoIngredienti.getIngredienteByName(nomeIng));
            }
            Ricetta ricetta = new Ricetta(listIng, new DescrizioneRicetta(nomeRicetta, ""));
            res = catalogoRicette.aggiungiRicettaAlCatalogo(ricetta);
            if(res == 3 || res == 1){
                this.nomeIngredienteRicetta.clear();
            }
        }else {
            res = 5;
        }
        return res;
    }

    public int rimuoviRicetta(String nomeRicetta){
        int res = 0;
        if (nomeRicetta.matches("")){
            res = 5;
        }else {
            res = catalogoRicette.rimuoviRicettaDalCatalogo(nomeRicetta);
        }
        return res;
    }
}
