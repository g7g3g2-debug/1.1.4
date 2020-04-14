package com.kong.lutech.apartment.model.list;

import com.kong.lutech.apartment.model.Notice;
import com.kong.lutech.apartment.model.PageInfo;

import java.util.ArrayList;

/**
 * Created by gimdonghyeog on 2017. 5. 24..
 */

public class NoticeList {
    private ArrayList<Notice> notices;
    private PageInfo pageInfo;


    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public ArrayList<Notice> getNotices() {
        return notices;
    }

    public void setNotices(ArrayList<Notice> notices) {
        this.notices = notices;
    }
}
