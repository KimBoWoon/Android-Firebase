package com.android.practice.firebase_database;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Null on 2017-10-07.
 */

interface ItemClickListener {
    void onItemClick(int position);
}

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemClickListener {
    private ArrayList<ChatDTO> items;
    private Context context;

    public ChatAdapter(Context context, ArrayList<ChatDTO> items) {
        this.context = context;
        this.items = items;
    }

    public String getItemID(int position) {
        return items.get(position).getId();
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(context, getItemID(position), Toast.LENGTH_SHORT).show();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        holder = new UsersViewHolder(v, this);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder == null) {
            return;
        }

        ((UsersViewHolder) holder).id.setText("ID : " + items.get(position).getId());
        ((UsersViewHolder) holder).msg.setText("Msg : " + items.get(position).getMsg());
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    static class UsersViewHolder extends RecyclerView.ViewHolder {
        private TextView id;
        private TextView msg;

        public UsersViewHolder(View itemView, final ItemClickListener itemClickListener) {
            super(itemView);
            this.id = (TextView) itemView.findViewById(R.id.list_item_id);
            this.msg = (TextView) itemView.findViewById(R.id.list_item_msg);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}
