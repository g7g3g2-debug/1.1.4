package com.kong.lutech.apartment.ui.parking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kongtech.lutech.apartment.R;
import com.kong.lutech.apartment.model.Car;
import com.kong.lutech.apartment.utils.DrawableUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gimdonghyeog on 12/11/2018.
 * KDH
 */
public class ParkingCarFragment extends Fragment implements View.OnClickListener {

    public interface OnParkingCarFragmentListener {
        void onParkingCarDetailClick(int index, Car car);
        void onParkingCarParkClick(int index, Car car);
    }

    private Button btnDetail, btnParkingAction;

    private int index;
    private Car car;
    private int color;

    private OnParkingCarFragmentListener onParkingCarFragmentListener;
    public void setOnParkingCarFragmentListener(OnParkingCarFragmentListener onParkingCarFragmentListener) {
        this.onParkingCarFragmentListener = onParkingCarFragmentListener;
    }

    public static ParkingCarFragment newInstance(OnParkingCarFragmentListener onParkingCarFragmentListener, int index, Car car, int color) {
        ParkingCarFragment fragment = new ParkingCarFragment();

        fragment.setOnParkingCarFragmentListener(onParkingCarFragmentListener);

        Bundle args = new Bundle();
        args.putInt("INDEX", index);
        args.putParcelable("CAR", car);
        args.putInt("COLOR", color);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle arguments = getArguments();
        if (arguments != null) {
            this.index = arguments.getInt("INDEX");
            this.car = arguments.getParcelable("CAR");
            this.color = arguments.getInt("COLOR");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.item_parking_info, container, false);

        final TextView tvCarNumber = view.findViewById(R.id.item_parkinginfo_tvCarNumber);
        final TextView tvParkingDate = view.findViewById(R.id.item_parkinginfo_tvParkingDate);
        final TextView tvParkingLocation = view.findViewById(R.id.item_parkinginfo_tvParkingLocation);

        final LinearLayout llInfoContainer = view.findViewById(R.id.llInfoContainer);

        btnDetail = view.findViewById(R.id.btnDetail);
        btnParkingAction = view.findViewById(R.id.btnParkingAction);
        btnDetail.setOnClickListener(this);
        btnParkingAction.setOnClickListener(this);

        if (car != null) {
            tvCarNumber.setText(car.getNumber());
            llInfoContainer.setBackgroundColor(color);

            final Date parkDate = car.getParkDate();

            if (parkDate != null) {
                tvParkingDate.setVisibility(View.VISIBLE);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
                tvParkingDate.setText(dateFormat.format(parkDate));
            } else {
                tvParkingDate.setVisibility(View.GONE);
            }

            final StringBuilder content = new StringBuilder();
            if (!TextUtils.isEmpty(car.getCctvName())) {
                content.append(car.getCctvName());
            }
            if (!TextUtils.isEmpty(car.getCctvName()) && !TextUtils.isEmpty(car.getParkName())) {
                content.append("/").append(car.getParkName());
            }
            tvParkingLocation.setText(content.toString());
        }

        DrawableUtil.SetBackgroundDrawable(btnDetail, 0, 0xffffffff);
        DrawableUtil.SetBackgroundDrawable(btnParkingAction, 0, 0xff41cae1);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == btnDetail) { if (onParkingCarFragmentListener != null) onParkingCarFragmentListener.onParkingCarDetailClick(index, car); }
        else if (v == btnParkingAction) { if (onParkingCarFragmentListener != null) onParkingCarFragmentListener.onParkingCarParkClick(index, car); }
    }
}
