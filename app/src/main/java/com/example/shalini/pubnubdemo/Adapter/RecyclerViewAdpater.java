package com.example.shalini.pubnubdemo.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shalini.pubnubdemo.Model.ChatMessage;
import com.example.shalini.pubnubdemo.R;

import java.util.List;

/**
 * Created by shalini on 17/5/16.
 */
public class RecyclerViewAdpater extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context context;
    List<ChatMessage> messageList;

    public RecyclerViewAdpater(Context context, List<ChatMessage> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = (LayoutInflater.from(context)).inflate(R.layout.chat_layout,parent,false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ChatViewHolder){
            ChatViewHolder chatViewHolder = (ChatViewHolder) holder;
            ChatMessage chatMessage = messageList.get(position);
            String message = chatMessage.getMessage();
            boolean isPublisher = chatMessage.isPublisher();

            chatViewHolder.textviewMessage.setText(message);
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


    private class ChatViewHolder extends RecyclerView.ViewHolder {
        private final TextView textviewMessage;

        public ChatViewHolder(View view) {
            super(view);
            textviewMessage = (TextView) view.findViewById(R.id.textview_message);
        }

    }
}
