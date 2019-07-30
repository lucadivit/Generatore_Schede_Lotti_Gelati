package com.example.lucad.schedelotti.Model;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.example.lucad.schedelotti.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;

public class SchedaPDFRicette extends SchedaPDF{
    ArrayList<Ricetta> ricettas = new ArrayList<>();
    Context context;

    public SchedaPDFRicette(String nomeFileScheda, Context context){
        super(nomeFileScheda);
        this.context = context;
    }

    @Override
    public int generaScheda() {
        int res = 0;
        if(this.ricettas.size() > 0){
            res = super.generaScheda();
        }else {
            res = 0;
        }
        return res;
    }

    @Override
    public boolean addItem(Object o) {
        return ricettas.add((Ricetta)o);
    }

    @Override
    public boolean removeItem(Object o) {
        Ricetta ricetta = (Ricetta) o;
        return this.ricettas.remove(ricetta);
    }

    @Override
    public boolean checkItem(Object o) {
        return this.ricettas.contains((Ricetta) o);
    }

    @Override
    public boolean cleanScheda() {
        if(this.ricettas.size() > 0){
            try{
                this.ricettas.clear();
                return true;
            }catch (Exception e){
                return false;
            }
        }else {
            return false;
        }
    }

    @Override
    public void draw(Object o) {
        Canvas canvas = (Canvas) o;
        Ricetta ricetta;
        int pxToStartY = 20;
        int pxToStartX = 20;

        //Nome e data sulla stesa riga
        Paint paintHeader = new Paint();
        paintHeader.setTextSize(15);
        canvas.drawText(this.getNomeFile() ,pxToStartX, pxToStartY, paintHeader);
        String dateString = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(Calendar.getInstance().getTime());
        canvas.drawText(dateString ,500, pxToStartY, paintHeader);
        pxToStartY = pxToStartY + 80;

        Iterator iterator = this.ricettas.listIterator();
        while (iterator.hasNext()){
            ricetta = (Ricetta) iterator.next();
            //Title
            Paint paintTitle = new Paint();
            paintTitle.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            paintTitle.setTextSize(30);
            canvas.drawText(ricetta.getNomeRicetta() ,200, pxToStartY, paintTitle);
            pxToStartY = pxToStartY + 70;

            //Firt Row
            pxToStartX = pxToStartX + 30;
            Paint paintFR = new Paint();
            paintFR.setTextSize(25);
            paintFR.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

            canvas.drawText(this.context.getResources().getString(R.string.table_ingredient_name) ,pxToStartX, pxToStartY, paintFR);
            canvas.drawText(this.context.getResources().getString(R.string.table_expiry_name) ,pxToStartX + 180, pxToStartY, paintFR);
            canvas.drawText(this.context.getResources().getString(R.string.table_batch_name) ,pxToStartX + 370, pxToStartY, paintFR);

            //Ingredienti
            Paint paintIngredienti = new Paint();
            paintIngredienti.setTextSize(20);
            ArrayList<Ingrediente> ingredientes = (ArrayList<Ingrediente>) ricetta.getListaIngredienti();
            Iterator iteratorIngredienti = ingredientes.iterator();
            while (iteratorIngredienti.hasNext()){
                pxToStartY = pxToStartY + 40;
                Ingrediente ingrediente = (Ingrediente) iteratorIngredienti.next();
                canvas.drawText(ingrediente.getNomeIngrediente() ,pxToStartX, pxToStartY, paintIngredienti);
                canvas.drawText(ingrediente.getScadenza() ,pxToStartX + 190, pxToStartY, paintIngredienti);
                canvas.drawText(ingrediente.getNumeroLotto() ,pxToStartX + 370, pxToStartY, paintIngredienti);
            }
        }
    }
}
