package com.kong.lutech.apartment.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EdgeEffect;

import com.kongtech.lutech.apartment.R;

import java.lang.reflect.Field;

public class TermsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("개인정보 이용약관");
        toolbar.setTitleTextAppearance(this, R.style.ToolBarTitleStyle);

        setEdgeEffectL(findViewById(R.id.scrollTerm), ContextCompat.getColor(getApplicationContext(), R.color.background));
        findViewById(R.id.btnAgreement).setOnClickListener(view -> startActivity(new Intent(TermsActivity.this, FindApartActivity.class)));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void setEdgeEffectL(View scrollableView, int color) {
        final String[] edgeGlows = {"mEdgeGlowTop", "mEdgeGlowBottom", "mEdgeGlowLeft", "mEdgeGlowRight"};
        for (String edgeGlow : edgeGlows) {
            Class<?> clazz = scrollableView.getClass();
            while (clazz != null) {
                try {
                    final Field edgeGlowField = clazz.getDeclaredField(edgeGlow);
                    edgeGlowField.setAccessible(true);
                    final EdgeEffect edgeEffect = (EdgeEffect) edgeGlowField.get(scrollableView);
                    edgeEffect.setColor(color);
                    break;
                } catch (Exception e) {
                    clazz = clazz.getSuperclass();
                }
            }
        }
    }
}
