package com.kong.lutech.apartment.adapter.recyclerview.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kongtech.lutech.apartment.R;
import com.kong.lutech.apartment.view.RecyclerItemView;

/**
 * Created by gimdonghyeog on 2017. 5. 24..
 */

public class GateHistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private RecyclerItemView recyclerItemView;
    private OnGateHistoryHolderClickListener onGateHistoryHolderClickListener;

    public void setOnGateHistoryHolderClickListener(OnGateHistoryHolderClickListener onGateHistoryHolderClickListener) {
        this.onGateHistoryHolderClickListener = onGateHistoryHolderClickListener;
    }

    public GateHistoryViewHolder(View itemView) {
        super(itemView);

        recyclerItemView = (RecyclerItemView)itemView.findViewById(R.id.item_doorhistory);
        recyclerItemView.setOnClickListener(this);
    }

    public RecyclerItemView getRecyclerItemView() {
        return recyclerItemView;
    }

    @Override
    public void onClick(View v) {
        if (onGateHistoryHolderClickListener != null) onGateHistoryHolderClickListener.onGateHistoryHolderClick(v, getAdapterPosition());
    }

    public interface OnGateHistoryHolderClickListener {
        void onGateHistoryHolderClick(View v, int position);
    }
}
