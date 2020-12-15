package com.skrypchenko.telegramtest.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skrypchenko.telegramtest.R;
import com.skrypchenko.telegramtest.data.TdChatObj;

import org.drinkless.td.libcore.telegram.TdApi;

import java.util.ArrayList;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.MyViewHolder> {
    ArrayList<TdChatObj>list = new ArrayList<>();

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.groups_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView idItemText;

        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            idItemText = (TextView) view.findViewById(R.id.idItemText);
        }

        public void bind(TdChatObj chat){
            idItemText.setText(chat.getTitle());
        }

        @Override
        public void onClick(View v) {
            if(onItemClickListener!=null){
                onItemClickListener.onItemClick(list.get(getAdapterPosition()));
            }
        }
    }

    public void updateListData(ArrayList<TdChatObj> list){
        this.list=list;
        notifyDataSetChanged();
    }

    private OnItemClickListener onItemClickListener;
    public interface OnItemClickListener{
        public void onItemClick(TdChatObj chat);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}