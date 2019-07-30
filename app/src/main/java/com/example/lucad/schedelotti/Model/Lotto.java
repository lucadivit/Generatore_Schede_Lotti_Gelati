package com.example.lucad.schedelotti.Model;

import android.util.Log;

import java.util.Date;

public class Lotto {

    private String numeroLotto = null;
    private String scadenza = null;

    public Lotto(String numeroLotto, String scadenza){
        this.numeroLotto = numeroLotto;
        this.scadenza = scadenza;
    }

    public String getNumeroLotto() {
        return numeroLotto;
    }

    public void setNumeroLotto(String numeroLotto) {
        this.numeroLotto = numeroLotto;
    }

    public String getScadenza() {
        return scadenza;
    }

    public void setScadenza(String scadenza) {
        this.scadenza = scadenza;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Lotto)) {
            return false;
        }
        Lotto lotto = (Lotto) obj;
        Log.d("Scadenza", String.valueOf(lotto.scadenza.equals(this.scadenza)));
        return lotto.numeroLotto == this.numeroLotto && lotto.scadenza.equals(this.scadenza);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.numeroLotto.hashCode();
        result = 31 * result + this.scadenza.hashCode();
        return result;
    }
}
