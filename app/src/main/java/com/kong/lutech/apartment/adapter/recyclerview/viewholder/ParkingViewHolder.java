package com.kong.lutech.apartment.adapter.recyclerview.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kongtech.lutech.apartment.R;
import com.kong.lutech.apartment.utils.DrawableUtil;

/**
 * Created by gimdonghyeog on 2017. 5. 24..
 */

public class ParkingViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener{

    private OnParkingHolderClickListener onParkingHolderClickListener;
    public void setOnParkingHolderClickListener(OnParkingHolderClickListener onParkingHolderClickListener) {
        this.onParkingHolderClickListener = onParkingHolderClickListener;
    }

    private LinearLayout llInfoContainer;
    private TextView tvCarNumber, tvParkingDate, tvParkingLocation;
    private Button btnParkingAction, btnDetail;

    public ParkingViewHolder(View itemView) {
        super(itemView);

        llInfoContainer = (LinearLayout)itemView.findViewById(R.id.llInfoContainer);
        tvCarNumber = (TextView)itemView.findViewById(R.id.item_parkinginfo_tvCarNumber);
        tvParkingDate = (TextView)itemView.findViewById(R.id.item_parkinginfo_tvParkingDate);
        tvParkingLocation = (TextView) itemView.findViewById(R.id.item_parkinginfo_tvParkingLocation);
        btnParkingAction = (Button)itemView.findViewById(R.id.btnParkingAction);
        btnDetail = (Button)itemView.findViewById(R.id.btnDetail);

        btnParkingAction.setOnClickListener(this);
        btnDetail.setOnClickListener(this);

        DrawableUtil.SetBackgroundDrawable(btnDetail, 0, 0xffffffff);
        DrawableUtil.SetBackgroundDrawable(btnParkingAction, 0, 0xff41cae1);
    }

    public Button getBtnParkingAction() {
        return btnParkingAction;
    }

    public Button getBtnDetail() {
        return btnDetail;
    }

    public LinearLayout getLlInfoContainer() {
        return llInfoContainer;
    }

    public TextView getTvCarNumber() {
        return tvCarNumber;
    }

    public TextView getTvParkingDate() {
        return tvParkingDate;
    }

    public TextView getTvParkingLocation() {
        return tvParkingLocation;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnDetail:
                if (onParkingHolderClickListener != null) onParkingHolderClickListener.onCarDetailClick(view, getAdapterPosition());
                break;
            case R.id.btnParkingAction:
                if (onParkingHolderClickListener != null) onParkingHolderClickListener.onCarParkClick(view, getAdapterPosition());
        }
    }

    public interface OnParkingHolderClickListener {
        void onCarDetailClick(View view, int position);
        void onCarParkClick(View view, int position);
    }
}
