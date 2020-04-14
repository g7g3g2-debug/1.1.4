package com.kong.lutech.apartment.adapter.recyclerview.viewholder;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kongtech.lutech.apartment.R;

/**
 * Created by gimdonghyeog on 2017. 5. 24..
 */

public class NoticeViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener{

    private ImageView ivNoticeRead;
    private TextView tvContent, tvDate;
    private Button btnDetail;

    private OnNoticeHolderClickListener onNoticeHolderClickListener;

    public void setOnNoticeHolderClickListener(OnNoticeHolderClickListener onNoticeHolderClickListener) {
        this.onNoticeHolderClickListener = onNoticeHolderClickListener;
    }

    public NoticeViewHolder(View itemView) {
        super(itemView);

        ivNoticeRead = (ImageView)itemView.findViewById(R.id.item_notice_icon);
        tvContent = (TextView)itemView.findViewById(R.id.item_notice_tvContent);
        tvDate = (TextView)itemView.findViewById(R.id.item_notice_tvDate);
        btnDetail = (Button)itemView.findViewById(R.id.item_notice_btnDetail);
        btnDetail.setOnClickListener(this);
    }

    public ImageView getIvNoticeRead() {
        return ivNoticeRead;
    }

    public TextView getTvContent() {
        return tvContent;
    }

    public void setTvContent(TextView tvContent) {
        this.tvContent = tvContent;
    }

    public TextView getTvDate() {
        return tvDate;
    }

    public void setTvDate(TextView tvDate) {
        this.tvDate = tvDate;
    }

    public Button getBtnDetail() {
        return btnDetail;
    }

    public void setBtnDetail(Button btnDetail) {
        this.btnDetail = btnDetail;
    }

    @Override
    public void onClick(View v) {
        Log.d("NoticeViewHolder", "AdapterPosition : " + getAdapterPosition() + ", LayoutPosition : " + getLayoutPosition());
        if (onNoticeHolderClickListener != null) onNoticeHolderClickListener.onNoticeHolderClick(v, getAdapterPosition() );
    }

    public interface OnNoticeHolderClickListener {
        void onNoticeHolderClick(View v, int position);
    }
}
