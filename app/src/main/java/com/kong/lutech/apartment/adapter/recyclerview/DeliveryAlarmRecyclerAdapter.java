package com.kong.lutech.apartment.adapter.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kong.lutech.apartment.adapter.recyclerview.viewholder.DeliveryAlarmViewHolder;
import com.kongtech.lutech.apartment.R;
import com.kong.lutech.apartment.model.Delivery;
import com.kong.lutech.apartment.view.RecyclerItemView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by gimdonghyeog on 2017. 5. 24..
 */

public class DeliveryAlarmRecyclerAdapter extends RecyclerView.Adapter<DeliveryAlarmViewHolder> {

    private List<Delivery> items;
    private OnDeliveryClickListener onDeliveryClickListener;

    public DeliveryAlarmRecyclerAdapter(OnDeliveryClickListener onDeliveryClickListener) {
        items = new ArrayList<>();
        this.onDeliveryClickListener = onDeliveryClickListener;
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void addNewItems(List<Delivery> newItems) {
        int index = 0;
        for (int i = 0 ; i < newItems.size() ; i ++) {
            final Delivery item = newItems.get(i);
            if (!items.contains(item)) {
                items.add(index, item);
                notifyItemInserted(index);
            }
        }
    }

    public Delivery getPositionItem(int position) {
        return items.get(position);
    }

    public void setItems(List<Delivery> deliveries) {
        items = deliveries;
        notifyDataSetChanged();
    }

    public void addItems(List<Delivery> deliveries) {
        int addStart = items.size();
        int addEnd = addStart + deliveries.size();
        items.addAll(deliveries);
        notifyItemRangeChanged(addStart, addEnd);
    }

    @Override
    public DeliveryAlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_delivery_alarm, parent, false);

        return new DeliveryAlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DeliveryAlarmViewHolder holder, int position) {
        if (position == 0) Log.e("onBindViewHolder", "onBindViewHolder");
        final RecyclerItemView itemView = holder.getRecyclerItemView();
        final Delivery ITEM = items.get(position);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd  a hh:mm", Locale.ENGLISH);

        if (ITEM.getStatus() == 0) {
            itemView.setValue("택배가 경비실에 보관 중 입니다.", null, "수신 시간 : " + dateFormat.format(ITEM.getReceiveDate()), true);
        } else {
            itemView.setValue("택배를 수령 하였습니다.", null, "수령 시간 : " + dateFormat.format(ITEM.getSendDate()), false);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnDeliveryClickListener {
        void onDeliveryClick(Delivery delivery);
    }
}
