package com.example.lucad.schedelotti;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

public class IngredientiAdapterWithCheckbox extends RecyclerView.Adapter<IngredientiAdapterWithCheckbox.IngredientiViewHolder>{

    public static class IngredientiViewHolder extends RecyclerView.ViewHolder{
        //public TextView textView;
        //public CheckBox checkBox;
        public CheckedTextView checkedTextView;
        public View view;
        private List<String> nomiIngredienti;
        private Boolean[] booleans;
        private SparseBooleanArray sparseBooleanArray;

        public IngredientiViewHolder(View v){
            super(v);

            this.checkedTextView = v.findViewById(R.id.checked_text_view_ing);
            //this.textView = (TextView) v.findViewById(R.id.ingrediente_recycler_view);
            //this.checkBox = (CheckBox) v.findViewById(R.id.ingrediente_recycler_view_cb);

            this.checkedTextView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_CANCEL){
                        int pos = getAdapterPosition();
                        if(!(checkedTextView.isChecked() == sparseBooleanArray.get(pos))){
                            sparseBooleanArray.put(pos, checkedTextView.isChecked());
                        }
                    }
                    return true;
                }
            });
        }

        public void bind(int position){
            checkedTextView.setText(nomiIngredienti.get(position));
            checkedTextView.setChecked(sparseBooleanArray.get(position));
        }

        public void setNomiIngredienti(List<String> nomiIngredienti){
            this.nomiIngredienti = nomiIngredienti;
        }

        public void setSparseBooleanArray(SparseBooleanArray sparseBooleanArray){
            this.sparseBooleanArray = sparseBooleanArray;
        }
    }

    private List<String> nomiIngredienti;
    private SparseBooleanArray sparseBooleanArray;
    private IngredientiViewHolder ingredientiViewHolder;


    public IngredientiAdapterWithCheckbox(List<String> nomiIngredienti, SparseBooleanArray sparseBooleanArray){
        this.nomiIngredienti = nomiIngredienti;
        this.sparseBooleanArray = sparseBooleanArray;
    }

    @NonNull
    @Override
    public IngredientiAdapterWithCheckbox.IngredientiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredienti_recycler_view_with_checkboxes, parent, false);
        IngredientiViewHolder ingredientiViewHolder = new IngredientiViewHolder(v);
        this.ingredientiViewHolder = ingredientiViewHolder;
        ingredientiViewHolder.setNomiIngredienti(this.nomiIngredienti);
        ingredientiViewHolder.setSparseBooleanArray(this.sparseBooleanArray);
        return ingredientiViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientiViewHolder holder, int position) {
        //holder.checkedTextView.setText(this.nomiIngredienti.get(position));
        holder.bind(position);
        //holder.textView.setText(this.nomiIngredienti.get(position));
    }

    @Override
    public int getItemCount() {
        return this.nomiIngredienti.size();
    }
}
