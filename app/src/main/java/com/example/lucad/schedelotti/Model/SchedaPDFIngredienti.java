package com.example.lucad.schedelotti.Model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.example.lucad.schedelotti.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class SchedaPDFIngredienti extends SchedaPDF{
    private List<Ingrediente> listaIngredientiPerScheda = new ArrayList<Ingrediente>();
    private Context context;

    public SchedaPDFIngredienti(String nomeSchedaFile, Context context) {
        super(nomeSchedaFile);
        this.context = context;
    }

    @Override
    public int generaScheda() {
        int res = 0;
        if(this.listaIngredientiPerScheda.size() > 0){
            res = super.generaScheda();
        }else {
            res = 0;
        }
        return res;
    }

    @Override
    public void draw(Object o) {
        Canvas canvas = (Canvas) o;
        int pxToStartY = 20;
        int pxToStartX = 20;

        //Nome e data sulla stesa riga
        Paint paintHeader = new Paint();
        paintHeader.setTextSize(15);
        canvas.drawText(this.getNomeFile() ,pxToStartX, pxToStartY, paintHeader);
        String dateString = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(Calendar.getInstance().getTime());
        canvas.drawText(dateString ,500, pxToStartY, paintHeader);
        pxToStartY = pxToStartY + 80;

        //Title
        Paint paintTitle = new Paint();
        paintTitle.setTextSize(30);
        paintTitle.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText(this.getTitle() ,200, pxToStartY, paintTitle);
        pxToStartY = pxToStartY + 70;

        //Firt Row
        pxToStartX = pxToStartX + 30;
        Paint paintFR = new Paint();
        paintFR.setTextSize(25);
        paintFR.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        canvas.drawText(this.context.getResources().getString(R.string.table_ingredient_name) ,pxToStartX, pxToStartY, paintFR);
        canvas.drawText(this.context.getResources().getString(R.string.table_expiry_name) ,pxToStartX + 180, pxToStartY, paintFR);
        canvas.drawText(this.context.getResources().getString(R.string.table_batch_name) ,pxToStartX + 370, pxToStartY, paintFR);

        Iterator iterator = this.listaIngredientiPerScheda.listIterator();
        Paint paintIngredienti = new Paint();
        paintIngredienti.setTextSize(20);
        while (iterator.hasNext()){
            pxToStartY = pxToStartY + 40;
            Ingrediente ingrediente = (Ingrediente) iterator.next();
            canvas.drawText(ingrediente.getNomeIngrediente() ,pxToStartX, pxToStartY, paintIngredienti);
            canvas.drawText(ingrediente.getScadenza() ,pxToStartX + 190, pxToStartY, paintIngredienti);
            canvas.drawText(ingrediente.getNumeroLotto() ,pxToStartX + 370, pxToStartY, paintIngredienti);
        }
    }

    @Override
    public boolean addItem(Object o) {
        Ingrediente ingrediente = (Ingrediente) o;
        if(this.listaIngredientiPerScheda.contains(ingrediente)){
            return false;
        }else {
            this.listaIngredientiPerScheda.add(ingrediente);
            return true;
        }
    }

    @Override
    public boolean removeItem(Object o) {
        Ingrediente ingrediente = (Ingrediente) o;
        return this.listaIngredientiPerScheda.remove(ingrediente);
    }

    @Override
    public boolean checkItem(Object o) {
        Ingrediente ingrediente = (Ingrediente) o;
        return this.listaIngredientiPerScheda.contains(ingrediente);
    }

    @Override
    public boolean cleanScheda() {
        if(this.listaIngredientiPerScheda.size() > 0){
            try{
                this.listaIngredientiPerScheda.clear();
                return true;
            }catch (Exception e){
                return false;
            }
        }else {
            return false;
        }
    }
}
