package com.skrypchenko.telegramtest.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skrypchenko.telegramtest.R;
import com.skrypchenko.telegramtest.data.TdMessageObj;
import com.skrypchenko.telegramtest.ui.home.GroupsAdapter;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<TdMessageObj> list = new ArrayList<>();
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder;
        switch (viewType){
            case TdMessageObj.INCOMING:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.incoming_item_msg, parent, false);
                holder = new IncomingViewHolder(view);
                break;
            case TdMessageObj.OUTGOING:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.outgoing_item_msg, parent, false);
                holder = new OutgoingViewHolder(view);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }

        return holder;
    }


    @Override
    public int getItemViewType(int position) {
        return list.get(position).getType();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof IncomingViewHolder){
                ((IncomingViewHolder)holder).bind(list.get(position));
            }
            if(holder instanceof OutgoingViewHolder){
                ((OutgoingViewHolder)holder).bind(list.get(position));
            }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateListData(ArrayList<TdMessageObj> input){
        list=input;
        notifyDataSetChanged();
    }


    public class IncomingViewHolder extends RecyclerView.ViewHolder  {
        private TextView tvMessage;
        public IncomingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }
        public void bind(TdMessageObj messageObj){
            tvMessage.setText(messageObj.getMessage());
        }
    }

    public class OutgoingViewHolder extends RecyclerView.ViewHolder  {
        private TextView tvMessage;
        public OutgoingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }
        public void bind(TdMessageObj messageObj){
            tvMessage.setText(messageObj.getMessage());
        }
    }
}
