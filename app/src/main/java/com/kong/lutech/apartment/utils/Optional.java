package com.kong.lutech.apartment.utils;

import android.support.annotation.Nullable;

import java.util.NoSuchElementException;

/**
 * Created by gimdonghyeog on 21/11/2018.
 * KDH
 */
public class Optional<M> {

    private final M optional;

    public Optional(@Nullable M optional) {
        this.optional = optional;
    }

    public boolean isEmpty() {
        return this.optional == null;
    }

    public M get() {
        if (optional == null) {
            throw new NoSuchElementException("No value present");
        }
        return optional;
    }
}
