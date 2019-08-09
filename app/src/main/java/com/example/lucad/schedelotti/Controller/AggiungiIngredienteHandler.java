package com.example.lucad.schedelotti.Controller;

import android.content.Context;
import android.util.Log;

import com.example.lucad.schedelotti.Model.CatalogoIngredienti;
import com.example.lucad.schedelotti.Model.DescrizioneIngrediente;
import com.example.lucad.schedelotti.Model.Ingrediente;
import com.example.lucad.schedelotti.Model.Lotto;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AggiungiIngredienteHandler {
    private static final AggiungiIngredienteHandler ourInstance = new AggiungiIngredienteHandler();
    private static CatalogoIngredienti catalogoIngredienti;

    public static AggiungiIngredienteHandler getInstance(Context context) {
        catalogoIngredienti = CatalogoIngredienti.getInstance(context);
        return ourInstance;
    }

    private AggiungiIngredienteHandler() {
    }

    public int aggiungiIngrediente(String nome, String lotto, String scadenza, String nota){
        int res = 0;
        if(nome.matches("") || lotto.matches("") || scadenza.matches("")){
            res = 4;
        }
        if (!validateDate(scadenza) && res != 4){
            res = 5;
        }
        if(res != 4 && res != 5){
            Ingrediente ingrediente = new Ingrediente(new Lotto(lotto, scadenza), new DescrizioneIngrediente(nome, nota));
            res = catalogoIngredienti.aggiungiIngredienteAlCatalogo(ingrediente);
        }
        return res;
    }

    private boolean validateDate(String date){
        String formatString = null;
        boolean res = false;
        switch (Locale.getDefault().toString()){
            case "en_US":
                formatString = "MM/dd/yyyy";
                break;
            case "it_IT":
                formatString = "dd/MM/yyyy";
                break;
            default:
                formatString = "MM/dd/yyyy";
                break;
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat(formatString);
            format.setLenient(false);
            format.parse(date);
            res = true;
        } catch (Exception e) {
            res = false;
        }
        return res;
    }

    public HashMap<String, String> getInfoToFill(String nomeIngrediente){
        HashMap<String, String> info = new HashMap<>();
        Ingrediente ingrediente = catalogoIngredienti.getIngredienteByName(nomeIngrediente);
        if(ingrediente != null){
            info.put("lotto_ingrediente", ingrediente.getNumeroLotto());
            info.put("data_scadenza", ingrediente.getScadenza());
            info.put("note_ingrediente", ingrediente.getNote());
        }
        return info;
    }

    public int removeIngredient(String nomeIngrediente){
        int res = 0;
        if (nomeIngrediente.matches("")){
            res = 3;
        }else {
            Ingrediente ingrediente = catalogoIngredienti.getIngredienteByName(nomeIngrediente);
            res = catalogoIngredienti.rimuoviIngredienteDalCatagolo(ingrediente);
        }
        return res;
    }

    public String[] getNomiIngredienti(){
        List<String> names = this.catalogoIngredienti.getNomiIngredienti();
        String[] nomi = new String[names.size()];
        for (int i=0; i<names.size(); i++){
            nomi[i]=names.get(i);
        }
        return nomi;
    }
}
