package com.example.lucad.schedelotti;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lucad.schedelotti.Model.Ricetta;

import java.util.ArrayList;
import java.util.List;

public class RicetteAdapter extends RecyclerView.Adapter<RicetteAdapter.RicetteViewHolder> {

    private List<String> nomiRicette;

    @NonNull
    @Override
    public RicetteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ricette_recycler_view, parent, false);
        RicetteViewHolder ricetteViewHolder = new RicetteAdapter.RicetteViewHolder(v);
        return ricetteViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RicetteViewHolder holder, int position) {
        holder.textView.setText(this.nomiRicette.get(position));
    }

    public RicetteAdapter(List<String> listaNomiRicette){
        this.nomiRicette = listaNomiRicette;
    }

    @Override
    public int getItemCount() {
        return this.nomiRicette.size();
    }

    public static class RicetteViewHolder extends RecyclerView.ViewHolder{

        public TextView textView;

        public RicetteViewHolder(View v){
            super(v);
            this.textView = (TextView) v.findViewById(R.id.ricetta_recycler_view);
        }
    }

}
