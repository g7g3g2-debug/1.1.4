package com.kong.lutech.apartment.adapter.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kong.lutech.apartment.adapter.recyclerview.viewholder.ParkingViewHolder;
import com.kongtech.lutech.apartment.R;
import com.kong.lutech.apartment.model.Car;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by gimdonghyeog on 2017. 5. 24..
 */

public class ParkingRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ParkingViewHolder.OnParkingHolderClickListener {
    private final int accentColor[] = {0xff41515b, 0xff5c6b76, 0xff93a2a9};

    private Context mContext;
    private List<Car> items;

    public void setItems(List<Car> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    private OnParkingCarClickListener onParkingCarClickListener;

    public ParkingRecyclerAdapter(Context context, OnParkingCarClickListener onParkingCarClickListener) {
        this.mContext = context;
        items = new ArrayList<>();
        this.onParkingCarClickListener = onParkingCarClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_parking_info, parent, false);
        final ParkingViewHolder viewHolder = new ParkingViewHolder(view);
        viewHolder.setOnParkingHolderClickListener(this);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ParkingViewHolder viewHolder = (ParkingViewHolder) holder;
        ViewGroup.LayoutParams layoutParams = viewHolder.itemView.getLayoutParams();
        layoutParams.height = (int) convertDpToPixel(140, mContext);
        viewHolder.itemView.requestLayout();

        final Car ITEM = items.get(position);

        viewHolder.getTvCarNumber().setText(ITEM.getNumber());
        viewHolder.getLlInfoContainer().setBackgroundColor(accentColor[position % 3]);


        final Date parkDate = ITEM.getParkDate();

        if (parkDate != null) {
            viewHolder.getTvParkingDate().setVisibility(View.VISIBLE);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
            viewHolder.getTvParkingDate().setText(dateFormat.format(parkDate));
        } else {
            viewHolder.getTvParkingDate().setVisibility(View.GONE);
        }

        final StringBuilder content = new StringBuilder();
        if (!TextUtils.isEmpty(ITEM.getCctvName())) {
            content.append(ITEM.getCctvName());
        }
        if (!TextUtils.isEmpty(ITEM.getCctvName()) && !TextUtils.isEmpty(ITEM.getParkName())) {
            content.append("/").append(ITEM.getParkName());
        }
        viewHolder.getTvParkingLocation().setText(content.toString());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onCarDetailClick(View view, int position) {
        if (onParkingCarClickListener != null)
            onParkingCarClickListener.onCarDetailClick(items.get(position));
    }

    @Override
    public void onCarParkClick(View view, int position) {
        if (onParkingCarClickListener != null)
            onParkingCarClickListener.onCarParkClick(items.get(position));
    }

    public interface OnParkingCarClickListener {
        void onCarDetailClick(Car car);

        void onCarParkClick(Car car);
    }

    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float convertPixelsToDp(float px, Context context){
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
