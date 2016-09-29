package com.example.julian.clubculturallima;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Julian on 22/09/2016.
 */
public class RecomendacionesRecyclerAdapter extends RecyclerView.Adapter<RecomendacionesRecyclerAdapter.ViewHolder>{


    List<JSONObject> list=new ArrayList<>();
    View.OnClickListener listener;

    public RecomendacionesRecyclerAdapter(List<JSONObject> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reco,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        JSONObject o=list.get(position);
        holder.reco.setText(o.get("nombre").toString());
        holder.fecha.setText(o.get("fecha").toString());
        holder.itemView.setTag(o);
        holder.itemView.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        if(list==null){
            return 0;
        }else {
            return list.size();

        }
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView reco,fecha;

        public ViewHolder(View itemView) {
            super(itemView);
            reco=(TextView)itemView.findViewById(R.id.item_reco);
            fecha=(TextView)itemView.findViewById(R.id.item_reco_fecha);
            reco.setTextSize(25);

        }
    }
}
