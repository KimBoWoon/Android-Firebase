package com.example.anull.firebase.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anull.firebase.R;
import com.example.anull.firebase.data.ChatDTO;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Null on 2016-11-21.
 */

public class ChatAdapter extends BaseAdapter {
    private ViewHolder holder;
    private ArrayList<ChatDTO> chatDTOs;
    private LayoutInflater inflater;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public ChatAdapter(Context context, ArrayList<ChatDTO> chatDTOs, ImageLoader imageLoader, DisplayImageOptions options) {
        this.chatDTOs = chatDTOs;
        this.inflater = LayoutInflater.from(context);
        this.imageLoader = imageLoader;
        this.options = options;
    }

    @Override
    public int getCount() {
        return chatDTOs.size();
    }

    @Override
    public ChatDTO getItem(int position) {
        return chatDTOs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_cell, parent, false);

            holder = new ViewHolder();
            convertView.setTag(holder);

            holder.chatText = (TextView) convertView.findViewById(R.id.chatText);
            holder.chatID = (TextView) convertView.findViewById(R.id.chatID);
            holder.chatImg = (ImageView) convertView.findViewById(R.id.chatImg);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 세팅
        ChatDTO cd = getItem(position);
        if (cd.getMsg().indexOf("http") >= 0) {
            // 이미지 처리
            holder.chatImg.setVisibility(View.VISIBLE);
            holder.chatText.setVisibility(View.GONE);
            imageLoader.displayImage(cd.getMsg(), holder.chatImg, options);
        } else {
            holder.chatImg.setVisibility(View.GONE);
            holder.chatText.setVisibility(View.VISIBLE);
            holder.chatText.setText(cd.getMsg());
        }
        holder.chatID.setText(cd.getId());

        return convertView;
    }
}
