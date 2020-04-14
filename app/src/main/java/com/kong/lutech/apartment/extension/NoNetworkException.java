package com.kong.lutech.apartment.extension;

import java.io.IOException;

/**
 * Created by gimdonghyeog on 2017. 7. 27..
 */

public class NoNetworkException extends IOException {
    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }

    public NoNetworkException() {
        super("No network connected");

    }
}
