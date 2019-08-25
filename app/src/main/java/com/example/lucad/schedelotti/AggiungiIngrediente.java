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
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.lucad.schedelotti.Controller.AggiungiIngredienteHandler;
import java.util.HashMap;
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AggiungiIngrediente.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AggiungiIngrediente#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AggiungiIngrediente extends Fragment {

    private OnFragmentInteractionListener mListener;
    private AutoCompleteTextView text_nome_ingrediente = null;
    private EditText text_scadenza_lotto_ingrediente = null;
    private EditText text_lotto_ingrediente = null;
    private EditText text_nota_ingrediente = null;
    private Button btn_add_ingredient = null;
    private Button btn_rm_ingredient = null;
    private AggiungiIngredienteHandler aggiungiIngredienteHandler = null;
    private Vibrator vibrator;
    private View view;
    public static final String INGREDIENTE_AGGIUNTO_INTENT = "INGREDIENTE_AGGIUNTO";
    public static final String INGREDIENTE_RIMOSSO_INTENT = "INGREDIENTE_RIMOSSO";

    public AggiungiIngrediente() {
        // Required empty public constructor

    }

    public static AggiungiIngrediente newInstance(String param1, String param2) {
        AggiungiIngrediente fragment = new AggiungiIngrediente();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aggiungi_ingrediente, container, false);
        return view;
    }

    public void initializeButton(){
        this.btn_add_ingredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                int res = 0;
                String nomeIngrediente = text_nome_ingrediente.getText().toString();
                String lottoIngrediente = text_lotto_ingrediente.getText().toString();
                String scadenzaLottoIngrediente = text_scadenza_lotto_ingrediente.getText().toString();
                String notaIngrediente = text_nota_ingrediente.getText().toString();
                if(lottoIngrediente.matches("") || nomeIngrediente.matches("") || scadenzaLottoIngrediente.matches("")){
                    vibrator.vibrate(100);
                    Toast.makeText(context, context.getString(R.string.ingrediente_non_aggiunto_form), Toast.LENGTH_SHORT).show();
                }else {
                    if(notaIngrediente.matches("")){
                        notaIngrediente = "";
                    }
                    if(nomeIngrediente.length()<4){
                        res = 6;
                    }else {
                        res = aggiungiIngredienteHandler.aggiungiIngrediente(nomeIngrediente,lottoIngrediente,scadenzaLottoIngrediente,notaIngrediente);
                    }
                    switch (res){
                        case 0:
                            vibrator.vibrate(100);
                            Toast.makeText(context, context.getString(R.string.errore_aggiunta_ingrediente_form), Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            vibrator.vibrate(300);
                            Toast.makeText(context, context.getString(R.string.ingrediente_aggiunto_form), Toast.LENGTH_SHORT).show();
                            text_lotto_ingrediente.setText("");
                            text_nome_ingrediente.setText("");
                            text_nota_ingrediente.setText("");
                            text_scadenza_lotto_ingrediente.setText("");
                            Intent intent = new Intent(INGREDIENTE_AGGIUNTO_INTENT);
                            context.sendBroadcast(intent);
                            refreshAutocomplete();
                            break;
                        case 2:
                            vibrator.vibrate(100);
                            Toast.makeText(context, context.getString(R.string.errore_modifica_ingrediente), Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            vibrator.vibrate(300);
                            Toast.makeText(context, context.getString(R.string.modifica_ingrediente), Toast.LENGTH_SHORT).show();
                            text_lotto_ingrediente.setText("");
                            text_nome_ingrediente.setText("");
                            text_nota_ingrediente.setText("");
                            text_scadenza_lotto_ingrediente.setText("");
                            break;
                        case 4:
                            vibrator.vibrate(100);
                            Toast.makeText(context, context.getString(R.string.ingrediente_non_aggiunto_form), Toast.LENGTH_SHORT).show();
                            break;
                        case 5:
                            Toast.makeText(context, context.getString(R.string.invalid_date), Toast.LENGTH_SHORT).show();
                            vibrator.vibrate(100);
                            break;
                        case 6:
                            vibrator.vibrate(100);
                            Toast.makeText(context, context.getString(R.string.nome_corto), Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        });

        this.btn_rm_ingredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = v.getContext();
                final String nomeIngrediente = text_nome_ingrediente.getText().toString();
                final View view = v;
                if(nomeIngrediente.matches("")){
                    vibrator.vibrate(100);
                    Toast.makeText(context, context.getString(R.string.select_ingredient), Toast.LENGTH_SHORT).show();
                }else {
                    String[] ricette = aggiungiIngredienteHandler.ricetteCoinvolte(nomeIngrediente);
                    if(ricette.length == 0){
                        rimuoviIngrediente(nomeIngrediente, view, context);
                    }else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);
                        builder.setTitle(R.string.dialog_del_ingrediente_title);
                        builder.setIcon(android.R.drawable.ic_dialog_alert);
                        builder.setItems(ricette, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.setPositiveButton(R.string.dialog_del_db_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                rimuoviIngrediente(nomeIngrediente, view, context);
                            }
                        });
                        builder.setNegativeButton(R.string.dialog_del_db_stop, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                        builder.create();
                        builder.show();
                    }
                }
            }
        });
    }

    private void rimuoviIngrediente(String nomeIngrediente, View view, Context context){
        int res = aggiungiIngredienteHandler.removeIngredient(nomeIngrediente);
        switch (res){
            case 0:
                break;
            case 1:
                vibrator.vibrate(100);
                Toast.makeText(context, context.getString(R.string.rimozione_ingredienti_sistema_errore), Toast.LENGTH_SHORT).show();
                break;
            case 2:
                vibrator.vibrate(300);
                Toast.makeText(context, context.getString(R.string.rimozione_ingredienti_sistema), Toast.LENGTH_SHORT).show();
                text_lotto_ingrediente.setText("");
                text_nome_ingrediente.setText("");
                text_nota_ingrediente.setText("");
                text_scadenza_lotto_ingrediente.setText("");
                Intent intent = new Intent(INGREDIENTE_RIMOSSO_INTENT);
                context.sendBroadcast(intent);
                refreshAutocomplete();
                break;
            case 3:
                vibrator.vibrate(100);
                Toast.makeText(context, context.getString(R.string.select_ingredient), Toast.LENGTH_SHORT).show();
            case 4:
                break;
        }
    }

    public void refreshAutocomplete(){
        final String[] nomiIngredientiAutocomplete = this.aggiungiIngredienteHandler.getNomiIngredienti();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(),android.R.layout.select_dialog_item,nomiIngredientiAutocomplete);
        this.text_nome_ingrediente.setAdapter(adapter);
    }

    public void initializeAutocomplete(){
        this.text_nome_ingrediente.setThreshold(2);
        this.text_nome_ingrediente.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> info = aggiungiIngredienteHandler.getInfoToFill(parent.getItemAtPosition(position).toString());
                text_scadenza_lotto_ingrediente.setText(info.get("data_scadenza"));
                text_lotto_ingrediente.setText(info.get("lotto_ingrediente"));
                String note = info.get("note_ingrediente");
                if (!note.matches("")){
                    text_nota_ingrediente.setText(note);
                }
            }
        });
        refreshAutocomplete();
    }

    private void initializeBroadcastReceiver(Context context){
        IntentFilter intentFilter = new IntentFilter(AggiungiIngrediente.INGREDIENTE_RIMOSSO_INTENT);
        IntentFilter intentFilter1 = new IntentFilter(AggiungiIngrediente.INGREDIENTE_AGGIUNTO_INTENT);
        context.registerReceiver(broadcastReceiver, intentFilter);
        context.registerReceiver(broadcastReceiver, intentFilter1);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case AggiungiIngrediente.INGREDIENTE_RIMOSSO_INTENT:
                    refreshAutocomplete();
                    break;
                case AggiungiIngrediente.INGREDIENTE_AGGIUNTO_INTENT:
                    refreshAutocomplete();
                    break;
            }
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = view.getContext();
        final Context ctx = context;
        this.view = view;
        aggiungiIngredienteHandler = AggiungiIngredienteHandler.getInstance(context);
        this.text_lotto_ingrediente = (EditText) view.findViewById(R.id.lotto_ingrediente_form);
        this.text_nome_ingrediente = (AutoCompleteTextView) view.findViewById(R.id.nome_ingrediente_form);
        this.text_scadenza_lotto_ingrediente = (EditText) view.findViewById(R.id.data_scadenza_form);
        this.text_nota_ingrediente = (EditText) view.findViewById(R.id.notes_form);
        this.btn_add_ingredient = (Button) view.findViewById(R.id.button_add_ingredient);
        this.btn_rm_ingredient = (Button) view.findViewById(R.id.btn_remove_ingredient);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        initializeButton();
        initializeAutocomplete();
        initializeBroadcastReceiver(context);
        view.setOnTouchListener(new OnSwipeTouchListener(context){

            @Override
            public void onSwipeRight() {
                Intent intent = new Intent(OnSwipeTouchListener.SWIPE_RIGHT);
                ctx.sendBroadcast(intent);
            }

            @Override
            public void onSwipeLeft() {
                Intent intent = new Intent(OnSwipeTouchListener.SWIPE_LEFT);
                ctx.sendBroadcast(intent);
            }
        });
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //Questo funziona con il modo di mostrare le view commentato nella main activity. Viene richiamato ad ogni reattach
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
