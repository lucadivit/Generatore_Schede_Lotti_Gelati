package com.example.lucad.schedelotti.Model;

import java.util.List;

public interface Scheda {

    int generaScheda();
    boolean addItem(Object o);
    boolean removeItem(Object o);
    boolean checkItem(Object o);
    boolean cleanScheda();
    void draw(Object o);
    void setPageWidth(int pageWidth);
    void setPageHeight(int pageHeight);
    int getPageHeight();
    int getPageWidth();
    void setNomeFile(String nomeFile);
    String getNomeFile();
    void setTitle(String title);
    String getTitle();
}
