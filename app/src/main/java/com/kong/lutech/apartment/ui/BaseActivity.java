package com.kong.lutech.apartment.ui;

import com.kongtech.lutech.apartment.R;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

/**
 * Created by gimdonghyeog on 2017. 5. 24..
 */

public class BaseActivity extends RxAppCompatActivity {

    public void homeButtonEnabled() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.toolbar_indicator);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
}
