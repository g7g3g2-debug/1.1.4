package com.kong.lutech.apartment.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.kongtech.lutech.apartment.R;
import com.kong.lutech.apartment.adapter.recyclerview.FindApartRecyclerAdapter;
import com.kong.lutech.apartment.model.Apartment;
import com.kong.lutech.apartment.network.api.authentication.AuthenticationRestUtil;
import com.kong.lutech.apartment.utils.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.PublishSubject;

public class FindApartActivity extends BaseActivity implements FindApartRecyclerAdapter.OnApartClickListener {
    private String TAG = getClass().getSimpleName();

    private RecyclerView rvAparts;

    private FindApartRecyclerAdapter adapter;
    private List<Apartment> apartments;
    private PublishSubject<String> apartmentsObservable = PublishSubject.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_apart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_findapart);
        setSupportActionBar(toolbar);
        homeButtonEnabled();

        rvAparts = (RecyclerView) findViewById(R.id.rvAparts);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setSmoothScrollbarEnabled(true);
        rvAparts.setLayoutManager(layoutManager);

        adapter = new FindApartRecyclerAdapter(this);
        rvAparts.setAdapter(adapter);

        apartmentsObservable.filter(text -> {
            if (TextUtils.isEmpty(text)) {
                adapter.setItems(apartments);
                return false;
            }
            return true;
        })
                .compose(bindToLifecycle())
                .debounce(500, TimeUnit.MICROSECONDS)
                .distinctUntilChanged()
                .map(text -> {
                    final List<Apartment> filtered = new ArrayList<>();
                    for (Apartment apartment : apartments) {
                        if (apartment.getName().contains(text)) {
                            filtered.add(apartment);
                        }
                    }
                    return filtered;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aparts -> adapter.setItems(aparts));

        final String clientToken = new SharedPreferenceUtil(getApplicationContext()).getPref().getString("clientToken", "");

        AuthenticationRestUtil.getIntsance(getApplicationContext())
                .getApartmentList("Bearer " + clientToken)
                .compose(bindToLifecycle())
                .subscribe(listResponse -> {
                    if (listResponse.isSuccessful()) {
                        Log.d(TAG, "Request Success");
                        apartments = listResponse.body().getApartments();
                        adapter.setItems(apartments);
                    } else {
                        showFailLoadApartDatas();
                    }
                }, throwable -> {
                    showFailLoadApartDatas();

                    throwable.printStackTrace();
                });

    }

    private void showFailLoadApartDatas() {
        new AlertDialog.Builder(FindApartActivity.this)
                .setMessage("아파트 정보를 불러오는데 실패하였습니다.")
                .setPositiveButton("확인", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_findapart, menu);

        final MenuItem searchViewItem = menu.findItem(R.id.menu_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                apartmentsObservable.onNext(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onApartClick(Apartment apartment) {
        startActivity(
                new Intent(getApplicationContext(), LoginActivity.class)
                        .putExtra("Apartment", apartment)
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
