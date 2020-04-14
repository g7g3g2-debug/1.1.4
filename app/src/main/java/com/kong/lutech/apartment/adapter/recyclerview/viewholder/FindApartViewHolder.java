package com.kong.lutech.apartment.adapter.recyclerview.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kongtech.lutech.apartment.R;

/**
 * Created by gimdonghyeog on 2017. 5. 24..
 */

public class FindApartViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener{

    private LinearLayout llContainer;
    private TextView tvName;

    private OnApartHolderClickListener onApartHolderClickListener;

    public void setOnApartHolderClickListener(OnApartHolderClickListener onApartHolderClickListener) {
        this.onApartHolderClickListener = onApartHolderClickListener;
    }

    public FindApartViewHolder(View itemView) {
        super(itemView);

        llContainer = (LinearLayout)itemView.findViewById(R.id.item_findapart_llContainer);
        llContainer.setOnClickListener(this);
        tvName = (TextView)itemView.findViewById(R.id.item_findapart_tvName);
    }

    public LinearLayout getLlContainer() {
        return llContainer;
    }

    public TextView getTvName() {
        return tvName;
    }

    @Override
    public void onClick(View v) {
        if (onApartHolderClickListener != null) onApartHolderClickListener.onApartClick(v, getAdapterPosition());
    }

    public interface OnApartHolderClickListener {
        void onApartClick(View v, int position);
    }
}
