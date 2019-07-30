package com.example.lucad.schedelotti;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.lucad.schedelotti.Foundation.Preferences;

public class MainActivity extends AppCompatActivity implements GeneraScheda.OnFragmentInteractionListener, AggiungiRicetta.OnFragmentInteractionListener,
        AggiungiIngrediente.OnFragmentInteractionListener, IngredientiFragment.OnFragmentInteractionListener, RicetteFragment.OnFragmentInteractionListener {

    private final Fragment fragment_genera_scheda = new GeneraScheda();
    private final Fragment fragment_aggiungi_ingrediente = new AggiungiIngrediente();
    private final Fragment fragment_aggiungi_ricetta = new AggiungiRicetta();
    private final FragmentManager fm = getSupportFragmentManager();
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
    private BottomNavigationView navigation;
    private static Context context ;
    Fragment active_fragment = null;

    private TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    private void permessi(){
        //TODO aggiustare richieste permessi
        if (Build.VERSION.SDK_INT >= 26){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    this.finish();
                    System.exit(0);
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                }
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE)!= PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.VIBRATE)) {

                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.VIBRATE}, 100);
                }
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                }
            }
        }else {
            this.finish();
            System.exit(0);
        }
    }

    private void setupNavbar(){
        mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.genera_scheda:
                        fm.beginTransaction().hide(active_fragment).show(fragment_genera_scheda).commit();
                        //fm.beginTransaction().detach(active_fragment).attach(fragment_genera_scheda).commit();
                        /*Questo modo di cambiare fragment insieme all'override della funzione onResume permette di
                         * eseguire una callback ad ogni ripristino del fragment*/
                        active_fragment = fragment_genera_scheda;
                        return true;
                    case R.id.aggiungi_ingrediente:
                        fm.beginTransaction().hide(active_fragment).show(fragment_aggiungi_ingrediente).commit();
                        //fm.beginTransaction().detach(active_fragment).attach(fragment_aggiungi_ingrediente).commit();
                        active_fragment = fragment_aggiungi_ingrediente;
                        return true;
                    case R.id.aggiungi_ricetta:
                        fm.beginTransaction().hide(active_fragment).show(fragment_aggiungi_ricetta).commit();
                        //fm.beginTransaction().detach(active_fragment).attach(fragment_aggiungi_ricetta).commit();
                        active_fragment = fragment_aggiungi_ricetta;
                        return true;
                }
                return false;
            }
        };

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        active_fragment = fragment_genera_scheda;

        //Nav
        fm.beginTransaction().add(R.id.fragment_container, fragment_aggiungi_ricetta, "3").hide(fragment_aggiungi_ricetta).commit();
        fm.beginTransaction().add(R.id.fragment_container, fragment_aggiungi_ingrediente, "2").hide(fragment_aggiungi_ingrediente).commit();
        fm.beginTransaction().add(R.id.fragment_container, fragment_genera_scheda, "1").commit();
    }

    private void firstBoot(){
        //Queste righe tengono traccia delle preferenze. In questo caso la uso per stabilire l'avvio dell'app se è il primo o meno
        boolean firstTime = Preferences.load(context, "firstTime", true);
        if(firstTime){
            Preferences.save(this, "firstTime", false);

        }else {

        }
    }

    //TODO Autocomplete per numero lotto e per data scadenza
    //TODO Pulizia DB dell APP
    //TODO Nome Gelateria e Intestario nel PDF
    //TODO Swipe per girare i tab della navbar
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getApplicationContext();
        setContentView(R.layout.activity_main);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        permessi();
        setupNavbar();
        firstBoot();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public static Context getContext(){
        return context;
    }
}
