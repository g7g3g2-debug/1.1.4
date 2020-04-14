package com.kong.lutech.apartment.adapter.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kong.lutech.apartment.adapter.recyclerview.viewholder.FindApartViewHolder;
import com.kongtech.lutech.apartment.R;
import com.kong.lutech.apartment.model.Apartment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gimdonghyeog on 2017. 5. 24..
 */

public class FindApartRecyclerAdapter extends RecyclerView.Adapter<FindApartViewHolder> implements FindApartViewHolder.OnApartHolderClickListener {

    private List<Apartment> items;
    private OnApartClickListener listener;

    public FindApartRecyclerAdapter(OnApartClickListener onApartClickListener) {
        items = new ArrayList<>();
        listener = onApartClickListener;
    }

    public void setItems(List<Apartment> apartments) {
        items = apartments;
        notifyDataSetChanged();
    }

    @Override
    public FindApartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_find_apart, parent, false);
        final FindApartViewHolder viewHolder = new FindApartViewHolder(view);
        viewHolder.setOnApartHolderClickListener(this);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FindApartViewHolder holder, int position) {
        final Apartment ITEM = items.get(position);

        holder.getTvName().setText(ITEM.getName());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onApartClick(View v, int position) {
        if(listener != null)listener.onApartClick(items.get(position));
    }

    public interface OnApartClickListener {
        void onApartClick(Apartment apartment);
    }
}
