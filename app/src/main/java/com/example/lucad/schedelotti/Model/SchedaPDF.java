package com.example.lucad.schedelotti.Model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public abstract class SchedaPDF implements Scheda {
    protected String nomeFile = "";
    protected String title = "";
    private int pageWidth = 595;
    private int pageHeight = 842;

    public SchedaPDF(String nomeSchedaFile){
        this.setNomeFile(nomeSchedaFile);
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public int getPageWidth() {
        return pageWidth;
    }

    @Override
    public int getPageHeight() {
        return pageHeight;
    }

    @Override
    public void setPageHeight(int pageHeight) {
        this.pageHeight = pageHeight;
    }

    @Override
    public void setPageWidth(int pageWidth) {
        this.pageWidth = pageWidth;
    }

    @Override
    public void setNomeFile(String nomeFile) {
        if(!nomeFile.endsWith(".pdf")){
            nomeFile = nomeFile + ".pdf";
        }
        this.nomeFile = nomeFile;
    }

    @Override
    public String getNomeFile() {
        return nomeFile;
    }

    @Override
    public int generaScheda(){
        int res = 0;
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(this.getPageWidth(), this.getPageHeight(), 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        this.draw(canvas);
        pdfDocument.finishPage(page);
        try {
            File filePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), this.getNomeFile());
            int i = 1;
            while (filePath.exists()){
                String nuovoNome = "(" + String.valueOf(i) + ")" + this.getNomeFile();
                filePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), nuovoNome);
                i = i + 1;
            }
            try{
                if(filePath.createNewFile()){
                    pdfDocument.writeTo(new FileOutputStream(filePath));
                    pdfDocument.close();
                    res = 1;
                }else {
                    res = 0;
                }
            }catch (Exception e){
                res = 0;
                Log.d("ErroreFile", "ex", e);
            }
        }catch (Exception e){
            res = 0;
            Log.d("ErroreFile", "ex", e);
        }
        this.cleanScheda();
        return res;
    }

    @Override
    public abstract boolean addItem(Object o);

    @Override
    public abstract boolean removeItem(Object o);

    @Override
    public abstract boolean checkItem(Object o);

    @Override
    public abstract boolean cleanScheda();

    @Override
    public abstract void draw(Object o);

}
