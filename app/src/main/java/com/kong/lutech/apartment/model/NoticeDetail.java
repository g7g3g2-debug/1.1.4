package com.kong.lutech.apartment.model;

/**
 * Created by gimdonghyeog on 2017. 5. 26..
 */

public class NoticeDetail {
    private Notice notice;
    private Notice prev;
    private Notice next;

    public Notice getPrev() {
        return prev;
    }

    public void setPrev(Notice prev) {
        this.prev = prev;
    }

    public Notice getNext() {
        return next;
    }

    public void setNext(Notice next) {
        this.next = next;
    }

    public Notice getNotice() {
        return notice;
    }

    public void setNotice(Notice notice) {
        this.notice = notice;
    }
}
