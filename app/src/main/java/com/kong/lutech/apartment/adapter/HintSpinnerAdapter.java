package com.kong.lutech.apartment.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gimdonghyeog on 19/11/2018.
 * KDH
 */
public class HintSpinnerAdapter<T> extends ArrayAdapter {

    private static <T> List<Object> make(String hint, List<T> objects) {
        List<Object> array = new ArrayList<>();
        array.add(hint);
        if (objects != null && objects.size() > 0) {
            array.addAll(objects);
        }
        return array;
    }

    public HintSpinnerAdapter(@NonNull Context context, int resource, String hint, @NonNull List<T> objects) {
        super(context, resource, HintSpinnerAdapter.make(hint, objects));
    }


    @Override
    public boolean isEnabled(int position) {
        return position != 0;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View view = super.getDropDownView(position, convertView, parent);

        final TextView tv = (TextView) view;
        tv.setTextColor(position != 0 ? 0xff293744 : 0x4c000000);

        return tv;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View view = super.getView(position, convertView, parent);

        final TextView tv = (TextView) view;
        tv.setTextColor(position != 0 ? 0xff293744 : 0x4c000000);

        return tv;
    }

}
