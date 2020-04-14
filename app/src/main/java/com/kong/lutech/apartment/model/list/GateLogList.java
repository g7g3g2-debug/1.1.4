package com.kong.lutech.apartment.model.list;

import com.kong.lutech.apartment.model.GateLog;
import com.kong.lutech.apartment.model.PageInfo;

import java.util.List;

/**
 * Created by gimdonghyeog on 2017. 5. 24..
 */

public class GateLogList {
    private List<GateLog> gateLogs;
    private PageInfo pageInfo;

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public List<GateLog> getGateLogs() {
        return gateLogs;
    }

    public void setGateLogs(List<GateLog> gateLogs) {
        this.gateLogs = gateLogs;
    }
}
