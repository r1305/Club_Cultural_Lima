package com.example.julian.clubculturallima;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Julian on 22/09/2016.
 */
public class ActivityRecyclerAdapter extends RecyclerView.Adapter<ActivityRecyclerAdapter.ViewHolder>{

    List<JSONObject> l =new ArrayList<>();
    Context c;
    View.OnClickListener listener;

    public ActivityRecyclerAdapter(List<JSONObject> l) {
        this.l = l;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        c=parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity,parent,false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final JSONObject o=l.get(position);
        holder.activity.setText((String)o.get("nombre"));
        holder.fecha.setText((String)o.get("fecha"));
        holder.itemView.setTag(o);
        holder.itemView.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        if(l==null){
            return 0;
        }else {
            return l.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView activity,fecha;

        public ViewHolder(View itemView) {
            super(itemView);
            activity=(TextView)itemView.findViewById(R.id.item_act);
            fecha=(TextView)itemView.findViewById(R.id.item_act_fecha);
        }

        @Override
        public void onClick(View view) {

        }
    }

    public void setOnClickListener(View.OnClickListener listener){this.listener=listener;}
}
