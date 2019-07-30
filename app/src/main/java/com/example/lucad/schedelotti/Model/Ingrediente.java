package com.example.lucad.schedelotti.Model;

import java.util.Date;

public class Ingrediente {

    private Lotto lotto;
    private DescrizioneIngrediente descrizioneIngrediente;

    public Ingrediente(Lotto lotto, DescrizioneIngrediente descrizioneIngrediente){
        this.lotto = lotto;
        this.descrizioneIngrediente = descrizioneIngrediente;
    }

    public Lotto getLotto() {
        return lotto;
    }

    public void setLotto(Lotto lotto) {
        this.lotto = lotto;
    }

    public DescrizioneIngrediente getDescrizioneIngrediente() {
        return descrizioneIngrediente;
    }

    public void setDescrizioneIngrediente(DescrizioneIngrediente descrizioneIngrediente) {
        this.descrizioneIngrediente = descrizioneIngrediente;
    }

    public String getNomeIngrediente(){
        return descrizioneIngrediente.getNome();
    }

    public String getNumeroLotto(){
        return lotto.getNumeroLotto();
    }

    public String getScadenza(){
        return lotto.getScadenza();
    }

    public String getNote(){
        return descrizioneIngrediente.getDescrizione();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if(!(obj instanceof Ingrediente)){
            return false;
        }
        Ingrediente ingrediente = (Ingrediente) obj;
        return ingrediente.descrizioneIngrediente.equals(this.descrizioneIngrediente) && ingrediente.lotto.equals(this.lotto);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.lotto.hashCode();
        result = 31 * result + this.descrizioneIngrediente.hashCode();
        return result;
    }
}
