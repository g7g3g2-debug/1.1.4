package com.kong.lutech.apartment.adapter.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kong.lutech.apartment.adapter.recyclerview.viewholder.GateHistoryViewHolder;
import com.kongtech.lutech.apartment.R;
import com.kong.lutech.apartment.model.GateLog;
import com.kong.lutech.apartment.view.RecyclerItemView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gimdonghyeog on 2017. 5. 24..
 */

public class GateHistoryRecyclerAdapter extends RecyclerView.Adapter<GateHistoryViewHolder> implements GateHistoryViewHolder.OnGateHistoryHolderClickListener {

    private List<GateLog> items;
    private OnGateLogClickListener onGateLogClickListener;

    public void setItems(List<GateLog> items) {
        this.items = items;
        notifyDataSetChanged();

        if(items.size() <= 0) {
            if (onGateLogClickListener != null) onGateLogClickListener.noneItemListener();
        }
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public GateHistoryRecyclerAdapter(OnGateLogClickListener onGateLogClickListener) {
        items = new ArrayList<>();
        this.onGateLogClickListener = onGateLogClickListener;
    }

    @Override
    public GateHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_door_history, parent, false);
        final GateHistoryViewHolder viewHolder = new GateHistoryViewHolder(view);
        viewHolder.setOnGateHistoryHolderClickListener(this);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(GateHistoryViewHolder holder, int position) {
        final GateLog ITEM = items.get(position);
        final RecyclerItemView itemView = holder.getRecyclerItemView();

        itemView.setValue("출입 하셨습니다.", null, ITEM.getDate());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onGateHistoryHolderClick(View v, int position) {
        if (onGateLogClickListener != null) onGateLogClickListener.onGateLogClick(items.get(position));
    }

    public interface OnGateLogClickListener {
        void onGateLogClick(GateLog gateLog);
        void noneItemListener();
    }
}
