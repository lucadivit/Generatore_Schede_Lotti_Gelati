package com.example.lucad.schedelotti;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.lucad.schedelotti.Foundation.Preferences;
import com.example.lucad.schedelotti.Model.CatalogoIngredienti;
import com.example.lucad.schedelotti.Model.CatalogoLotti;
import com.example.lucad.schedelotti.Model.CatalogoRicette;
import com.example.lucad.schedelotti.R;

public class OptionsActivity extends AppCompatActivity {

    private TextView textViewGelateria;
    private TextView textViewGelataio;
    private Button generalOptionsBtn;
    private Button eliminaLottiBtn;
    private Button generalOptionsBtnSv;
    private Button eliminaDBBtn;
    public static String GELATAIO_PREFERENCE = "nome_gelataio";
    public static String GELATERIA_PREFERENCE = "nome_gelateria";
    private Vibrator vibrator;
    private static Context context;
    private CatalogoLotti catalogoLotti;
    private CatalogoIngredienti catalogoIngredienti;
    private Button eliminaRicetteBtn;
    private CatalogoRicette catalogoRicette;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = getApplicationContext();
        catalogoLotti = CatalogoLotti.getInstance(context);
        catalogoIngredienti = CatalogoIngredienti.getInstance(context);
        catalogoRicette = CatalogoRicette.getInstance(context);
        textViewGelateria = findViewById(R.id.nome_gelateria_text);
        textViewGelataio = findViewById(R.id.nome_gelataio);
        setTextValues();
        generalOptionsBtn = findViewById(R.id.general_options_btn);
        generalOptionsBtnSv = findViewById(R.id.general_options_btn_save);
        eliminaLottiBtn = findViewById(R.id.db_options_batches_del_btn);
        eliminaDBBtn = findViewById(R.id.database_options_drop_db_btn);
        eliminaRicetteBtn = findViewById(R.id.recipes_drop_btn);
        setButtonActions();
        vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void generalOptionsSave(View view){
        String valGelataio = textViewGelataio.getText().toString();
        String valGelateria = textViewGelateria.getText().toString();
        if(valGelataio.matches("") && valGelateria.matches("")){
            Snackbar.make(view, R.string.ingrediente_non_aggiunto_form, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            vibrator.vibrate(100);
        }
        if(!valGelataio.matches("") || !valGelateria.matches("")){
            if(!valGelataio.matches("")){
                Preferences.save_string(getApplicationContext(),GELATAIO_PREFERENCE, valGelataio);
                textViewGelataio.setText(valGelataio);
            }
            if(!valGelateria.matches("")){
                Preferences.save_string(getApplicationContext(),GELATERIA_PREFERENCE, valGelateria);
                textViewGelateria.setText(valGelateria);
            }
            Snackbar.make(view, R.string.options_saved, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            vibrator.vibrate(300);
            generalOptionsBtn.setEnabled(true);
        }
    }

    private void setTextValues(){
        String gelataio = Preferences.load_string(getApplicationContext(), GELATAIO_PREFERENCE);
        String gelateria = Preferences.load_string(getApplicationContext(), GELATERIA_PREFERENCE);
        if(gelataio != null){
            this.textViewGelataio.setText(gelataio);
        }
        if(gelateria != null){
            this.textViewGelateria.setText(gelateria);
        }
    }

    private void setButtonActions(){
        String valGelataio = textViewGelataio.getText().toString();
        String valGelateria = textViewGelateria.getText().toString();
        if(valGelataio.matches("") && valGelateria.matches("")){
            generalOptionsBtn.setEnabled(false);
        }
        generalOptionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Preferences.load_string(getApplicationContext(), GELATAIO_PREFERENCE)!=null || Preferences.load_string(getApplicationContext(), GELATERIA_PREFERENCE) != null){
                    Preferences.remove(getApplicationContext(), GELATERIA_PREFERENCE);
                    Preferences.remove(getApplicationContext(), GELATAIO_PREFERENCE);
                    textViewGelataio.setText("");
                    textViewGelateria.setText("");
                    Snackbar.make(v, R.string.general_options_rm, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    vibrator.vibrate(300);
                    generalOptionsBtn.setEnabled(false);
                }
            }
        });

        eliminaRicetteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = v;
                AlertDialog.Builder builder = new AlertDialog.Builder(OptionsActivity.this, R.style.MyDialogTheme);
                builder.setPositiveButton(R.string.dialog_del_db_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int res = catalogoRicette.svuotaCatalogo();
                        Intent intent1 = new Intent(AggiungiRicetta.RICETTA_RIMOSSA_INTENT);
                        switch (res){
                            case 0:
                                Snackbar.make(view, R.string.database_deleted_error, Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                vibrator.vibrate(100);
                                context.sendBroadcast(intent1);
                                break;
                            case 1:
                                Snackbar.make(view, R.string.no_recipe_to_del, Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                vibrator.vibrate(100);
                                break;
                            case 2:
                                Snackbar.make(view, R.string.database_deleted, Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                vibrator.vibrate(300);
                                context.sendBroadcast(intent1);
                                break;
                        }
                    }
                });
                builder.setNegativeButton(R.string.dialog_del_db_stop, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                builder.setTitle(R.string.dialog_del_db_title);
                builder.setMessage(R.string.elimina_ricette_msg);
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.create();
                builder.show();
            }
        });

        generalOptionsBtnSv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generalOptionsSave(v);
            }
        });

        eliminaDBBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = v;
                AlertDialog.Builder builder = new AlertDialog.Builder(OptionsActivity.this, R.style.MyDialogTheme);
                builder.setPositiveButton(R.string.dialog_del_db_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if((catalogoIngredienti.svuotaIlCatalogo() == 0 && catalogoLotti.cancellaVecchiLottiDalCatalogo() == 0) || (catalogoIngredienti.svuotaIlCatalogo() == 1 && catalogoLotti.cancellaVecchiLottiDalCatalogo() == 0)){
                            Intent intent = new Intent(AggiungiIngrediente.INGREDIENTE_RIMOSSO_INTENT);
                            Intent intent1 = new Intent(AggiungiRicetta.RICETTA_RIMOSSA_INTENT);
                            context.sendBroadcast(intent);
                            context.sendBroadcast(intent1);
                            Snackbar.make(view, R.string.database_deleted, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            vibrator.vibrate(300);
                        }else {
                            if(catalogoIngredienti.svuotaIlCatalogo() == 1 && catalogoLotti.cancellaVecchiLottiDalCatalogo() == 1){
                                Snackbar.make(view, R.string.database_empty, Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                vibrator.vibrate(100);
                            }
                            else {
                                Snackbar.make(view, R.string.database_deleted_error, Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                vibrator.vibrate(100);
                            }
                        }
                    }
                });
                builder.setNegativeButton(R.string.dialog_del_db_stop, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                builder.setTitle(R.string.dialog_del_db_title);
                builder.setMessage(R.string.dialog_del_db_message);
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.create();
                builder.show();
            }
        });

        eliminaLottiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (catalogoLotti.cancellaVecchiLottiDalCatalogo()){
                    case 0:
                        Snackbar.make(v, R.string.database_options_batches_deleted, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        vibrator.vibrate(300);
                        break;
                    case 1:
                        Snackbar.make(v, R.string.database_options_batches_deleted_clean, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        vibrator.vibrate(100);
                        break;
                    case 3:
                        Snackbar.make(v, R.string.database_options_batches_deleted_error, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        vibrator.vibrate(100);
                        break;
                }
        }
        });
    }

    public static Context getContext(){
        return context;
    }
}
