package com.kong.lutech.apartment.model.list;

import com.kong.lutech.apartment.model.CctvLog;

import java.util.ArrayList;

/**
 * Created by gimdonghyeog on 2017. 5. 25..
 */

public class CctvLogList {
    private String message;
    private ArrayList<CctvLog> cctvLogs;
    private PageInfo pageInfo;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<CctvLog> getCctvLogs() {
        return cctvLogs;
    }

    public void setCctvLogs(ArrayList<CctvLog> cctvLogs) {
        this.cctvLogs = cctvLogs;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public class PageInfo {
        private int page;
        private int size;
        private int totalPageCount;
        private int totalItemCount;

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getTotalPageCount() {
            return totalPageCount;
        }

        public void setTotalPageCount(int totalPageCount) {
            this.totalPageCount = totalPageCount;
        }

        public int getTotalItemCount() {
            return totalItemCount;
        }

        public void setTotlItemCount(int totlItemCount) {
            this.totalItemCount = totlItemCount;
        }
    }
}
