package com.kong.lutech.apartment.ui.notice.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kong.lutech.apartment.Config;
import com.kongtech.lutech.apartment.R;
import com.kong.lutech.apartment.model.Mobile;
import com.kong.lutech.apartment.model.Notice;
import com.kong.lutech.apartment.model.NoticeDetail;
import com.kong.lutech.apartment.network.api.apartment.ApartmentRestUtil;
import com.kong.lutech.apartment.ui.BaseActivity;
import com.kong.lutech.apartment.utils.sqlite.DBManager;

import java.text.SimpleDateFormat;


public class NoticeDetailActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvTitle, tvWriter, tvCreatedDate, tvContent;
    private LinearLayout llPrevWrite, llNextWrite;
    private TextView tvPrevTitle, tvNextTitle;

    private Notice notice;

    private Notice prev, next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        homeButtonEnabled();

        tvTitle = (TextView)findViewById(R.id.tvTitle);
        tvWriter = (TextView)findViewById(R.id.tvWriter);
        tvCreatedDate = (TextView)findViewById(R.id.tvCreatedDate);
        tvContent = (TextView)findViewById(R.id.tvContent);
        llPrevWrite = (LinearLayout)findViewById(R.id.llPrevWrite);
        llNextWrite = (LinearLayout)findViewById(R.id.llNextWrite);
        tvPrevTitle = (TextView)findViewById(R.id.tvPrevTitle);
        tvNextTitle = (TextView)findViewById(R.id.tvNextTitle);
        llPrevWrite.setOnClickListener(this);
        llNextWrite.setOnClickListener(this);

        final Intent intent = getIntent();
        notice = intent.getParcelableExtra("Notice");

        loadWriting(notice.getSequence());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.llPrevWrite) {
            loadWriting(prev.getSequence());
        } else if (v.getId() == R.id.llNextWrite) {
            loadWriting(next.getSequence());
        }
    }

    private void loadWriting(int noticeId) {
        if (Config.Check()) {
            final Mobile mobile = Config.getMobile();
            ApartmentRestUtil.getInstance(getApplicationContext(), "yyyy.MM.dd")
                    .getNoticeDetail(Config.getAccessToken(), mobile.getApartmentId(), noticeId)
                    .subscribe(noticeDetailResponse -> {
                        if (noticeDetailResponse.isSuccessful()) {
                            final NoticeDetail noticeDetail = noticeDetailResponse.body();
                            final Notice item = noticeDetail.getNotice();

                            tvTitle.setText(item.getTitle());

                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
                            tvCreatedDate.setText("등록일 : " + dateFormat.format(item.getCreatedDate()));
                            tvContent.setText(item.getContent());

                            prev = noticeDetail.getPrev();
                            next = noticeDetail.getNext();

                            if (prev != null) {
                                tvPrevTitle.setText(prev.getTitle());
                                llPrevWrite.setEnabled(true);
                            } else {
                                tvPrevTitle.setText("없습니다.");
                                llPrevWrite.setEnabled(false);
                            }

                            if (next != null) {
                                tvNextTitle.setText(next.getTitle());
                                llNextWrite.setEnabled(true);
                            } else {
                                tvNextTitle.setText("없습니다.");
                                llNextWrite.setEnabled(false);
                            }

                            DBManager dbManager = new DBManager(getApplicationContext()).open();
                            dbManager.insertReadNotice(item.getNoticeId());
                            if(dbManager.isExistNotice(item.getNoticeId())) {
                                dbManager.deleteNotice(item.getNoticeId());
                            }
                            dbManager.close();
                        } else {

                        }
                    }, Throwable::printStackTrace);
        }
    }
}
