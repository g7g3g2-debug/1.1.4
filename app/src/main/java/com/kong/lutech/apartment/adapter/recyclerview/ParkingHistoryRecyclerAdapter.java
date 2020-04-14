package com.kong.lutech.apartment.adapter.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kong.lutech.apartment.adapter.recyclerview.viewholder.ParkingHistoryViewHolder;
import com.kongtech.lutech.apartment.R;
import com.kong.lutech.apartment.model.CctvLog;
import com.kong.lutech.apartment.view.RecyclerItemView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by gimdonghyeog on 2017. 5. 25..
 */

public class ParkingHistoryRecyclerAdapter extends RecyclerView.Adapter<ParkingHistoryViewHolder> {

    private Context context;
    private List<CctvLog> items;

    public void addItems(List<CctvLog> items) {
        final int addStart = this.items.size();
        final int addEnd = this.items.size() + items.size();

        this.items.addAll(items);
        notifyItemRangeInserted(addStart, addEnd);
    }


    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void setItems(List<CctvLog> cctvLogs) {
        items = cctvLogs;
        notifyDataSetChanged();
    }

    public ParkingHistoryRecyclerAdapter(Context context) {
        this.context = context;
        items = new ArrayList<>();
    }

    @Override
    public ParkingHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_parking_history, parent, false);
        final ParkingHistoryViewHolder viewHolder = new ParkingHistoryViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ParkingHistoryViewHolder holder, int position) {
        final RecyclerItemView itemView = holder.getRecyclerItemView();
        final CctvLog ITEM = items.get(position);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd  a hh:mm", Locale.ENGLISH);
        itemView.setValue("주차 하셨습니다.", ITEM.getParkName() + ", " + ITEM.getCctvName(), dateFormat.format(ITEM.getDate()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
