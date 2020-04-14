package com.kong.lutech.apartment.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by gimdonghyeog on 2017. 5. 23..
 */

public class Notice implements Parcelable{
    public static final int STATE_UNREAD = 0;
    public static final int STATE_READ = 1;

    private int noticeId;
    private int sequence;
    private String title;
    private String content;
    private Date createdDate;

    // 0 : unread, 1: read
    private int state;

    protected Notice(Parcel in) {
        noticeId = in.readInt();
        sequence = in.readInt();
        title = in.readString();
        content = in.readString();
        createdDate = new Date(in.readLong());
        state = in.readInt();
    }

    public static final Creator<Notice> CREATOR = new Creator<Notice>() {
        @Override
        public Notice createFromParcel(Parcel in) {
            return new Notice(in);
        }

        @Override
        public Notice[] newArray(int size) {
            return new Notice[size];
        }
    };

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(int noticeId) {
        this.noticeId = noticeId;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Notice(int noticeId, int sequence, String title, String content, Date createdDate) {
        this.noticeId = noticeId;
        this.sequence = sequence;
        this.title = title;
        this.content = content;
        this.createdDate = createdDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(noticeId);
        dest.writeInt(sequence);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeLong(createdDate.getTime());
        dest.writeInt(state);
    }

    @Override
    public boolean equals(Object obj) {
        return ((Notice)obj).getNoticeId() == noticeId;
    }
}
