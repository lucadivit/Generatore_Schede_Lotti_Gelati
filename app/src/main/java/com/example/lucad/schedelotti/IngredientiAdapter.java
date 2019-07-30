package com.example.lucad.schedelotti;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class IngredientiAdapter extends RecyclerView.Adapter<IngredientiAdapter.IngredientiViewHolder>{

    private List<String> nomiIngredienti;

    public static class IngredientiViewHolder extends RecyclerView.ViewHolder{

        public TextView textView;

        public IngredientiViewHolder(View v){
            super(v);
            this.textView = (TextView) v.findViewById(R.id.ingrediente_recycler_view);
        }
    }

    public IngredientiAdapter(List<String> nomiIngredienti){
        this.nomiIngredienti = nomiIngredienti;
    }

    @NonNull
    @Override
    public IngredientiAdapter.IngredientiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredienti_recycler_view, parent, false);
        IngredientiViewHolder ingredientiViewHolder = new IngredientiViewHolder(v);
        return ingredientiViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientiViewHolder holder, int position) {
        holder.textView.setText(this.nomiIngredienti.get(position));
    }

    @Override
    public int getItemCount() {
        return this.nomiIngredienti.size();
    }

}
