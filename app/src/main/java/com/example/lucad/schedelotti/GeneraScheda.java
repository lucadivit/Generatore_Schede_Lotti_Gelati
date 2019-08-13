package com.example.lucad.schedelotti;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GeneraScheda.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GeneraScheda#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GeneraScheda extends Fragment{

    private final Fragment fragment_genera_scheda_ingredienti = new IngredientiFragment();
    private final Fragment fragment_genera_scheda_ricette = new RicetteFragment();
    private FragmentActivity fa = null;
    private FragmentManager fm = null;
    Fragment active_items = null;
    TabLayout tabLayout = null;
    public static String ACTIVE_FRAGMENT_RICETTE = "GENERA_RICETTE";
    public static String ACTIVE_FRAGMENT_INGREDIENTI = "GENERA_INGREDIENTI";
    public static String ACTIVE_FRAGMENT = "";


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public GeneraScheda() {
        // Required empty public constructor
    }

    public static GeneraScheda newInstance(String param1, String param2) {
        GeneraScheda fragment = new GeneraScheda();
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
        View view = inflater.inflate(R.layout.fragment_genera_scheda, container, false);
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fa = (FragmentActivity) context;
        fm = fa.getSupportFragmentManager();
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TabLayout tabLayout = (TabLayout) getView().findViewById(R.id.genera_scheda_tab_items);
        tabLayout.setOnTabSelectedListener(onTabSelectedListener);

        active_items = fragment_genera_scheda_ricette;
        ACTIVE_FRAGMENT = ACTIVE_FRAGMENT_RICETTE;
        //Tabs
        fm.beginTransaction().add(R.id.genera_scheda_fragment, fragment_genera_scheda_ricette, "2").commit();
        fm.beginTransaction().add(R.id.genera_scheda_fragment, fragment_genera_scheda_ingredienti, "1").hide(fragment_genera_scheda_ingredienti).commit();
        super.onViewCreated(view, savedInstanceState);
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

    private TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            switch (tab.getPosition()){
                case 0:
                    fm.beginTransaction().hide(active_items).show(fragment_genera_scheda_ricette).commit();
                    active_items = fragment_genera_scheda_ricette;
                    ACTIVE_FRAGMENT = ACTIVE_FRAGMENT_RICETTE;
                    break;
                case 1:
                    fm.beginTransaction().hide(active_items).show(fragment_genera_scheda_ingredienti).commit();
                    active_items = fragment_genera_scheda_ingredienti;
                    ACTIVE_FRAGMENT = ACTIVE_FRAGMENT_INGREDIENTI;
                    break;
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };
}
