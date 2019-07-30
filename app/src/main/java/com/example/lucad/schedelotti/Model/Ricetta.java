package com.example.lucad.schedelotti.Model;

import java.util.ArrayList;
import java.util.List;

public class Ricetta {

    private List<Ingrediente> listaIngredienti = new ArrayList<Ingrediente>();
    private DescrizioneRicetta descrizioneRicetta = new DescrizioneRicetta();

    public Ricetta(){

    }

    public Ricetta(ArrayList<Ingrediente> listaIngredienti, DescrizioneRicetta descrizioneRicetta){
        this.listaIngredienti = listaIngredienti;
        this.descrizioneRicetta = descrizioneRicetta;
    }

    public void setListaIngredienti(List<Ingrediente> listaIngredienti) {
        this.listaIngredienti = listaIngredienti;
    }

    public List<Ingrediente> getListaIngredienti() {
        return listaIngredienti;
    }

    public boolean addIngrediente(Ingrediente ingrediente){
        return this.listaIngredienti.add(ingrediente);
    }

    public boolean removeIngrediente(Ingrediente ingrediente){
        return this.listaIngredienti.remove(ingrediente);
    }

    public DescrizioneRicetta getDescrizioneRicetta() {
        return descrizioneRicetta;
    }

    public void setDescrizioneRicetta(DescrizioneRicetta descrizioneRicetta) {
        this.descrizioneRicetta = descrizioneRicetta;
    }

    public String getNoteRicetta(){
        return this.descrizioneRicetta.getNote();
    }

    public String getNomeRicetta(){
        return this.descrizioneRicetta.getNomeRicetta();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(!(obj instanceof Ricetta)){
            return false;
        }
        Ricetta ricetta = (Ricetta) obj;
        return this.descrizioneRicetta.equals(ricetta.descrizioneRicetta) && this.listaIngredienti.equals(ricetta.listaIngredienti);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.listaIngredienti.hashCode();
        result = 31 * result + this.descrizioneRicetta.hashCode();
        return result;
    }
}
