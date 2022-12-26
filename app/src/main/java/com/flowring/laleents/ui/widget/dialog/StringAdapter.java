package com.flowring.laleents.ui.widget.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flowring.laleents.R;

public class StringAdapter extends RecyclerView.Adapter<StringAdapter.StringViewHolder> {
    public String[] list;
    private final Context m_context;

    private final LayoutInflater m_layoutInflater;
    private OnRecyclerViewClickListener listener;


    public StringAdapter(Context context, String[] m_photoList, OnRecyclerViewClickListener listener) {
        m_context = context;
        m_layoutInflater = LayoutInflater.from(m_context);
        this.list = m_photoList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public StringViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = m_layoutInflater.inflate(R.layout.string_item, viewGroup, false);
        StringViewHolder holder = new StringViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull StringViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.name.setText(list[position]);

        if (listener != null) {
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClickListener(list[position]);
                }
            });


        }


    }

    @Override
    public int getItemCount() {

        return list.length;
    }


    public interface OnRecyclerViewClickListener {
        void onItemClickListener(String albumIteam);
    }

    public class StringViewHolder extends RecyclerView.ViewHolder {
        public View view;

        public TextView name;

        public StringViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;


            name = view.findViewById(R.id.name);
        }
    }
}
