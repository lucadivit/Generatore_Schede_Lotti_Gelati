package com.example.lucad.schedelotti;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lucad.schedelotti.Controller.CreaSchedaHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class IngredientiFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private CreaSchedaHandler creaSchedaHandler = null;
    private List<String> listaNomiIngredienti = null;
    private Button button_generate = null;
    private Button button_clean = null;

    Vibrator vibrator;

    private OnFragmentInteractionListener mListener;

    public IngredientiFragment() {
        // Required empty public constructor
    }

    public static IngredientiFragment newInstance(String param1, String param2) {
        IngredientiFragment fragment = new IngredientiFragment();
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
        return inflater.inflate(R.layout.fragment_ingredienti, container, false);
    }

    private void initializeBroadcastReceiver(Context context){
        IntentFilter intentFilter = new IntentFilter(AggiungiIngrediente.INGREDIENTE_AGGIUNTO_INTENT);
        IntentFilter intentFilter1 = new IntentFilter(AggiungiIngrediente.INGREDIENTE_RIMOSSO_INTENT);
        context.registerReceiver(broadcastReceiver, intentFilter);
        context.registerReceiver(broadcastReceiver, intentFilter1);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case AggiungiIngrediente.INGREDIENTE_RIMOSSO_INTENT:
                    refreshIngredients();
                    break;
                case AggiungiIngrediente.INGREDIENTE_AGGIUNTO_INTENT:
                    refreshIngredients();
                    break;
            }

        }
    };

    public void refreshIngredients(){
        listaNomiIngredienti = creaSchedaHandler.getNomiIngredienti();
        //adapter = new IngredientiAdapter(listaNomiIngredienti);
        SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
        Iterator iterator = this.listaNomiIngredienti.iterator();
        int i = 0;
        while (iterator.hasNext()){
            iterator.next();
            sparseBooleanArray.put(i, false);
            i++;
        }
        adapter = new IngredientiAdapterWithCheckbox(listaNomiIngredienti, sparseBooleanArray);
        recyclerView.setAdapter(adapter);
    }

    public void initializeButton(){

        this.button_generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText input = new EditText(getContext());
                final View view1 = view;
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
                builder.setTitle(R.string.dialog_nome_ricetta);
                builder.setIcon(android.R.drawable.ic_dialog_info);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton(R.string.dialog_del_db_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String nome_ricetta = String.valueOf(input.getText()).toLowerCase();
                        if(nome_ricetta.matches("")){
                            nome_ricetta = "";
                        }
                        switch (creaSchedaHandler.generaSchedaIngredienti(nome_ricetta)){
                            case 0:
                                vibrator.vibrate(100);
                                Toast.makeText(view1.getContext(), view1.getContext().getString(R.string.scheda_non_generata), Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                vibrator.vibrate(300);
                                Toast.makeText(view1.getContext(), view1.getContext().getString(R.string.scheda_generata), Toast.LENGTH_SHORT).show();
                                refreshIngredients();
                                break;
                            case 2:
                                vibrator.vibrate(100);
                                Toast.makeText(view1.getContext(), view1.getContext().getString(R.string.isset_scheda), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
                builder.setNegativeButton(R.string.dialog_del_db_stop, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                builder.create();
                builder.show();
            }
        });

        this.button_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(creaSchedaHandler.rimuoviIngredientiDaScheda()){
                    refreshIngredients();
                    Toast.makeText(view.getContext(), view.getContext().getString(R.string.rimozione_ingredienti), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void initializeRecyclerView(Context context){
        final Context ctx = context;
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setOnTouchListener(new OnSwipeTouchListener(MainActivity.getContext()){
            @Override
            public void onSwipeLeft() {
                Intent intent = new Intent(OnSwipeTouchListener.SWIPE_LEFT);
                ctx.sendBroadcast(intent);
            }

            @Override
            public void onSwipeRight() {
                Intent intent = new Intent(OnSwipeTouchListener.SWIPE_RIGHT);
                ctx.sendBroadcast(intent);
            }
        });
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), 1);
        recyclerView.addItemDecoration(dividerItemDecoration);
        refreshIngredients();
        List<Integer> ids = new ArrayList<>();
        ids.add(R.id.checked_text_view_ing);
        recyclerView.addOnItemTouchListener(new RecyclerViewCheckBox(context, recyclerView, new RecyclerViewCheckBox.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, ArrayList<Object> itemViews) {
                String nomeIngrediente = listaNomiIngredienti.get(position);
                CheckedTextView checkedTextView = (CheckedTextView) itemViews.get(0);
                checkedTextView.toggle();
                if(checkedTextView.isChecked()){
                    switch (creaSchedaHandler.aggiungiIngredienteAScheda(nomeIngrediente)){
                        case 0:
                            Toast.makeText(view.getContext(), nomeIngrediente + " " + view.getContext().getString(R.string.ingrediente_non_aggiunto), Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            vibrator.vibrate(300);
                            Toast.makeText(view.getContext(), view.getContext().getString(R.string.ingrediente_aggiunto) + " " + nomeIngrediente, Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(view.getContext(), nomeIngrediente + " " + view.getContext().getString(R.string.ingrediente_non_aggiunto), Toast.LENGTH_SHORT).show();
                            break;
                    }
                }else {
                    switch (creaSchedaHandler.rimuoviIngredienteDaScheda(nomeIngrediente)){
                        case 0:
                            Toast.makeText(view.getContext(), nomeIngrediente + " " + view.getContext().getString(R.string.ingrediente_non_rimosso), Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            vibrator.vibrate(100);
                            Toast.makeText(view.getContext(), nomeIngrediente + " " + view.getContext().getString(R.string.ingrediente_rimosso), Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Log.d("Ingrediente", "Ingrediente non rimosso");
                            break;
                    }
                }
            }
            @Override
            public void onLongItemClick(View view, int position, ArrayList<Object> itemViews) {

            }
        }, ids));
        /*
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String nomeIngrediente = listaNomiIngredienti.get(position);
                switch (creaSchedaHandler.rimuoviIngredienteDaScheda(nomeIngrediente)){
                    case 0:
                        Toast.makeText(view.getContext(), nomeIngrediente + " " + view.getContext().getString(R.string.ingrediente_non_rimosso), Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        vibrator.vibrate(100);
                        Toast.makeText(view.getContext(), nomeIngrediente + " " + view.getContext().getString(R.string.ingrediente_rimosso), Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        break;
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {
                String nomeIngrediente = listaNomiIngredienti.get(position);
                if(creaSchedaHandler.aggiungiIngredienteAScheda(nomeIngrediente)){
                    vibrator.vibrate(300);
                    Toast.makeText(view.getContext(), view.getContext().getString(R.string.ingrediente_aggiunto) + " " + nomeIngrediente, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(view.getContext(), nomeIngrediente + " " + view.getContext().getString(R.string.ingrediente_non_aggiunto), Toast.LENGTH_SHORT).show();
                }
            }
        }));*/
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = view.getContext();
        creaSchedaHandler = CreaSchedaHandler.getInstance(context);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        this.button_generate = (Button) view.findViewById(R.id.genera_scheda_btn);
        this.button_clean = (Button) view.findViewById(R.id.elimina_ingredienti_scheda_btn);
        recyclerView = (RecyclerView) getView().findViewById(R.id.lista_ingredienti_scheda);
        initializeButton();
        initializeRecyclerView(context);
        initializeBroadcastReceiver(context);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
