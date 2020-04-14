package com.kong.lutech.apartment.adapter.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.kong.lutech.apartment.ui.parking.ParkingCarFragment;
import com.kong.lutech.apartment.model.Car;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gimdonghyeog on 12/11/2018.
 * KDH
 */
public class ParkingCarPagerAdapter extends FragmentStatePagerAdapter implements ParkingCarFragment.OnParkingCarFragmentListener {
    public interface OnParkingCarClickListener {
        void onCarDetailClick(Car car);
        void onCarParkClick(Car car);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    private final int accentColor[] = {0xff41515b, 0xff5c6b76, 0xff93a2a9};

    private List<ParkingCarFragment> fragments = new ArrayList<>();

    public ParkingCarPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setParkingCars(List<Car> cars) {
        fragments.clear();
        notifyDataSetChanged();

        for ( int i = 0 ; i < cars.size() ; i ++ ) {
            final Car car = cars.get(i);

            fragments.add(ParkingCarFragment.newInstance(this, i, car, this.accentColor[i % 3]));
        }

        notifyDataSetChanged();
    }


    private ParkingCarPagerAdapter.OnParkingCarClickListener onParkingCarClickListener;
    public void setOnParkingCarClickListener(OnParkingCarClickListener onParkingCarClickListener) {
        this.onParkingCarClickListener = onParkingCarClickListener;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }


    @Override
    public void onParkingCarDetailClick(int index, Car car) {
        if (onParkingCarClickListener != null) onParkingCarClickListener.onCarDetailClick(car);
    }

    @Override
    public void onParkingCarParkClick(int index, Car car) {
        if (onParkingCarClickListener != null) onParkingCarClickListener.onCarParkClick(car);
    }
}
