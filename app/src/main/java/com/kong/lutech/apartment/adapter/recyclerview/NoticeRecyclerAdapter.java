package com.kong.lutech.apartment.adapter.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kong.lutech.apartment.adapter.recyclerview.viewholder.NoticeViewHolder;
import com.kongtech.lutech.apartment.R;
import com.kong.lutech.apartment.model.Notice;
import com.kong.lutech.apartment.utils.sqlite.DBManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gimdonghyeog on 2017. 5. 24..
 */

public class NoticeRecyclerAdapter extends RecyclerView.Adapter<NoticeViewHolder> implements NoticeViewHolder.OnNoticeHolderClickListener{

    private Context context;

    private ArrayList<Notice> items;
    private OnNoticeClickListener onNoticeClickListener;

    public void refresh() {
        final DBManager dbManager = new DBManager(context).open();
        for (int i = 0 ; i < items.size(); i ++) {
            if (dbManager.isExistReadNotice(items.get(i).getNoticeId())) {
                items.get(i).setState(Notice.STATE_READ);
            }
        }
        notifyDataSetChanged();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void addNewItems(List<Notice> newItems) {
        int index = 0;
        for (int i = 0 ; i < newItems.size() ; i ++) {
            final Notice item = newItems.get(i);
            if (!items.contains(item)) {
                items.add(index, item);
                notifyItemInserted(index);
                i++;
            }
        }
    }

    public void addItems(List<Notice> items) {
        final DBManager dbManager = new DBManager(context).open();
        for (int i = 0 ; i < items.size(); i ++) {
            if (dbManager.isExistReadNotice(items.get(i).getNoticeId())) {
                items.get(i).setState(Notice.STATE_READ);
            }
        }

        final int addStart = this.items.size();
        final int addEnd = this.items.size() + items.size();

        this.items.addAll(items);
        notifyItemRangeInserted(addStart, addEnd);
    }

    public void setItems(ArrayList<Notice> items) {
        final DBManager dbManager = new DBManager(context).open();
        for (int i = 0 ; i < items.size(); i ++) {
            if (dbManager.isExistReadNotice(items.get(i).getNoticeId())) {
                items.get(i).setState(Notice.STATE_READ);
            }
        }
        this.items = items;
        notifyDataSetChanged();
    }

    public NoticeRecyclerAdapter(Context context, OnNoticeClickListener onNoticeClickListener){
        this.context = context;
        items = new ArrayList<>();
        this.onNoticeClickListener = onNoticeClickListener;
    }

    @Override
    public NoticeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice,  parent, false);
        final NoticeViewHolder viewHolder = new NoticeViewHolder(view);
        viewHolder.setOnNoticeHolderClickListener(this);

        return viewHolder;
    }



    @Override
    public void onBindViewHolder(NoticeViewHolder holder, int position) {
        final Notice ITEM = items.get(position);

        holder.getTvContent().setText(ITEM.getTitle());

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");

        holder.getTvDate().setText("등록일 : " + dateFormat.format(ITEM.getCreatedDate()));
        if (ITEM.getState() == Notice.STATE_UNREAD) {
            holder.getIvNoticeRead().setImageResource(R.drawable.ic_notice_new);
            holder.getTvContent().setTextColor(0xFF384D5A);
            holder.getTvDate().setTextColor(0xFF787878);
        } else {
            holder.getIvNoticeRead().setImageResource(R.drawable.ic_notice_old);
            holder.getTvContent().setTextColor(0xFFC8C8C8);
            holder.getTvDate().setTextColor(0xFFC8C8C8);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onNoticeHolderClick(View v, int position) {
        if (onNoticeClickListener != null) onNoticeClickListener.onNoticeClickListener(position, items.get(position));
    }

    public interface OnNoticeClickListener {
        void onNoticeClickListener(int position, Notice notice);
    }
}
