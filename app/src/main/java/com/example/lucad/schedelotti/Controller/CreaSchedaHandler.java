package com.example.lucad.schedelotti.Controller;

import android.content.Context;
import android.util.Log;

import com.example.lucad.schedelotti.Model.CatalogoIngredienti;
import com.example.lucad.schedelotti.Model.Ingrediente;
import com.example.lucad.schedelotti.Model.CatalogoRicette;
import com.example.lucad.schedelotti.Model.Ricetta;
import com.example.lucad.schedelotti.Model.Scheda;
import com.example.lucad.schedelotti.Model.SchedaIngredientiFactory;
import com.example.lucad.schedelotti.Model.SchedaRicetteFactory;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreaSchedaHandler {
    private static final CreaSchedaHandler ourInstance = new CreaSchedaHandler();
    private static CatalogoIngredienti catalogoIngredienti;
    private static CatalogoRicette ricettario;
    private static Scheda scheda;
    private static Scheda scheda_ricetta;

    public static CreaSchedaHandler getInstance(Context context) {
        catalogoIngredienti = CatalogoIngredienti.getInstance(context);
        ricettario = CatalogoRicette.getInstance(context);
        scheda = SchedaIngredientiFactory.getInstance().getGeneratoreScheda(context);
        scheda_ricetta = SchedaRicetteFactory.getInstance().getGeneratoreScheda(context);
        return ourInstance;
    }

    private CreaSchedaHandler() {
    }

    public List<String> getNomiIngredienti(){
        List<String> names = this.catalogoIngredienti.getNomiIngredienti();
        return names;
    }

    public List<String> getNomiRicette(){
        List<String> nomi = this.ricettario.getNomiRicette();
        return nomi;
    }

    public int aggiungiIngredienteAScheda(String nomeIngrediente){
        Ingrediente i = this.catalogoIngredienti.getIngredienteByName(nomeIngrediente);
        int res = 0;
        if(scheda.checkItem(i)){
            res = 0;
        }else {
            if(scheda.addItem(i)){
                res = 1;
            }else {
                res = 2;
            }
        }
        return res;
    }

    public int rimuoviIngredienteDaScheda(String nomeIngrediente){
        int RESPONSE = 0;
        Ingrediente i = this.catalogoIngredienti.getIngredienteByName(nomeIngrediente);
        if(!scheda.checkItem(i)){
            RESPONSE = 2;
        }else {
            if(scheda.removeItem(i)) RESPONSE = 1;
            else {
                RESPONSE = 0;
            }
        }
        return RESPONSE;
    }

    public boolean rimuoviIngredientiDaScheda(){
        return scheda.cleanScheda();
    }


    public int generaSchedaIngredienti(String nomeRicetta){
        String timeStamp = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(Calendar.getInstance().getTime());
        timeStamp = timeStamp.replace("/", "-");
        String nomeFile;
        /*
        Long tsLong = System.currentTimeMillis()/1000;
        String timeStamp = tsLong.toString();*/
        if(nomeRicetta.matches("")){
            nomeFile = timeStamp + "_" + "item.pdf";
        }else {
            nomeFile = timeStamp + "_" + nomeRicetta + ".pdf";
        }
        scheda.setNomeFile(nomeFile);
        scheda.setTitle(nomeRicetta);
        return scheda.generaScheda();
    }

    public int generaSchedaRicetta(String nomeRicetta){
        Ricetta ricetta = (Ricetta) ricettario.getRicettaByName(nomeRicetta);
        scheda_ricetta.addItem(ricetta);
        /*
        Long tsLong = System.currentTimeMillis()/1000;
        String timeStamp = tsLong.toString();*/
        String timeStamp = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(Calendar.getInstance().getTime());
        timeStamp = timeStamp.replace("/", "-");
        String nomeFile = timeStamp + "_" + nomeRicetta + ".pdf";
        scheda_ricetta.setNomeFile(nomeFile);
        return scheda_ricetta.generaScheda();
    }
}
