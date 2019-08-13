package com.example.lucad.schedelotti;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.lucad.schedelotti.Controller.AggiungiRicettaHandler;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AggiungiRicetta.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AggiungiRicetta#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AggiungiRicetta extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private AggiungiRicettaHandler aggiungiRicettaHandler = null;
    private AutoCompleteTextView text_nome_ricetta = null;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<String> listaNomiIngredienti = null;
    private Button genera_btn = null;
    private Button remove_btn = null;
    private Vibrator vibrator;
    private View view;
    public static final String RICETTA_AGGIUNTA_INTENT = "RICETTA_AGGIUNTA";
    public static final String RICETTA_RIMOSSA_INTENT = "RICETTA_RIMOSSA";

    public AggiungiRicetta() {
        // Required empty public constructor
    }

    public static AggiungiRicetta newInstance(String param1, String param2) {
        AggiungiRicetta fragment = new AggiungiRicetta();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_aggiungi_ricetta, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void initializeBroadcastReceiver(Context context){
        IntentFilter intentFilter = new IntentFilter(AggiungiIngrediente.INGREDIENTE_AGGIUNTO_INTENT);
        IntentFilter intentFilter1= new IntentFilter(AggiungiIngrediente.INGREDIENTE_RIMOSSO_INTENT);
        context.registerReceiver(broadcastReceiver, intentFilter);
        context.registerReceiver(broadcastReceiver, intentFilter1);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case AggiungiIngrediente.INGREDIENTE_AGGIUNTO_INTENT:
                    refreshIngredients();
                    break;
                case AggiungiIngrediente.INGREDIENTE_RIMOSSO_INTENT:
                    refreshIngredients();
                    refreshAutocomplete(view);
                    break;
            }
        }
    };

    private void refreshIngredients(){
        this.listaNomiIngredienti = aggiungiRicettaHandler.getNomiIngredienti();
        this.adapter = new IngredientiAdapter(this.listaNomiIngredienti);
        this.recyclerView.setAdapter(this.adapter);
    }

    private void initializeButton(){
        this.remove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                int res = 0;
                String nomeRicetta = text_nome_ricetta.getText().toString();
                res = aggiungiRicettaHandler.rimuoviRicetta(text_nome_ricetta.getText().toString());
                switch (res){
                    case 0:
                        vibrator.vibrate(100);
                        Toast.makeText(v.getContext(), nomeRicetta + " " + v.getContext().getString(R.string.not_rm_recipe), Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        vibrator.vibrate(300);
                        Toast.makeText(v.getContext(), nomeRicetta + " " + v.getContext().getString(R.string.rm_recipe), Toast.LENGTH_SHORT).show();
                        text_nome_ricetta.setText("");
                        Intent intent = new Intent(RICETTA_RIMOSSA_INTENT);
                        context.sendBroadcast(intent);
                        refreshAutocomplete(v);
                        break;
                    case 5:
                        vibrator.vibrate(100);
                        Toast.makeText(v.getContext(), v.getContext().getString(R.string.ricetta_non_selezionata), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        this.genera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int res;
                Context context = v.getContext();
                String nomeRicetta = text_nome_ricetta.getText().toString();
                if(nomeRicetta.matches("")){
                    res = 6;
                }else {
                    res = aggiungiRicettaHandler.aggiungiRicetta(text_nome_ricetta.getText().toString());
                }
                switch (res){
                    case 0:
                        vibrator.vibrate(100);
                        Toast.makeText(context, context.getString(R.string.add_recipe_error), Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        vibrator.vibrate(300);
                        Toast.makeText(context, context.getString(R.string.add_recipe), Toast.LENGTH_SHORT).show();
                        text_nome_ricetta.setText("");
                        Intent intent = new Intent(RICETTA_AGGIUNTA_INTENT);
                        context.sendBroadcast(intent);
                        refreshAutocomplete(v);
                        break;
                    case 2:
                        vibrator.vibrate(100);
                        Toast.makeText(context, context.getString(R.string.modify_recipe_error), Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        vibrator.vibrate(300);
                        Toast.makeText(context, context.getString(R.string.modify_recipe), Toast.LENGTH_SHORT).show();
                        text_nome_ricetta.setText("");
                        break;
                    case 4:
                        vibrator.vibrate(100);
                        Toast.makeText(context, context.getString(R.string.exists_recipe), Toast.LENGTH_SHORT).show();
                        break;
                    case 5:
                        vibrator.vibrate(100);
                        Toast.makeText(context, context.getString(R.string.add_ing_to_recipe), Toast.LENGTH_SHORT).show();
                        break;
                    case 6:
                        vibrator.vibrate(100);
                        Toast.makeText(context, context.getString(R.string.ingrediente_non_aggiunto_form), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void initializeRecyclerView(Context context){
        this.recyclerView.setHasFixedSize(false);
        this.layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        this.recyclerView.setLayoutManager(this.layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this.recyclerView.getContext(), 1);
        recyclerView.addItemDecoration(dividerItemDecoration);
        this.recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, this.recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String nomeIngrediente = listaNomiIngredienti.get(position);
                switch (aggiungiRicettaHandler.rimuoviIngredienteRicetta(nomeIngrediente)){
                    case 0:
                        break;
                    case 1:
                        vibrator.vibrate(100);
                        Toast.makeText(view.getContext(), nomeIngrediente + " " + view.getContext().getString(R.string.ingrediente_rimosso), Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(view.getContext(), nomeIngrediente + " " + view.getContext().getString(R.string.ingrediente_non_rimosso), Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {
                String nomeIngrediente = listaNomiIngredienti.get(position);
                if(aggiungiRicettaHandler.aggiungiIngredienteARicetta(nomeIngrediente)){
                    vibrator.vibrate(300);
                    Toast.makeText(view.getContext(), view.getContext().getString(R.string.ingrediente_aggiunto) + " " + nomeIngrediente, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(view.getContext(), nomeIngrediente + " " + view.getContext().getString(R.string.ingrediente_non_aggiunto), Toast.LENGTH_SHORT).show();
                }
            }
        }));
        refreshIngredients();
    }

    private void initializeAutocomplete(View view){
        this.text_nome_ricetta.setThreshold(2);
        this.text_nome_ricetta.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("ricetta", parent.getItemAtPosition(position).toString());
            }
        });
        refreshAutocomplete(view);
    }

    private void refreshAutocomplete(View view){
        final String[] nomiRicetteAutocomplete = aggiungiRicettaHandler.getNomiRicette();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(),android.R.layout.select_dialog_item, nomiRicetteAutocomplete);
        this.text_nome_ricetta.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Context context = view.getContext();
        initializeBroadcastReceiver(context);
        this.view = view;
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        aggiungiRicettaHandler = AggiungiRicettaHandler.getInstance(context);
        this.text_nome_ricetta = (AutoCompleteTextView) view.findViewById(R.id.aggiungi_ricetta_autocomplete);
        this.genera_btn = (Button) view.findViewById(R.id.btn_add_recipe);
        this.remove_btn = (Button) view.findViewById(R.id.btn_remove_recipe);
        this.recyclerView = (RecyclerView) getView().findViewById(R.id.aggiungi_ricetta_ingredienti);
        initializeButton();
        initializeRecyclerView(context);
        initializeAutocomplete(view);
        recyclerView.setOnTouchListener(new OnSwipeTouchListener(context){
            @Override
            public void onSwipeLeft() {
                Intent intent = new Intent(OnSwipeTouchListener.SWIPE_LEFT);
                context.sendBroadcast(intent);
            }

            @Override
            public void onSwipeRight() {
                Intent intent = new Intent(OnSwipeTouchListener.SWIPE_RIGHT);
                context.sendBroadcast(intent);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
