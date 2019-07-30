package com.example.lucad.schedelotti.Model;

public class DescrizioneIngrediente {

    private String nome = null;
    private String descrizione = null;

    public DescrizioneIngrediente(String nome, String descrizione){
        this.nome = nome;
        this.descrizione = descrizione;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(!(obj instanceof DescrizioneIngrediente)){
            return false;
        }
        DescrizioneIngrediente descrizioneIngrediente = (DescrizioneIngrediente) obj;
        return descrizioneIngrediente.descrizione.equals(this.descrizione) && descrizioneIngrediente.nome.equals(this.nome);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.descrizione.hashCode();
        result = 31 * result + this.nome.hashCode();
        return result;
    }
}
