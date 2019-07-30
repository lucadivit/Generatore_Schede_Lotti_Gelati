package com.example.lucad.schedelotti.Model;

import java.util.Date;

public class DescrizioneRicetta {

    String nomeRicetta = new String();
    String note = new String();

    public DescrizioneRicetta(){

    }

    public DescrizioneRicetta(String nomeRicetta, String note){
        this.nomeRicetta = nomeRicetta;
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setNomeRicetta(String nomeRicetta) {
        this.nomeRicetta = nomeRicetta;
    }

    public String getNomeRicetta() {
        return nomeRicetta;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(!(obj instanceof DescrizioneRicetta)){
            return false;
        }
        DescrizioneRicetta descrizioneRicetta = (DescrizioneRicetta) obj;
        return descrizioneRicetta.note.equals(this.note) && descrizioneRicetta.nomeRicetta.equals(this.nomeRicetta);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.note.hashCode();
        result = 31 * result + this.nomeRicetta.hashCode();
        return result;
    }
}
