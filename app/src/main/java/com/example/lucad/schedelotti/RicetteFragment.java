package com.example.lucad.schedelotti;

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
import android.widget.Toast;
import com.example.lucad.schedelotti.Controller.CreaSchedaHandler;

import java.util.List;


public class RicetteFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerViewRicette;
    private RecyclerView.Adapter adapterRicette;
    private RecyclerView.LayoutManager layoutManagerRicette;
    private CreaSchedaHandler creaSchedaHandler = null;
    private List<String> listaNomiRicette = null;
    private Vibrator vibrator;

    public RicetteFragment() {
        // Required empty public constructor
    }

    public static RicetteFragment newInstance(String param1, String param2) {
        RicetteFragment fragment = new RicetteFragment();
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
        return inflater.inflate(R.layout.fragment_ricette, container, false);
    }

    private void initializeBroadcastReceiver(Context context){
        IntentFilter intentFilter = new IntentFilter(AggiungiRicetta.RICETTA_AGGIUNTA_INTENT);
        IntentFilter intentFilter1 = new IntentFilter(AggiungiRicetta.RICETTA_RIMOSSA_INTENT);
        IntentFilter intentFilter2 = new IntentFilter(AggiungiIngrediente.INGREDIENTE_RIMOSSO_INTENT);
        context.registerReceiver(broadcastReceiver, intentFilter);
        context.registerReceiver(broadcastReceiver, intentFilter1);
        context.registerReceiver(broadcastReceiver, intentFilter2);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case AggiungiRicetta.RICETTA_AGGIUNTA_INTENT:
                    refreshRecipes();
                    break;
                case AggiungiRicetta.RICETTA_RIMOSSA_INTENT:
                    refreshRecipes();
                    break;
                case AggiungiIngrediente.INGREDIENTE_RIMOSSO_INTENT:
                    refreshRecipes();
                    break;
            }
        }
    };

    public void refreshRecipes(){
        listaNomiRicette = creaSchedaHandler.getNomiRicette();
        adapterRicette = new RicetteAdapter(listaNomiRicette);
        recyclerViewRicette.setAdapter(adapterRicette);
    }

    public void initializeRecyclerView(final Context context){
        recyclerViewRicette.setHasFixedSize(false);

        layoutManagerRicette = new LinearLayoutManager(context);
        recyclerViewRicette.setLayoutManager(layoutManagerRicette);

        recyclerViewRicette.setOnTouchListener(new OnSwipeTouchListener(MainActivity.getContext()){
            @Override
            public void onSwipeLeft() {
                Intent intent = new Intent(OnSwipeTouchListener.SWIPE_LEFT);
                context.sendBroadcast(intent);
            }
        });

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewRicette.getContext(), 1);
        recyclerViewRicette.addItemDecoration(dividerItemDecoration);

        refreshRecipes();

        recyclerViewRicette.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerViewRicette, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String nomeRicetta = listaNomiRicette.get(position);
                int res = creaSchedaHandler.generaSchedaRicetta(nomeRicetta);
                switch (res){
                    case 0:
                        vibrator.vibrate(100);
                        Toast.makeText(view.getContext(), view.getContext().getString(R.string.scheda_non_generata), Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        vibrator.vibrate(300);
                        Toast.makeText(view.getContext(), view.getContext().getString(R.string.scheda_generata) +": " + nomeRicetta, Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        vibrator.vibrate(100);
                        Toast.makeText(view.getContext(), view.getContext().getString(R.string.isset_scheda), Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {
                return;
            }
        }));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Context context = view.getContext();
        this.creaSchedaHandler =  CreaSchedaHandler.getInstance(context);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        recyclerViewRicette = (RecyclerView) getView().findViewById(R.id.lista_ricette_scheda);
        initializeBroadcastReceiver(context);
        initializeRecyclerView(context);
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
