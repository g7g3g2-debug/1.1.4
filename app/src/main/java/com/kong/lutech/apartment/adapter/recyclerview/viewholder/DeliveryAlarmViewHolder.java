package com.kong.lutech.apartment.adapter.recyclerview.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kongtech.lutech.apartment.R;
import com.kong.lutech.apartment.view.RecyclerItemView;

/**
 * Created by gimdonghyeog on 2017. 5. 24..
 */

public class DeliveryAlarmViewHolder extends RecyclerView.ViewHolder {

    private RecyclerItemView recyclerItemView;

    public DeliveryAlarmViewHolder(View itemView) {
        super(itemView);

        recyclerItemView = (RecyclerItemView)itemView.findViewById(R.id.item_deliveryHistory);
    }

    public RecyclerItemView getRecyclerItemView() {
        return recyclerItemView;
    }
}
