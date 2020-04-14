package com.kong.lutech.apartment.extension;

import java.util.function.Consumer;

/**
 * Created by gimdonghyeog on 2017. 7. 27..
 */

public class NetworkCheck implements Consumer<NoNetworkException> {

    @Override
    public void accept(NoNetworkException e) {

    }
}
